package com.kjrepo.infra.crypto.signature;

import java.security.Signature;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import com.kjrepo.infra.common.executor.PooledInfoExecutor;

public abstract class SignatureCrypto extends PooledInfoExecutor<Signature, SignatureInfo> {

	public SignatureCrypto(SignatureInfo info) {
		super(info);
	}

//	public SignatureCrypto(SignatureInfo info, GenericObjectPoolConfig<Signature> poolConfig) {
//		super(info, poolConfig);
//	}

}
