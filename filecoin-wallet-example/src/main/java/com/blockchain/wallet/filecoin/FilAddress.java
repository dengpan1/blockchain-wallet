package com.blockchain.wallet.filecoin;

import com.google.common.io.BaseEncoding;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import java.math.BigInteger;

public class FilAddress {
	
	public static byte[] addressAsBytes(String address) throws DecoderException{
        FilProtocolIndicator filProtocolIndicator = FilProtocolIndicator.getByCode(address.substring(1, 2));
        if(filProtocolIndicator == null){
            return null;
        }

        switch (filProtocolIndicator){
            case ID:
                BigInteger highOrderBitValue = new BigInteger(1, Hex.decodeHex("80"));
                byte[] output = new byte[] {};
                BigInteger x = new BigInteger(address.substring(2));
                while (x.compareTo(highOrderBitValue) >= 0) {
                	output = ArrayUtils.addAll(output, (byte) (x.byteValue() | 0x80));
                	x = x.shiftRight(7);
                }
                output = ArrayUtils.addAll(output, x.byteValue());
                return ArrayUtils.addAll(new byte[] {0x00}, output);
            case SECP256K1:
            	String rawPayload = address.substring(2);
            	BaseEncoding base32Encoding = BaseEncoding.base32().lowerCase().omitPadding();
            	byte[] value = base32Encoding.decode(rawPayload);
            	byte[] payload = ArrayUtils.subarray(value, 0, value.length - 4);
            	return ArrayUtils.addAll(new byte[] {0x01}, payload);
            case ACTOR:
            	rawPayload = address.substring(2);
            	base32Encoding = BaseEncoding.base32().lowerCase().omitPadding();
            	value = base32Encoding.decode(rawPayload);
            	payload = ArrayUtils.subarray(value, 0, value.length - 4);
            	return ArrayUtils.addAll(new byte[] {0x02}, payload);
            case BLS:
                break;
            default:
                break;
        }
        return null;
    }
    
    @Getter
    @AllArgsConstructor
    public static enum FilProtocolIndicator {
        ID("0"),
        SECP256K1("1"),   //普通地址
        ACTOR("2"),       //合约地址
        BLS("3");         //矿工地址

        private String code;

        public static FilProtocolIndicator getByCode(String code){
            if(code == null){return null;}
            for (FilProtocolIndicator value : FilProtocolIndicator.values()) {
                if(value.code.equalsIgnoreCase(code)){
                    return value;
                }
            }
            return null;
        }
    }

}
