package com.kjrepo.infra.crypto.signature;

import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.SignatureException;

//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.kjrepo.infra.crypto.utils.Base64Utils;

public class SignatureCryptoSign extends SignatureCrypto {
	public SignatureCryptoSign(SignatureInfo info) {
		super(info);
	}

//	public SignatureCryptoSign(SignatureInfo info, GenericObjectPoolConfig<Signature> poolConfig) {
//		super(info, poolConfig);
//	}

	public String sign(String data) {
		if (data == null) {
			return null;
		}
		return Base64Utils.encodeToString(sign(data.getBytes(StandardCharsets.UTF_8)));
	}

	public byte[] sign(byte[]... datas) {
		if (datas == null) {
			return null;
		}
		try {
			return execute(sig -> {
				for (byte[] src : Stream.of(Optional.ofNullable(datas).orElseGet(() -> new byte[0][0]))
						.filter(p -> p != null).toList()) {
					sig.update(src);
				}
				return sig.sign();
			});
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Signature create() throws Exception {
		return SignatureUtils.pri(this.info());
	}

}
