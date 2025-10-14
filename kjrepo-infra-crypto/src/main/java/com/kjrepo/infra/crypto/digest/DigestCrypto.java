package com.kjrepo.infra.crypto.digest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.kjrepo.infra.common.executor.PooledInfoExecutor;
import com.kjrepo.infra.crypto.utils.Base64Utils;

public class DigestCrypto extends PooledInfoExecutor<MessageDigest, DigestInfo> {

	public DigestCrypto(DigestInfo info) {
		super(info);
	}

//	public DigestCrypto(DigestInfo info, GenericObjectPoolConfig<MessageDigest> poolConfig) {
//		super(info, poolConfig);
//	}

	public final String digest(String data) {
		if (data == null) {
			return null;
		}
		return Base64Utils.encodeToString(digest(
				Optional.ofNullable(data).map(str -> str.getBytes(StandardCharsets.UTF_8)).orElseGet(() -> null)));
	}

	public final byte[] digest(byte[]... datas) {
		if (datas == null) {
			return null;
		}
		return execute(digest -> {
			Optional.ofNullable(datas).ifPresent(
					bs -> Stream.of(bs).filter(bytes -> bytes != null).forEach(bytes -> digest.update(bytes)));
			return digest.digest();
		});
	}

	@Override
	protected MessageDigest create() throws Exception {
		return DigestUtils.digest(info());
	}

	@Override
	protected void init(MessageDigest digest) {
		digest.reset();
	}

}
