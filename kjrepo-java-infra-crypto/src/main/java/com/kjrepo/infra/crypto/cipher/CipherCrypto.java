package com.kjrepo.infra.crypto.cipher;

import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.StringUtils;

import com.kjrepo.infra.crypto.Crypto;
import com.kjrepo.infra.crypto.utils.AlgoKeyUtils;
import com.kjrepo.infra.crypto.utils.AlgoParameterUtils;

public abstract class CipherCrypto extends Crypto<Cipher, CipherInfo> {

	public static Cipher crypt(CipherInfo info, int mode) throws InvalidKeyException,
			InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance(info.getAlgorithm());
		Key key = null;
		if (info.getKeyType() == CipherInfoKeyType.PUB) {
			key = AlgoKeyUtils.loadPublicKey(info.getKeyAlgorithm(), info.getKey());
		} else if (info.getKeyType() == CipherInfoKeyType.PRI) {
			key = AlgoKeyUtils.loadPrivateKey(info.getKeyAlgorithm(), info.getKey());
		} else {
			key = AlgoKeyUtils.loadKey(info.getKeyAlgorithm(), info.getKey());
		}
		if (StringUtils.isNotEmpty(info.getPadding())) {
			IvParameterSpec ivp = AlgoParameterUtils.loadIvp(info.getPadding());
			cipher.init(mode, key, ivp);
		} else {
			cipher.init(mode, key);
		}
		return cipher;
	}

	public CipherCrypto(CipherInfo info) {
		super(info);
	}

	@Override
	protected byte[] crypt(List<byte[]> datas) throws Exception {
		return super.execute(cipher -> {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			for (byte[] data : datas) {
				byte[] bytes = cipher.update(data);
				if (bytes != null) {
					baos.write(bytes);
				}
			}
			baos.write(cipher.doFinal());
			return baos.toByteArray();
		});
	}

}
