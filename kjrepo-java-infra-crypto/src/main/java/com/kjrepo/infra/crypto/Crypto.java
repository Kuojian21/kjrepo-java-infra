package com.kjrepo.infra.crypto;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.kjrepo.infra.executor.pool.PoolExecutor;

public abstract class Crypto<T, I extends CryptoInfo<T>> extends PoolExecutor<T, I> {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	protected Crypto(I info) {
		super(info);
	}

	public final String encrypt(String data) {
		return Optional.ofNullable(data).map(s -> s.getBytes(StandardCharsets.UTF_8)).map(this::crypt)
				.map(Base64.getEncoder()::encode).map(b -> new String(b, StandardCharsets.UTF_8)).orElse(null);
	}

	public final String decrypt(String data) {
		return Optional.ofNullable(data).map(s -> s.getBytes(StandardCharsets.UTF_8)).map(Base64.getDecoder()::decode)
				.map(this::crypt).map(b -> new String(b, StandardCharsets.UTF_8)).orElse(null);
	}

	public final byte[] crypt(byte[]... datas) {
		if (datas == null) {
			return null;
		}
		try {
			return crypt(Stream.of(datas).filter(data -> data != null).filter(data -> data.length > 0).toList());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract byte[] crypt(List<byte[]> datas) throws Exception;

	@Override
	protected String tag() {
		return info().getAlgorithm();
	}
}
