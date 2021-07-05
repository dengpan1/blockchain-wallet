package com.blockchain.wallet.bitcoin;

import java.security.SecureRandom;
import java.util.List;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script.ScriptType;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;

import com.google.common.base.Joiner;

/**
 * HD钱包工具
 * 
 * @author dengpan
 *
 */
public class HDWalletUtil {
	/**
	 * 密钥推导算法
	 * 
	 * @param masterKey    根密钥
	 * @param addressIndex 地址索引
	 * @param hdcoin       代币类型
	 * @return
	 */
	public static DeterministicKey fromBIP44HDpath(DeterministicKey masterKey, int addressIndex, int coinType) {
		DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, 44 | ChildNumber.HARDENED_BIT);
		DeterministicKey rootKey = HDKeyDerivation.deriveChildKey(purposeKey, coinType | ChildNumber.HARDENED_BIT);
		DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(rootKey, 0 | ChildNumber.HARDENED_BIT);
		DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey, 0);
		return HDKeyDerivation.deriveChildKey(changeKey, addressIndex);
	}

	/**
	 * 生成24长度助记词
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<String> generateMnemonicCode() throws Exception {
		MnemonicCode mnemonicCode = new MnemonicCode();
		SecureRandom secureRandom = new SecureRandom();
		byte[] initialEntropy = new byte[32];
		secureRandom.nextBytes(initialEntropy);
		return mnemonicCode.toMnemonic(initialEntropy);
	}

	public static void main(String s[]) throws Exception {
		System.out.println(Joiner.on(" ").join(generateMnemonicCode()));
		/*String mnemonicCode = "banana promote evidence piece worth annual purse member ivory start method wild clap hole seek topic night stairs behind maze liar boat dress ramp";
		byte[] seed = MnemonicUtils.generateSeed(mnemonicCode, "");
		DeterministicKey rootPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
		MainNetParams params = MainNetParams.get();
		for(int i = 0;i < 10;i++) {
			DeterministicKey eCKey = fromBIP44HDpath(rootPrivateKey, i,0);
			byte[] publicKey = eCKey.getPubKey();
			ECKey uncompressedChildKey = ECKey.fromPublicOnly(publicKey).decompress();
			String hexK = uncompressedChildKey.getPublicKeyAsHex().substring(2);
			String address = Keys.toChecksumAddress(Keys.getAddress(hexK));
			System.out.println(address);
			//ECKey uncompressedChildKey = ECKey.fromPublicOnly(eCKey.getPubKey()).decompress();
			//String hexK = uncompressedChildKey.getPublicKeyAsHex().substring(2);
			//System.out.println(eCKey.getPrivateKeyAsHex());
			
			String address = LegacyAddress.fromKey(params, eCKey).toBase58();
			System.out.println(address);
		}*/
	}

	private HDWalletUtil() {
	}
}