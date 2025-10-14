package com.kjrepo.infra.crypto.mac;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

import javax.crypto.Mac;

//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.common.executor.PooledInfoExecutor;
import com.kjrepo.infra.crypto.utils.Base64Utils;

public class MacCrypto extends PooledInfoExecutor<Mac, MacInfo> {

	public MacCrypto(MacInfo info) {
		super(info);
	}

//	public MacCrypto(MacInfo info, GenericObjectPoolConfig<Mac> poolConfig) {
//		super(info, poolConfig);
//	}

	public final String mac(String data) {
		if (data == null) {
			return null;
		}
		return Base64Utils.encodeToString(
				mac(Optional.ofNullable(data).map(str -> str.getBytes(StandardCharsets.UTF_8)).orElseGet(() -> null)));
	}

	public final byte[] mac(byte[]... datas) {
		if (datas == null) {
			return null;
		}
		return execute(mac -> {
			Optional.ofNullable(datas)
					.ifPresent(bs -> Stream.of(bs).filter(bytes -> bytes != null).forEach(bytes -> mac.update(bytes)));
			return mac.doFinal();
		});
	}

	@Override
	protected void init(Mac mac) {
		mac.reset();
	}

	@Override
	protected Mac create() throws Exception {
		return MacUtils.mac(info());
	}

}
