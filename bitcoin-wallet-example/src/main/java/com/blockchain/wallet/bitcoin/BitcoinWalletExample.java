package com.blockchain.wallet.bitcoin;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bouncycastle.util.encoders.Hex;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Unspent;

public class BitcoinWalletExample {
	//bitcoin主网参数
	private static NetworkParameters parameters = MainNetParams.get();
	private static BitcoinJSONRPCClient rpcClient = null;
	private static String url = "http://user:password@127.0.0.1:8332/wallet/wallet.dat";
	
	static {
		try {
			rpcClient = new BitcoinJSONRPCClient(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException("bitcoin jsonrpc client initialization failure.");
		}
	}
	
	public static void main(String s[]) {
		BitcoinWalletExample walletExample = new BitcoinWalletExample();
		//接收方 <地址,金额>
		Map<String, BigDecimal> receiverMap = new HashMap<>();
		receiverMap.put("1JDZXL6YEfb2Ac9yk2X4h64vy9MvHSeh4Q", BigDecimal.valueOf(0.00001));
		
		//发送方 <地址,私钥>
		Map<String,String> senderMap = new HashMap<>();
		senderMap.put("1KCQHH8itLxip35xBpfNBzrnvGStJMvX67", "3214d3dc7892fefcf6ab46e648f5767a0aef972f8901623d0c358cb63e570482");
		senderMap.put("1CgfSor9797QSeZABVTtU21iyDSBkvJQwR", "611e71380626084fc82de279cc1359a44bfb6c2b26e56cd31e8b9efc379aa391");
		
		//构建交易
		Set<String> fromAddress = senderMap.keySet();
		byte[] transaction = walletExample.buildTransaction(receiverMap, fromAddress.toArray(new String[fromAddress.size()]));
		System.out.println("交易体 -> " + Hex.toHexString(transaction));
		
		//签名交易
		byte[] signedTransaction = walletExample.signTransaction(senderMap.values(), transaction);
		
		System.out.println("已签名交易体 -> " + Hex.toHexString(signedTransaction));
		
		//发送交易
		String txHash = walletExample.sendRawTransaction(signedTransaction);
		
		System.out.println("交易广播成功 -> " + txHash);
	}
	
	/**
	 * 构建交易
	 * @param toMap
	 * @param from
	 * @return
	 */
	public byte[] buildTransaction(Map<String,BigDecimal> receiverMap,String... from) {
		Transaction transaction = new Transaction(parameters);
		
		receiverMap.forEach((address,amount) -> {
			Address receiver = Address.fromString(parameters, address);
			//out_put
			transaction.addOutput(Coin.parseCoin(amount.toPlainString()), receiver);
		});
		
		List<Unspent> unspentList = rpcClient.listUnspent(1, Integer.MAX_VALUE, from);
		
		if(CollUtil.isEmpty(unspentList)) {
			throw new RuntimeException("utxo not enough");
		}
		
		//目前网络推荐的矿工费价格
		Coin recommendedFee = this.getRecommendedFee();
		
		//input总金额
		Coin iutputSum = Coin.ZERO;
		Coin feeCoin = Coin.ZERO;
		for(Unspent unspent : unspentList) {
			Sha256Hash hash = Sha256Hash.wrap(unspent.txid());
			Address address = Address.fromString(parameters, unspent.address());
			Script script = new ScriptBuilder().createOutputScript(address);
			//in_put
			transaction.addInput(hash, unspent.vout(), script);
			
			Coin amount = Coin.parseCoin(String.valueOf(unspent.amount()));
			iutputSum = iutputSum.add(amount);
			
			//预计交易大小
			int transactionSize = transaction.getInputs().size() * 148 + 34 * transaction.getOutputs().size() + 10;
			//计算总矿工费
			feeCoin = recommendedFee.multiply(transactionSize);
			
			//总输出+所需矿工费 <= 总输入
			if(feeCoin.add(transaction.getOutputSum()).compareTo(iutputSum) <= 0) {
				break;
			}
		}
		
		//找零金额
    	Coin changeAmount = iutputSum.subtract(feeCoin.add(transaction.getOutputSum()));
    	if(changeAmount.compareTo(Coin.ZERO) < 0) {
    		//余额不足
    		throw new RuntimeException("所需余额不足,"+ changeAmount.toFriendlyString());
    	}else if(changeAmount.compareTo(Coin.ZERO) > 0) {
    		Address changeAddress = Address.fromString(parameters, from[0]);	//找零地址
    		transaction.addOutput(changeAmount, changeAddress);
    	}
    	return transaction.bitcoinSerialize();
	}
	
	/**
	 * 交易签名
	 * @param keys			私钥列表
	 * @param transaction	交易体
	 * @return
	 */
	public byte[] signTransaction(Collection<String> keys,byte[] transactionBytes) {
		Map<String,ECKey> eckeyMap = new HashMap<>();
		for(String k : keys) {
			ECKey ecKey = ECKey.fromPrivate(HexUtil.decodeHex(k));
			String address = LegacyAddress.fromKey(parameters, ecKey).toBase58();
			eckeyMap.put(address, ecKey);
		}
		Transaction transaction = new Transaction(parameters, transactionBytes);
		List<TransactionInput> inputs = transaction.getInputs();
		int i = 0;
		for(TransactionInput in : inputs) {
			Script scriptPubKey = in.getScriptSig();
			
			String address = LegacyAddress.fromPubKeyHash(parameters, scriptPubKey.getPubKeyHash()).toBase58();
			ECKey eckey = eckeyMap.get(address);
			if(eckey == null) {
				throw new RuntimeException("签名失败,未发现该地址私钥");
			}
			Sha256Hash hash = transaction.hashForSignature(i++, scriptPubKey, Transaction.SigHash.ALL, false);
			ECKey.ECDSASignature ecSig = eckey.sign(hash);
			TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, false);
			in.setScriptSig(ScriptBuilder.createInputScript(txSig, eckey));
		}
		return transaction.bitcoinSerialize();
	}
	
	/**
	 * 发送交易
	 * @param transaction		已签名交易
	 * @return
	 */
	public String sendRawTransaction(byte[] transaction) {
		String txHash = rpcClient.sendRawTransaction(HexUtil.encodeHexStr(transaction));
		return txHash;
	}
	
	/**
	 * 获取目前推荐手续费
	 * @return
	 */
	public Coin getRecommendedFee() {
		try {
			String json = HttpUtil.get("https://bitcoinfees.earn.com/api/v1/fees/recommended");
			if(StringUtils.isNotBlank(json)) {
				JSONObject jsonObj = JSONUtil.parseObj(json);
				return Coin.valueOf(jsonObj.getLong("halfHourFee"));
			}
		} catch (Exception e) {}
		
		return Coin.valueOf(100);
	}
}