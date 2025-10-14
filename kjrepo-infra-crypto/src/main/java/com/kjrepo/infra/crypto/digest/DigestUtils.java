package com.kjrepo.infra.crypto.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.kjrepo.infra.crypto.CryptoRuntimeException;

public class DigestUtils {

	public static MessageDigest digest(DigestInfo info) {
		try {
			return MessageDigest.getInstance(info.getAlgorithm());
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

}
