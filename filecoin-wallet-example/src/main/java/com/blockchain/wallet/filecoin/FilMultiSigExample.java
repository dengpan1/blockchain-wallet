package com.blockchain.wallet.filecoin;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.encoders.Hex;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blockchain.wallet.filecoin.cbor.CborEncoder;
import com.blockchain.wallet.filecoin.cbor.CborObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.ipfs.cid.Cid;
import io.ipfs.cid.Cid.Codec;
import io.ipfs.multihash.Multihash;
import lombok.*;

public class FilMultiSigExample {
	private static Map<String,String> signer = Maps.newHashMap();
	private static String sender = "f1wwzbpsaitsibpwdjz6lefcvdvbgez6z3sn4goaq";
	private static String senderPrivateKey = null;
	//private static String multisigAddress = "f2dxjo3jmfzm7jafuggr7e524fhfspbuouzbg4gsi";
	//private static String multisigAddress = "f2n2pmytbdjaslkhhjr7isus5ncc4nr4tchrjpzpi";
	private static String multisigAddress = "f2rf2i5bw7q7zvvpti7zmyctwrpzjkrwaixf5a3wy";
	
	static {
		signer.put("f1kwj2euckydrnz5whszz7mjl7nalec5d4kyfd7aq", Hex.toHexString(Base64.decodeBase64("A1kUzVqhLc4zUErODjmANHakPLtImEQP6tDfZDhQVqw=")));
		signer.put("f1cjbbe2camjznoqmgx7kijhgjvb3qkgaehnjmxxq", Hex.toHexString(Base64.decodeBase64("udQdKEH/thUcmW/uQLJz0YvHHvkvFJGD3Ipepeo4LdU=")));
		signer.put("f1g762qza77rucxjpjnndfwdyzdd7ih4mie4vps6y", Hex.toHexString(Base64.decodeBase64("+CPuhHe7V5vhkYnjU7aTC9RZ+Ku2eZ+mi82SzM27Q54=")));
		signer.put("f1wwzbpsaitsibpwdjz6lefcvdvbgez6z3sn4goaq", Hex.toHexString(Base64.decodeBase64("m0pMxVy9n+qbT37w5F8uHciK6Of8971hOnle5DAAse4=")));
		senderPrivateKey = Hex.toHexString(Base64.decodeBase64("m0pMxVy9n+qbT37w5F8uHciK6Of8971hOnle5DAAse4="));
	}
	
	public static void main1(String[] args) throws Exception {
		//普通转账
		FilSignatureMessage.Message tran = FilSignatureMessage.Message.builder()
		        .to("f1g762qza77rucxjpjnndfwdyzdd7ih4mie4vps6y")
		        .from(sender)
		        .params("")
		        .nonce(24L)
		        .value("0")
		        .gasFeeCap("2554952971")
		        .gasLimit(18103918L)
		        .gasPremium("150790")
		        .method(0L)
		        .version(0L)
		        .build();
		//send(tran,senderPrivateKey);
		
		//生成多签地址
		String param = createMultisigAddress();
		tran = FilSignatureMessage.Message.builder()
		        .to("f01")
		        .from(sender)
		        .params(param)
		        .nonce(24L)
		        .value("0")
		        .gasFeeCap("2554952971")
		        .gasLimit(18103918L)
		        .gasPremium("150790")
		        .method(2L)
		        .version(0L)
		        .build();
		send(tran,senderPrivateKey);
		
		//提议多签交易
		param = proposeMultisig(sender, BigDecimal.valueOf(0.01));
		tran = FilSignatureMessage.Message.builder()
		        .to(multisigAddress)
		        .from("f1kwj2euckydrnz5whszz7mjl7nalec5d4kyfd7aq")
		        .params(param)
		        .nonce(5L)
		        .value("1")
		        .gasFeeCap("2554952971")
		        .gasLimit(18103918L)
		        .gasPremium("150790")
		        .method(2L)		//提议
		        .version(0L)
		        .build();
		//send(tran,signer.get(tran.getFrom()));
		
		//授权多签交易
		param = approveOrCancelMsigMsgParams(0L, "f01118245", sender, BigDecimal.valueOf(0.01));
		tran = FilSignatureMessage.Message.builder()
		        .to(multisigAddress)
		        .from("f1cjbbe2camjznoqmgx7kijhgjvb3qkgaehnjmxxq")
		        .params(param)
		        .nonce(15L)
		        .value("1")
		        .gasFeeCap("2554952971")
		        .gasLimit(15103918L)
		        .gasPremium("150790")
		        .method(3L)		//授权
		        .version(0L)
		        .build();
		//send(tran,signer.get(tran.getFrom()));
		
		//取消授权交易，只能提议者才能取消
		param = approveOrCancelMsigMsgParams(0L, "f01118245", sender, BigDecimal.valueOf(0.01));
		tran = FilSignatureMessage.Message.builder()
		        .to(multisigAddress)
		        .from("f1cjbbe2camjznoqmgx7kijhgjvb3qkgaehnjmxxq")
		        .params(param)
		        .nonce(17L)
		        .value("0")
		        .gasFeeCap("2554952971")
		        .gasLimit(15103918L)
		        .gasPremium("150790")
		        .method(4L)		//取消授权
		        .version(0L)
		        .build();
		//send(tran,signer.get(tran.getFrom()));
    }
	
	public static byte[] serializeBigNum(BigDecimal amount) {
		if(amount.compareTo(BigDecimal.ZERO) == 0) {
			return new byte[] {};
		}
		byte[] valueBuffer = FilUnitUtil.big2Small(amount).toByteArray();
		return ArrayUtils.addAll(Hex.decode("00"),valueBuffer);
	}
	
	/**
	 * 
	 * @param messageId		多签交易序列号
	 * @param requester		提议者地址ID
	 * @param to			目标地址
	 * @param amount		批准金额
	 * @return
	 * @throws DecoderException
	 */
	public static String approveOrCancelMsigMsgParams(Long messageId, String requester, String to, BigDecimal amount) throws DecoderException {
		List<CborObject> list = new ArrayList<>();
		list.add(new CborObject.CborByteArray(FilAddress.addressAsBytes(requester)));
		list.add(new CborObject.CborByteArray(FilAddress.addressAsBytes(to)));
		list.add(new CborObject.CborByteArray(serializeBigNum(amount)));
		list.add(new CborObject.CborLong(0L));
		list.add(new CborObject.CborByteArray(new byte[]{}));

		CborObject.CborList cborList = new CborObject.CborList(Lists.newArrayList(new CborObject.CborList(list)));

		byte[] serializedProposalParams = cborList.toByteArray();

		serializedProposalParams = ArrayUtils.subarray(serializedProposalParams, 1, serializedProposalParams.length);

		byte[] hash = FilecoinSign.createHash(serializedProposalParams);
	
		list = new ArrayList<>();
		list.add(new CborObject.CborLong(messageId));
		list.add(new CborObject.CborByteArray(hash));
		cborList = new CborObject.CborList(Lists.newArrayList(new CborObject.CborList(list)));

		byte[] params = cborList.toByteArray();
		params = ArrayUtils.subarray(params, 1, params.length);

		String paramStr = Base64.encodeBase64String(params);
		return paramStr;
	 }
	
	public static String proposeMultisig(String toAddress,BigDecimal amount) throws Exception {
		ProposeMultisigMsgParam param = ProposeMultisigMsgParam.builder().amount(amount).toAddress(toAddress).build();
		byte[] bytes = proposeMultisigMsg(param);
		return Base64.encodeBase64String(bytes);
	}
	
	public static String createMultisigAddress() throws Exception {
		FilMultiSigExample mainTest = new FilMultiSigExample();
		CreateMultisigMsgParam param = CreateMultisigMsgParam.builder()
		.signers(signer.keySet())
		.requiredNumberOfApprovals(3)
		.unlockDuration(0)
		.codeCID("fil/5/multisig")
		.startEpoch(0).build();
		byte[] bytes = mainTest.createMultisigMsg(param);
		return Base64.encodeBase64String(bytes);
	}
	
	public static byte[] proposeMultisigMsg(ProposeMultisigMsgParam param) throws Exception {
		byte[] valueBuffer = serializeBigNum(param.getAmount());
		
		List<CborObject> list = new ArrayList<>();
		list.add(new CborObject.CborByteArray(FilAddress.addressAsBytes(param.getToAddress())));
		list.add(new CborObject.CborByteArray(valueBuffer));
		list.add(new CborObject.CborLong(0L));
		list.add(new CborObject.CborByteArray(new byte[] {}));
		CborObject.CborList cborList = new CborObject.CborList(Lists.newArrayList(new CborObject.CborList(list)));
		byte[] proposeParams = cborList.toByteArray();
		
		proposeParams = ArrayUtils.subarray(proposeParams, 1, proposeParams.length);
		return proposeParams;
	}
	
	public byte[] createMultisigMsg(CreateMultisigMsgParam param) throws Exception {
		List<CborObject.CborByteArray> addressList = Lists.newArrayList();
    	for(String address : param.getSigners()) {
    		addressList.add(new CborObject.CborByteArray(FilAddress.addressAsBytes(address)));
    	}
    	List<CborObject> list = new ArrayList<>();
		list.add(new CborObject.CborList(addressList));
		list.add(new CborObject.CborLong(param.getRequiredNumberOfApprovals()));
		list.add(new CborObject.CborLong(param.getUnlockDuration()));
		list.add(new CborObject.CborLong(param.getStartEpoch()));
		
		CborObject.CborList cborList = new CborObject.CborList(Lists.newArrayList(new CborObject.CborList(list)));
		
		byte[] constructorParams = cborList.toByteArray();
		
    	constructorParams = ArrayUtils.subarray(constructorParams, 1, constructorParams.length);
    	
    	byte[] cidHashBytes = createCid(param.getCodeCID().getBytes("utf-8"));
    	
    	list = new ArrayList<>();
    	list.add(new CborCid(cidHashBytes));
    	list.add(new CborObject.CborByteArray(constructorParams));
    	cborList = new CborObject.CborList(list);
		
		return cborList.toByteArray();
    }
    
    public byte[] createCid(final byte[] data) {
    	final Cid cid = Cid.buildCidV1(Codec.Raw, Multihash.Type.id, data);
        return cid.toBytes();
	}
    
    public static void send(FilSignatureMessage.Message tran,String priKeyAsHex) throws DecoderException{
        String sign = signTransaction(tran, priKeyAsHex);
        JSONObject cid = new JSONObject();
        JSONObject signer = new JSONObject();
        JSONObject message = new JSONObject();
        JSONObject callback = new JSONObject();
        JSONObject ncid = new JSONObject();

        cid.put("/", "");
        ncid.put("/", "");

        signer.put("Type", 1);
        signer.put("Data", sign);

        message.put("To", tran.getTo());
        message.put("From", tran.getFrom());
        message.put("Nonce", tran.getNonce());
        message.put("Value", "0");
        message.put("GasLimit", tran.getGasLimit());
        message.put("GasFeeCap", tran.getGasFeeCap());
        message.put("GasPremium", tran.getGasPremium());
        message.put("Method", tran.getMethod());
        message.put("Params", tran.getParams());
        message.put("CID", cid);

        callback.put("Message", message);
        callback.put("Signature", signer);
        callback.put("CID", ncid);
        String rawTx = JSON.toJSONString(callback);

        System.out.println(rawTx);
    }
    
    public static String signTransaction(FilSignatureMessage.Message tran, String priKeyAsHex) throws DecoderException {
    	List<CborObject> list = new ArrayList<>();
		list.add(new CborObject.CborLong(tran.getVersion()));
		list.add(new CborObject.CborByteArray(FilAddress.addressAsBytes(tran.getTo())));
		list.add(new CborObject.CborByteArray(FilAddress.addressAsBytes(tran.getFrom())));
		list.add(new CborObject.CborLong(tran.getNonce()));
    	
    	BigInteger value = new BigInteger(tran.getValue());
    	if(value.compareTo(BigInteger.ZERO) == 0) {
    		list.add(new CborObject.CborByteArray(new byte[] {}));
    	}else {
    		list.add(new CborObject.CborByteArray(unsigned(value)));
    	}
    	
		list.add(new CborObject.CborLong(tran.getGasLimit()));
		list.add(new CborObject.CborByteArray(unsigned(new BigInteger(tran.getGasFeeCap()))));
		list.add(new CborObject.CborByteArray(unsigned(new BigInteger(tran.getGasPremium()))));
		list.add(new CborObject.CborLong(tran.getMethod()));
		list.add(new CborObject.CborByteArray(Base64.decodeBase64(tran.getParams())));
    	
    	CborObject.CborList cborList = new CborObject.CborList(list);
        byte[] encodedBytes = cborList.toByteArray();
        
        byte[] cidHashBytes = FilecoinSign.getCidHash(encodedBytes);
        return FilecoinSign.sign(cidHashBytes,priKeyAsHex);
    }
    
    public static byte[] unsigned(BigInteger value) {
    	if (value == null) {
    		return null;
    	}
    	byte[] array = value.toByteArray();
    	
    	if (array[0] != 0) {
            byte[] byte2 = new byte[array.length + 1];
            byte2[0] = 0;
            System.arraycopy(array, 0, byte2, 1, array.length);
            return byte2;
        } else {
            return array;
        }
    }
    
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CreateMultisigMsgParam {
		private Set<String> signers;          	 //签名参与方地址
		private int requiredNumberOfApprovals;	 //
		private int unlockDuration;
		private int startEpoch;			//
		private String codeCID;			//支付通道CID
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProposeMultisigMsgParam {
		private String toAddress;
		private BigDecimal amount;
	}
	
	public static class CborCid implements CborObject {
		final byte[] value;
		public CborCid(byte[] value) {
			this.value = value;
		}
		
		@Override
		public void serialize(CborEncoder encoder) {
			try {
                encoder.writeTag(LINK_TAG);
                byte[] withMultibaseHeader = new byte[value.length + 1];
                System.arraycopy(value, 0, withMultibaseHeader, 1, value.length);
                encoder.writeByteString(withMultibaseHeader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
		}

		@Override
		public List<Multihash> links() {
			return null;
		}
	}
}