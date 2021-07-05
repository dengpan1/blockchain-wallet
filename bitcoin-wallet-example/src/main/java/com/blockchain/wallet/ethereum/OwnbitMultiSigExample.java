package com.blockchain.wallet.ethereum;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import com.google.common.collect.Lists;
import org.web3j.utils.Numeric;

public class OwnbitMultiSigExample {
	private static Web3j web3j = Web3j.build(new HttpService("https://ropsten.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161"));
	//private static String contract = "0xb84ce3Fd68CA87Ed2F2a5A4894abE6200E043Abc";
	private static String contractAddress = "0x839B36cb7202E2b5dFb854CACC60DFE2bd171aE0";
	//private static String contractAddress = "0xd9145CCE52D386f254917e481eB44e9943F39138";
	//\x19Ethereum Signed Message:\n32
	private static final String personalMessagePrefix = "0x19457468657265756d205369676e6564204d6573736167653a0a3332";
	
	private static Ownbit_sol_OwnbitMultiSig contract = null;
	
	private static Credentials credentials0 = null;
	private static Credentials credentials1 = null;
	private static Credentials credentials2 = null;
	
	static {
		/*byte[] seed = MnemonicUtils.generateSeed(mnemonicCode, "");
		DeterministicKey rootPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
		DeterministicKey eCKey0 = HDWalletUtil.fromBIP44HDpath(rootPrivateKey, 0,HDCoin.ETH);
		DeterministicKey eCKey1 = HDWalletUtil.fromBIP44HDpath(rootPrivateKey, 1,HDCoin.ETH);
		DeterministicKey eCKey2 = HDWalletUtil.fromBIP44HDpath(rootPrivateKey, 2,HDCoin.ETH);*/
		//导入钱包
		credentials0 = Credentials.create("ee323c25873450e5095fe56e7fc8e261975be17938b3ad4bc94cf67efa298fc8");
		credentials1 = Credentials.create("a771788f4001e8283fe1500a44f2e77cfa8b64e1a8fa2957c81babd53daff794");
		credentials2 = Credentials.create("3ddbea1117f1f52143e819bbef0a70d92906f44a09a1b8a0b3b7170c762013d3");
		
		StaticGasProvider gasProvider = new StaticGasProvider(BigInteger.valueOf(2000000000), BigInteger.valueOf(500000));
		contract = new Ownbit_sol_OwnbitMultiSig(contractAddress, web3j, credentials1, gasProvider);
		
		System.out.println("address0:"+ credentials0.getAddress());
		System.out.println("address1:"+ credentials1.getAddress());
		System.out.println("address2:"+ credentials2.getAddress());
	}
	
	public static void main(String s[]) throws Exception {
		//spendEth();
		spendERC20();
	}
	
	/**
	 * ERC20转账
	 * @throws Exception
	 */
	public static void spendERC20() throws Exception {
		String erc20Contract = "0x6c31a6CA2CE248ee4456D2972F5744bF4A2A2B4b";
		String destination = "0xd56E98E446bA114E1AB564BD9C2932BcE191Ee5f";
		BigInteger value = new BigInteger("100000000000000000000");
		//创建签名消息
		byte[] message = generateMessageToSign(erc20Contract,destination,value);
		
		//签名
		Sign.SignatureData signature0 = Sign.signMessage(message,credentials0.getEcKeyPair(), false);
		Sign.SignatureData signature1 = Sign.signMessage(message,credentials1.getEcKeyPair(), false);
		
		//System.out.println(JSON.toJSONString(Lists.newArrayList(Numeric.toBigInt(signature0.getV()).subtract(BigInteger.valueOf(27)),Numeric.toBigInt(signature1.getV()).subtract(BigInteger.valueOf(27)))));
		
		//System.out.println(JSON.toJSONString(Lists.newArrayList("0x"+Hex.toHexString(signature0.getR()),"0x"+Hex.toHexString(signature1.getR()))));
		
		//System.out.println(JSON.toJSONString(Lists.newArrayList("0x"+Hex.toHexString(signature0.getS()),"0x"+Hex.toHexString(signature1.getS()))));
		
		//V-27，合约中验签逻辑V+27
		BigInteger recId0 = Numeric.toBigInt(signature0.getV()).subtract(BigInteger.valueOf(27));
		BigInteger recId1 = Numeric.toBigInt(signature1.getV()).subtract(BigInteger.valueOf(27));
		
		//验签
		BigInteger puk0 = Sign.recoverFromSignature(recId0.intValue(), new ECDSASignature(new BigInteger(1, signature0.getR()), new BigInteger(1, signature0.getS())), message);
		System.out.println("verify sign0:" + credentials0.getAddress().equalsIgnoreCase("0x"+ Keys.getAddress(puk0)));
		BigInteger puk1 = Sign.recoverFromSignature(recId1.intValue(), new ECDSASignature(new BigInteger(1, signature1.getR()), new BigInteger(1, signature1.getS())), message);
		System.out.println("verify sign1:" + credentials1.getAddress().equalsIgnoreCase("0x"+ Keys.getAddress(puk1)));
		
		//发送交易
		CompletableFuture<TransactionReceipt> future = contract.spendERC20(destination, erc20Contract,value, 
				Lists.newArrayList(recId0,recId1), 
				Lists.newArrayList(signature0.getR(),signature1.getR()),
				Lists.newArrayList(signature0.getS(),signature1.getS())).sendAsync();
		TransactionReceipt receipt = future.get(60, TimeUnit.SECONDS);
		System.out.println("tx:"+ receipt.getTransactionHash());
	}
	
	/**
	 * eth转账
	 * @throws Exception
	 */
	public static void spendEth() throws Exception {
		String erc20Contract = "0x0000000000000000000000000000000000000000";
		String destination = "0xd56E98E446bA114E1AB564BD9C2932BcE191Ee5f";
		BigInteger value = Convert.toWei("0.1", Unit.ETHER).toBigInteger();
		//创建签名消息
		byte[] message = generateMessageToSign(erc20Contract,destination,value);
		
		//签名
		Sign.SignatureData signature0 = Sign.signMessage(message,credentials0.getEcKeyPair(), false);
		Sign.SignatureData signature1 = Sign.signMessage(message,credentials1.getEcKeyPair(), false);
		
		//System.out.println(JSON.toJSONString(Lists.newArrayList(Numeric.toBigInt(signature0.getV()).subtract(BigInteger.valueOf(27)),Numeric.toBigInt(signature1.getV()).subtract(BigInteger.valueOf(27)))));
		
		//System.out.println(JSON.toJSONString(Lists.newArrayList("0x"+Hex.toHexString(signature0.getR()),"0x"+Hex.toHexString(signature1.getR()))));
		
		//System.out.println(JSON.toJSONString(Lists.newArrayList("0x"+Hex.toHexString(signature0.getS()),"0x"+Hex.toHexString(signature1.getS()))));
		
		//V-27，合约中验签逻辑V+27
		BigInteger recId0 = Numeric.toBigInt(signature0.getV()).subtract(BigInteger.valueOf(27));
		BigInteger recId1 = Numeric.toBigInt(signature1.getV()).subtract(BigInteger.valueOf(27));
		
		//验签
		BigInteger puk0 = Sign.recoverFromSignature(recId0.intValue(), new ECDSASignature(new BigInteger(1, signature0.getR()), new BigInteger(1, signature0.getS())), message);
		System.out.println("verify sign0:" + credentials0.getAddress().equalsIgnoreCase("0x"+ Keys.getAddress(puk0)));
		BigInteger puk1 = Sign.recoverFromSignature(recId1.intValue(), new ECDSASignature(new BigInteger(1, signature1.getR()), new BigInteger(1, signature1.getS())), message);
		System.out.println("verify sign1:" + credentials1.getAddress().equalsIgnoreCase("0x"+ Keys.getAddress(puk1)));

		//发送交易
		CompletableFuture<TransactionReceipt> future = contract.spend(destination, value, 
				Lists.newArrayList(recId0,recId1), 
				Lists.newArrayList(signature0.getR(),signature1.getR()),
				Lists.newArrayList(signature0.getS(),signature1.getS())).sendAsync();
		
		TransactionReceipt receipt = future.get(60, TimeUnit.SECONDS);
		System.out.println("tx:"+ receipt.getTransactionHash());
	}
	
	public static byte[] generateMessageToSign(String erc20Contract,String destination,BigInteger value) throws Exception {
		//获取nonce
		BigInteger spendNonce = contract.getSpendNonce().send();
		System.out.println("spendNonce:"+ spendNonce.toString());
		
		byte[] param = byteMerger(
				Numeric.hexStringToByteArray(contractAddress),
				Numeric.hexStringToByteArray(erc20Contract),
				Numeric.hexStringToByteArray(destination),
				Numeric.toBytesPadded(value, 32),
				Numeric.toBytesPadded(spendNonce, 32)
				);
		
		byte[] bytes = byteMerger(
				Numeric.hexStringToByteArray(personalMessagePrefix),
				Hash.sha3(param)
				);
		return Hash.sha3(bytes);
	}
	
	public static byte[] byteMerger(byte[]... values) {
	    int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
	}
}