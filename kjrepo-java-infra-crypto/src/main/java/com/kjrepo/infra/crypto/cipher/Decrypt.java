package com.kjrepo.infra.crypto.cipher;

import javax.crypto.Cipher;

public class Decrypt extends CipherCrypto {

	public Decrypt(CipherInfo info) {
		super(info);
	}

	@Override
	protected Cipher create() throws Exception {
		return crypt(info(), Cipher.DECRYPT_MODE);
	}

	public byte[] decrypt(byte[]... datas) {
		return crypt(datas);
	}
}
