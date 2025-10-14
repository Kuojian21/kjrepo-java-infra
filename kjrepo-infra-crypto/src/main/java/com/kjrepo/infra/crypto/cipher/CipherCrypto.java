package com.kjrepo.infra.crypto.cipher;

import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import com.kjrepo.infra.common.executor.PooledInfoExecutor;

public abstract class CipherCrypto extends PooledInfoExecutor<Cipher, CipherInfo> {

	public CipherCrypto(CipherInfo info) {
		super(info);
	}

//	public CipherCrypto(CipherInfo info, GenericObjectPoolConfig<Cipher> poolConfig) {
//		super(info, poolConfig);
//	}

	public byte[] crypt(byte[] data) {
		if (data == null || data.length == 0) {
			return data;
		}
		try {
			return execute(cipher -> {
				return cipher.doFinal(data);
			});
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * update(byte[]) + final() do not work!!!
	 * 
	 */
//	public final byte[] encrypt(byte[]... srcs) {
//		return crypt(this.encrypt, srcs);
//	}
//
//	public final byte[] decrypt(byte[]... srcs) {
//		return crypt(this.decrypt, srcs);
//	}
//
//	public final byte[] crypt(Cipher cipher, byte[]... srcs) {
//		if (srcs == null) {
//			return null;
//		}
//		return crypt(cipher, cp -> {
//			Stream.of(Optional.ofNullable(srcs).orElseGet(() -> new byte[0][0])).filter(p -> p != null)
//					.forEach(cipher::update);
//			return cp.doFinal();
//		});
//	}

}
