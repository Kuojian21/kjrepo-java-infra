package com.kjrepo.infra.network.mail.legacy;

import javax.mail.MessagingException;
import javax.mail.Service;
import org.slf4j.Logger;

import com.kjrepo.infra.common.executor.PooledInfoExecutor;
import com.kjrepo.infra.common.logger.LoggerUtils;

public abstract class Mail<S extends Service> extends PooledInfoExecutor<MailSessionHolder<S>, MailInfo<S>> {

	protected final Logger logger = LoggerUtils.logger();

	public Mail(MailInfo<S> info) {
		super(info);
	}

	@Override
	public void destroy(MailSessionHolder<S> bean) throws Exception {
		bean.close();
	}

	@Override
	public boolean validate(MailSessionHolder<S> bean) {
		try {
			return bean.getService().isConnected();
		} catch (MessagingException e) {
			logger.error("", e);
			return false;
		}
	}
}
