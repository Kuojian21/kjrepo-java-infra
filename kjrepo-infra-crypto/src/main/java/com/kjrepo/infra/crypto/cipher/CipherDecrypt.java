package com.kjrepo.infra.crypto.cipher;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;

//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.crypto.utils.Base64Utils;

public class CipherDecrypt extends CipherCrypto {

//	public CipherDecrypt(CipherInfo info, GenericObjectPoolConfig<Cipher> poolConfig) {
//		super(info, poolConfig);
//	}

	public CipherDecrypt(CipherInfo info) {
		super(info);
	}

	public String decrypt(String data) {
		if (data == null) {
			return null;
		}
		return new String(decrypt(Base64Utils.decode(data)), StandardCharsets.UTF_8);
	}

	public byte[] decrypt(byte[] data) {
		return crypt(data);
	}

	@Override
	protected Cipher create() throws Exception {
		return CipherUtils.decrypt(this.info());
	}

}
