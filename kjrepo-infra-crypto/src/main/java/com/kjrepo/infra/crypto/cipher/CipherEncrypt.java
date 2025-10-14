package com.kjrepo.infra.crypto.cipher;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;

//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.crypto.utils.Base64Utils;

public class CipherEncrypt extends CipherCrypto {

//	public CipherEncrypt(CipherInfo info, GenericObjectPoolConfig<Cipher> poolConfig) {
//		super(info, poolConfig);
//	}

	public CipherEncrypt(CipherInfo info) {
		super(info);
	}

	public String encrypt(String data) {
		if (data == null) {
			return null;
		}
		return Base64Utils.encodeToString(encrypt(data.getBytes(StandardCharsets.UTF_8)));
	}

	public byte[] encrypt(byte[] data) {
		return crypt(data);
	}

	@Override
	protected Cipher create() throws Exception {
		return CipherUtils.encrypt(this.info());
	}

}
