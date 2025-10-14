package com.kjrepo.infra.crypto.signature;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import com.kjrepo.infra.crypto.CryptoRuntimeException;
import com.kjrepo.infra.crypto.utils.AlgoKeyUtils;

public class SignatureUtils {

	public static Signature pri(SignatureInfo info) {
		try {
			PrivateKey priKey = AlgoKeyUtils.loadPrivateKey(info.getKeyAlgorithm(), info.getKey());
			Signature signature = Signature.getInstance(info.getAlgorithm());
			signature.initSign(priKey);
			return signature;
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Signature pub(SignatureInfo info) {
		try {
			PublicKey pubKey = AlgoKeyUtils.loadPublicKey(info.getKeyAlgorithm(), info.getKey());
			Signature signature = Signature.getInstance(info.getAlgorithm());
			signature.initVerify(pubKey);
			return signature;
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

}
