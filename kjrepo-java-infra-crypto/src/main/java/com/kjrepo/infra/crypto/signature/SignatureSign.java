package com.kjrepo.infra.crypto.signature;

import java.security.PrivateKey;
import java.security.Signature;
import java.util.List;

import com.kjrepo.infra.crypto.utils.AlgoKeyUtils;

public class SignatureSign extends SignatureCrypto {

	public SignatureSign(SignatureInfo info) {
		super(info);
	}

	@Override
	protected Signature create() throws Exception {
		SignatureInfo info = info();
		PrivateKey priKey = AlgoKeyUtils.loadPrivateKey(info.getKeyAlgorithm(), info.getKey());
		Signature signature = Signature.getInstance(info.getAlgorithm());
		signature.initSign(priKey);
		return signature;
	}

	public String sign(String data) {
		return encrypt(data);
	}

	public byte[] sign(byte[]... datas) {
		return crypt(datas);
	}

	@Override
	protected byte[] crypt(List<byte[]> datas) throws Exception {
		return execute(sig -> {
			for (byte[] src : datas) {
				sig.update(src);
			}
			return sig.sign();
		});
	}

}
