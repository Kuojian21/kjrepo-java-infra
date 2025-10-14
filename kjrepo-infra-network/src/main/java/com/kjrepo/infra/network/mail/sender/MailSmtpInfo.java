package com.kjrepo.infra.network.mail.sender;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.common.executor.PooledInfo;
import com.sun.mail.smtp.SMTPTransport;

public class MailSmtpInfo extends MailSenderInfo implements PooledInfo<SMTPTransport> {

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
