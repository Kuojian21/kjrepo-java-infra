package com.kjrepo.infra.crypto.signature;

import java.security.Signature;
import java.util.List;

import com.kjrepo.infra.crypto.Crypto;

public abstract class SignatureCrypto extends Crypto<Signature, SignatureInfo> {

	public SignatureCrypto(SignatureInfo info) {
		super(info);
	}

	@Override
	protected byte[] crypt(List<byte[]> datas) throws Exception {
		throw new RuntimeException("unsupported!!!");
	}

}
