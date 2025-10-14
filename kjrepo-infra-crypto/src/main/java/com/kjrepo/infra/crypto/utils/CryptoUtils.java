package com.kjrepo.infra.crypto.utils;

import java.security.MessageDigest;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.crypto.cipher.CipherCrypto;
import com.kjrepo.infra.crypto.cipher.CipherInfo;
import com.kjrepo.infra.crypto.cipher.CipherPoolInfo;
import com.kjrepo.infra.crypto.digest.DigestInfo;
import com.kjrepo.infra.crypto.digest.DigestPoolInfo;
import com.kjrepo.infra.crypto.mac.MacInfo;
import com.kjrepo.infra.crypto.mac.MacPoolInfo;
import com.kjrepo.infra.crypto.signature.SignatureInfo;
import com.kjrepo.infra.crypto.signature.SignaturePoolInfo;

public class CryptoUtils {

	public static <T extends CipherCrypto> CipherPoolInfo pool(CipherInfo info,
			GenericObjectPoolConfig<Cipher> poolConfig) {
		CipherPoolInfo pool = new CipherPoolInfo();
		pool.setInfo(info);
		pool.setPoolConfig(poolConfig);
		return pool;
	}

	public static DigestPoolInfo pool(DigestInfo info, GenericObjectPoolConfig<MessageDigest> poolConfig) {
		DigestPoolInfo pool = new DigestPoolInfo();
		pool.setInfo(info);
		pool.setPoolConfig(poolConfig);
		return pool;
	}

	public static MacPoolInfo pool(MacInfo info, GenericObjectPoolConfig<Mac> poolConfig) {
		MacPoolInfo pool = new MacPoolInfo();
		pool.setInfo(info);
		pool.setPoolConfig(poolConfig);
		return pool;
	}

	public static SignaturePoolInfo pool(SignatureInfo info, GenericObjectPoolConfig<Signature> poolConfig) {
		SignaturePoolInfo pool = new SignaturePoolInfo();
		pool.setInfo(info);
		pool.setPoolConfig(poolConfig);
		return pool;
	}

}
