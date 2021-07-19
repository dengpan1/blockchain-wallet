package com.blockchain.wallet.filecoin;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bouncycastle.util.encoders.Hex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.google.common.base.Objects;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class FilSignatureMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	private Message message;
	private Signature signature;
	
	@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Message implements Serializable{
		private static final long serialVersionUID = 1L;
		private Long version;
		private String from;
		private String to;
		private Long nonce;
		private String value;
		private String gasFeeCap;
		private String gasPremium;
		private Long gasLimit;
		private Long method;
		private String params;
	}
	
	@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Signature implements Serializable{
		private static final long serialVersionUID = 1L;
		private Long type;
		private String data;
	}
}