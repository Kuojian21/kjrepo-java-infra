package com.kjrepo.infra.crypto.signature;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.kjrepo.infra.crypto.utils.AlgoKeyUtils;

public class SignatureVertify extends SignatureCrypto {

	public SignatureVertify(SignatureInfo info) {
		super(info);
	}

	@Override
	protected Signature create() throws Exception {
		SignatureInfo info = info();
		PublicKey pubKey = AlgoKeyUtils.loadPublicKey(info.getKeyAlgorithm(), info.getKey());
		Signature signature = Signature.getInstance(info.getAlgorithm());
		signature.initVerify(pubKey);
		return signature;
	}

	public boolean verify(String sign, String data) {
		return verify( //
				Optional.ofNullable(sign).map(Base64.getDecoder()::decode).orElse(null),
				Optional.ofNullable(data).map(s -> s.getBytes(StandardCharsets.UTF_8)).orElse(null) //
		);
	}

	public boolean verify(byte[] sign, byte[]... datas) {
		if (sign == null && datas == null) {
			return true;
		} else if (sign == null || datas == null) {
			return false;
		}
		try {
			return execute(sig -> {
				for (byte[] data : Stream.of(datas).filter(p -> p != null).toList()) {
					sig.update(data);
				}
				return sig.verify(sign);
			});
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
	}

}
