package com.kjrepo.infra.network.mail.sender;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.executor.pool.PoolExecutorInfo;
import com.sun.mail.smtp.SMTPTransport;

public class MailSmtpInfo extends MailSenderInfo implements PoolExecutorInfo<SMTPTransport> {

	private GenericObjectPoolConfig<SMTPTransport> poolConfig;

	@Override
	public GenericObjectPoolConfig<SMTPTransport> getPoolConfig() {
		return poolConfig;
	}

	@Override
	public void setPoolConfig(GenericObjectPoolConfig<SMTPTransport> poolConfig) {
		this.poolConfig = poolConfig;
	}

}
