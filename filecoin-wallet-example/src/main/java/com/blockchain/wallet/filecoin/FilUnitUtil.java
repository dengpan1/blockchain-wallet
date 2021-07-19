package com.blockchain.wallet.filecoin;

import java.math.BigDecimal;
import java.math.BigInteger;

public class FilUnitUtil {
	private static final int decimal = 18;	
	
	public static BigInteger big2Small(BigDecimal amount) {
		return amount.movePointRight(decimal).toBigInteger();
	}
	
	public static BigDecimal small2Big(BigInteger amount) {
		return new BigDecimal(amount).movePointLeft(decimal).stripTrailingZeros();
	}
}
