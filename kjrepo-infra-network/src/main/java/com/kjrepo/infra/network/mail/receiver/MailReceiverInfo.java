package com.kjrepo.infra.network.mail.receiver;

import javax.mail.Store;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.common.executor.PooledInfo;
import com.kjrepo.infra.network.mail.MailInfo;

public class MailReceiverInfo extends MailInfo implements PooledInfo<Store> {
	private GenericObjectPoolConfig<Store> poolConfig;

	@Override
	public GenericObjectPoolConfig<Store> getPoolConfig() {
		return poolConfig;
	}

	@Override
	public void setPoolConfig(GenericObjectPoolConfig<Store> poolConfig) {
		this.poolConfig = poolConfig;
	}

}
