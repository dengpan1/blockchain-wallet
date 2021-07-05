package com.blockchain.wallet.bitcoin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.SignatureDecodeException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptPattern;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.MnemonicUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Unspent;

public class BitcoinSegwitWalletExample {
	
	//private static MainNetParams parameters = MainNetParams.get();
	
	private static TestNet3Params parameters = TestNet3Params.get();
	
	private static boolean isMainNet = parameters.equals(MainNetParams.get());
	
	private static Script redeemScript = null;
	
	private static DeterministicKey eCKey0 = null;
	private static DeterministicKey eCKey1 = null;
	private static DeterministicKey eCKey2 = null;
	
	static {
		String mnemonicCode0 = "banana promote evidence piece worth annual purse member ivory start method wild clap hole seek topic night stairs behind maze liar boat dress ramp";
		String mnemonicCode1 = "urban inhale table trade process write slab enforce custom mistake suspect chalk announce cruel way grief vocal scale clutch net vocal foam demise fish";
		byte[] seed0 = MnemonicUtils.generateSeed(mnemonicCode0, "");
		DeterministicKey rootPrivateKey0 = HDKeyDerivation.createMasterPrivateKey(seed0);
		
		eCKey0 = HDWalletUtil.fromBIP44HDpath(rootPrivateKey0, 0,0);
		
		eCKey1 = HDWalletUtil.fromBIP44HDpath(rootPrivateKey0, 1,0);
		
		eCKey2 = HDWalletUtil.fromBIP44HDpath(rootPrivateKey0, 2,0);
		
		//String address0 = LegacyAddress.fromKey(parameters, eCKey0).toBase58();
		//String address1 = LegacyAddress.fromKey(parameters, eCKey1).toBase58();
		
		//List<ECKey> keys = ImmutableList.of(eCKey0, eCKey1);
		
		//redeemScript = ScriptBuilder.createRedeemScript(2, keys);
	}
	
	public static void main(String s[]) {
		BitcoinSegwitWalletExample example = new BitcoinSegwitWalletExample();
		example.createSegwitAddress();
		
		//接收方 <地址,金额>
		Map<String, BigDecimal> receiverMap = new HashMap<>();
		receiverMap.put("2Mz4j76KthutdNXD7bfdKTKjRas3jKwQeZE", BigDecimal.valueOf(0.009));
		//receiverMap.put("tb1qs69t4ve607ftlwk54j3j6dmjrhue4lkvuxw873", BigDecimal.valueOf(0.008));
		
		Transaction transaction = example.buildTransaction(receiverMap, "tb1qs69t4ve607ftlwk54j3j6dmjrhue4lkvuxw873");
		
		byte[] signedTransaction = example.signTransaction(Lists.newArrayList(eCKey0.getPrivateKeyAsHex()), transaction);
		//byte[] signedTransaction = example.multisignTransaction(eCKey1, transaction,true);
		//signedTransaction = example.multisignTransaction(eCKey0, signedTransaction,false);
		System.out.println(HexUtil.encodeHexStr(signedTransaction));
	}
	
	public byte[] multisignTransaction(ECKey key,byte[] transactionBytes,boolean first) {
		Transaction transaction = new Transaction(parameters, transactionBytes);
		List<TransactionInput> inputs = transaction.getInputs();
		int i = 0;
		for(TransactionInput in : inputs) {
			if(first) {
				Sha256Hash hash = transaction.hashForSignature(i++, redeemScript, Transaction.SigHash.ALL, false);
				ECKey.ECDSASignature ecSig = key.sign(hash);
				TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, false);
				Script inputScript = ScriptBuilder.createP2SHMultiSigInputScript(Collections.singletonList(txSig), redeemScript);
				in.setScriptSig(inputScript);
			}else {
				Script scriptPubKey = in.getScriptSig();
				
				List<ScriptChunk> scriptChunks = scriptPubKey.getChunks();
				
				List<TransactionSignature> signatureList = new ArrayList<>();
				
				Iterator<ScriptChunk> iterator = scriptChunks.iterator();
				Script redeem = null;
				while (iterator.hasNext()) {
					ScriptChunk chunk = iterator.next();
					if (iterator.hasNext() && chunk.opcode != 0) {
						TransactionSignature transactionSignature = null;
							try {
	                            transactionSignature = TransactionSignature.decodeFromBitcoin(Objects.requireNonNull(chunk.data), false, false);
	                        } catch (SignatureDecodeException e) {
	                            e.printStackTrace();
	                        }
	                        signatureList.add(transactionSignature);
					} else {
						redeem = new Script(Objects.requireNonNull(chunk.data));
					}
				}
				
				//计算待签名hash,也就是交易的简化形式的hash值,对这个hash值进行签名
		        Sha256Hash sigHash = transaction.hashForSignature(i++, Objects.requireNonNull(redeem), Transaction.SigHash.ALL, false);
	
		        //签名之后得到ECDSASignature
		        ECKey.ECDSASignature ecdsaSignature = key.sign(sigHash);
	
		        //ECDSASignature转换为TransactionSignature
		        TransactionSignature transactionSignature = new TransactionSignature(ecdsaSignature, Transaction.SigHash.ALL, false);
		        //添加本次签名的数据
		        signatureList.add(transactionSignature);
	
		        // 重新构建p2sh多重签名的输入脚本
		        scriptPubKey = ScriptBuilder.createP2SHMultiSigInputScript(signatureList, redeem);
	
		        //更新新的脚本
		        in.setScriptSig(scriptPubKey);
			}
		}
		return transaction.bitcoinSerialize();
	}
	
	
	public byte[] signTransaction(Collection<String> keys,Transaction transaction) {
		/*Map<String,ECKey> eckeyMap = new HashMap<>();
		for(String k : keys) {
			ECKey ecKey = ECKey.fromPrivate(HexUtil.decodeHex(k));
			String address = SegwitAddress.fromKey(parameters, ecKey).toBech32();
			eckeyMap.put(address, ecKey);
		}*/
		//Transaction transaction = new Transaction(parameters, transactionBytes);
		List<TransactionInput> inputs = transaction.getInputs();
		for(TransactionInput in : inputs) {
			//Script scriptPubKey = in.getScriptSig();
			
			//String address = SegwitAddress.fromHash(parameters, scriptPubKey.getPubKeyHash()).toBech32();
			//String address = LegacyAddress.fromPubKeyHash(parameters, scriptPubKey.getPubKeyHash()).toBase58();
			ECKey eckey = eCKey0;//eckeyMap.get(address);
			if(eckey == null) {
				throw new RuntimeException("签名失败,未发现该地址私钥");
			}
			Coin value = in.getParentTransaction().getOutput(in.getIndex()).getValue();
			//计算见证信息
			//Script scriptCode = new ScriptBuilder().data(ScriptBuilder.createP2PKHOutputScript(eckey).getProgram()).build();
			Script scriptCode = ScriptBuilder.createP2PKHOutputScript(eckey);
			
	        TransactionSignature txSig = transaction.calculateWitnessSignature(0, eckey,scriptCode, value,Transaction.SigHash.ALL, false);
	        //设置input的交易见证
	        in.setWitness(TransactionWitness.redeemP2WPKH(txSig, eckey));
	        //隔离见证的input不需要scriptSig
	        in.setScriptSig(ScriptBuilder.createEmpty());
	        
			/*Sha256Hash hash = transaction.hashForSignature(i++, scriptPubKey, Transaction.SigHash.ALL, false);
			ECKey.ECDSASignature ecSig = eckey.sign(hash);
			TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, false);
			in.setScriptSig(ScriptBuilder.createInputScript(txSig, eckey));*/
		}
		return transaction.bitcoinSerialize();
	}
	
	public Transaction buildTransaction(Map<String,BigDecimal> receiverMap,String from) {
		Transaction transaction = new Transaction(parameters);
		
		receiverMap.forEach((address,amount) -> {
			//Address receiver = SegwitAddress.fromBech32(parameters, address);
			Address receiver = Address.fromString(parameters, address);
			//out_put
			transaction.addOutput(Coin.parseCoin(amount.toPlainString()), receiver);
		});
		
		List<UTXO> unspentList = getUnspent(from);
		
		if(CollUtil.isEmpty(unspentList)) {
			throw new RuntimeException("utxo not enough");
		}
		
		unspentList = unspentList.stream().sorted(Comparator.comparing(UTXO::getValue).reversed()).collect(Collectors.toList());
		
		//目前网络推荐的矿工费价格
		Coin recommendedFee = Coin.valueOf(100);
		
		//input总金额
		Coin iutputSum = Coin.ZERO;
		Coin feeCoin = Coin.ZERO;
		for(UTXO unspent : unspentList) {
			Sha256Hash hash = unspent.getHash();
			Address address = Address.fromString(parameters, unspent.getAddress());
			Script script = unspent.getScript();//new ScriptBuilder().createOutputScript(address);
			//in_put
			//TransactionInput in = transaction.addInput(hash, unspent.getIndex(), script);
			
			TransactionInput in = new TransactionInput(parameters, transaction, script.getProgram(), new TransactionOutPoint(parameters, unspent.getIndex(), hash),unspent.getValue());
			transaction.addInput(in);
			
			Coin amount = unspent.getValue();
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
    		Address changeAddress = Address.fromString(parameters, from);	//找零地址
    		//transaction.addOutput(changeAmount, changeAddress);
    	}
    	
    	if(transaction.getInputSum().subtract(feeCoin.add(transaction.getOutputSum())).compareTo(Coin.ZERO) != 0) {
    		System.out.println("输入输出不一致");
    	}
    	
    	return transaction;
	}
	
	//生成隔离见证地址
	public String createSegwitAddress() {
		SegwitAddress segwitAddress = SegwitAddress.fromKey(parameters, eCKey0);
		String address = segwitAddress.toBech32();
		System.out.println(address);
		return address;
	}
	
	public String createMultiSigAddress() {
		String address0 = SegwitAddress.fromKey(parameters, eCKey0).toBech32();
		System.out.println("address0 is:"+ address0);
		String address1 = SegwitAddress.fromKey(parameters, eCKey1).toBech32();
		System.out.println("address1 is:"+ address1);
		
		List<ECKey> keys = ImmutableList.of(eCKey0, eCKey1);
		
		Script redeemScript = ScriptBuilder.createRedeemScript(2, keys);
		
		Script script = ScriptBuilder.createP2SHOutputScript(redeemScript);
		
		String addr = LegacyAddress.fromScriptHash(parameters, ScriptPattern.extractHashFromP2SH(script)).toBase58();
		
		System.out.println(addr);
		
		//System.out.println("puk0 is:"+ Hex.toHexString(eCKey0.getPubKeyHash()));
		//System.out.println("puk1 is:"+ Hex.toHexString(eCKey1.getPubKeyHash()));
		
		return addr;
		
		/*LegacyAddress address3 = LegacyAddress.fromScriptHash(params, script.getPubKeyHash());
		
		System.out.println(address3.toBase58());*/
	}
	
    public static List<UTXO> getUnspent(String address) {
        List<UTXO> utxos = Lists.newArrayList();
        String host = isMainNet ? "https://blockstream.info/api/" : "https://blockstream.info/testnet/api/";
        try {
            String httpGet = HttpUtil.get(host + "address/" + address + "/utxo");
            if (StringUtils.equals("No free outputs to spend", httpGet)) {
                return utxos;
            }
            JSONArray unspentOutputs = JSONUtil.parseArray(httpGet);
            
            List<JSONObject> outputs = JSONUtil.toList(unspentOutputs,  JSONObject.class);
            if (unspentOutputs == null || unspentOutputs.isEmpty()) {
                System.out.println("交易异常，余额不足");
            }
            
            for(JSONObject obj : outputs) {
            	String txid = obj.getStr("txid");
            	Long vout = obj.getLong("vout");
            	Long value = obj.getLong("value");
            	
            	httpGet = HttpUtil.get(host + "tx/" + txid);
            	
            	JSONObject script = (JSONObject) JSONUtil.toBean(httpGet, JSONObject.class).getJSONArray("vout").get(vout.intValue());
            	
            	UTXO utxo = new UTXO(Sha256Hash.wrap(txid), vout, Coin.valueOf(value),
                        0, false, new Script(HexUtil.decodeHex(script.getStr("scriptpubkey"))),script.getStr("scriptpubkey_address"));
                utxos.add(utxo);
            }
            return utxos;
        } catch (Exception e) {
            e.printStackTrace();
            return utxos;
        }
    }
}
