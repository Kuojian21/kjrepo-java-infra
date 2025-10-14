package com.kjrepo.infra.crypto.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.StringUtils;

import com.kjrepo.infra.crypto.CryptoRuntimeException;
import com.kjrepo.infra.crypto.utils.AlgoKeyUtils;

public class CipherUtils {

	public static Cipher encrypt(CipherInfo info) {
		return crypt(info, Cipher.ENCRYPT_MODE);
	}

	public static Cipher decrypt(CipherInfo info) {
		return crypt(info, Cipher.DECRYPT_MODE);
	}

	public static Cipher crypt(CipherInfo info, int mode) {
		try {
			Cipher cipher = Cipher.getInstance(info.getAlgorithm());
			Key key = null;
			if (info.getKeyType() == CipherInfo.KEYTYPE.PUBKEY) {
				key = AlgoKeyUtils.loadPublicKey(info.getKeyAlgorithm(), info.getKey());
			} else if (info.getKeyType() == CipherInfo.KEYTYPE.PRIKEY) {
				key = AlgoKeyUtils.loadPrivateKey(info.getKeyAlgorithm(), info.getKey());
			} else {
				key = AlgoKeyUtils.loadKey(info.getKeyAlgorithm(), info.getKey());
			}
			if (StringUtils.isNotEmpty(info.getPadding())) {
				IvParameterSpec ivp = AlgoKeyUtils.loadIvp(info.getPadding());
				cipher.init(mode, key, ivp);
			} else {
				cipher.init(mode, key);
			}
			return cipher;
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException e) {
			throw new CryptoRuntimeException(e);
		}
	}

}
