package com.kjrepo.infra.crypto.cipher;

import javax.crypto.Cipher;

public class Encrypt extends CipherCrypto {

	public Encrypt(CipherInfo info) {
		super(info);
	}

	@Override
	protected Cipher create() throws Exception {
		return crypt(info(), Cipher.ENCRYPT_MODE);
	}

	public byte[] encrypt(byte[]... datas) {
		return crypt(datas);
	}

}
