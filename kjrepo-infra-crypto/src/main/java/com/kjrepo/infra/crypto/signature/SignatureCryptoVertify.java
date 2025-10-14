package com.kjrepo.infra.crypto.signature;

import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.SignatureException;

//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.kjrepo.infra.crypto.utils.Base64Utils;

public class SignatureCryptoVertify extends SignatureCrypto {

	public SignatureCryptoVertify(SignatureInfo info) {
		super(info);
	}

//	public SignatureCryptoVertify(SignatureInfo info, GenericObjectPoolConfig<Signature> poolConfig) {
//		super(info, poolConfig);
//	}

	public boolean verify(String sign, String data) {
		if (sign == null && data == null) {
			return true;
		} else if (sign == null || data == null) {
			return false;
		}
		return verify(Base64Utils.decode(sign), data.getBytes(StandardCharsets.UTF_8));
	}

	public boolean verify(byte[] sign, byte[]... datas) {
		if (sign == null && datas == null) {
			return true;
		} else if (sign == null || datas == null) {
			return false;
		}
		try {
			return execute(sig -> {
				for (byte[] data : Stream.of(Optional.ofNullable(datas).orElseGet(() -> new byte[0][0]))
						.filter(p -> p != null).toList()) {
					sig.update(data);
				}
				return sig.verify(sign);
			});
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Signature create() throws Exception {
		return SignatureUtils.pub(this.info());
	}
}
