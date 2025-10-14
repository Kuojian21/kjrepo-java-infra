package com.kjrepo.infra.network.mail.legacy;

import javax.mail.MessagingException;
import javax.mail.Service;
import javax.mail.Session;

import com.github.phantomthief.util.ThrowableFunction;

public class MailSessionHolder<S extends Service> implements AutoCloseable {

	private final MailInfo<S> info;
	private final Session session;
	private final S service;

	public MailSessionHolder(MailInfo<S> info, ThrowableFunction<Session, S, Exception> service) throws Exception {
		super();
		this.info = info;
		this.session = Session.getInstance(info.getProps(), null);
		session.setDebug(info.isDebug());
		this.service = service.apply(session);
	}

	public S getService() throws MessagingException {
		if (!this.service.isConnected()) {
			this.service.connect(info.getHost(), info.getPort(), info.getUser(), info.getPassword());
		}
		return this.service;
	}

	@Override
	public void close() throws Exception {
		if (this.service.isConnected()) {
			this.service.close();
		}
	}

	public Session getSession() {
		return session;
	}

	public MailInfo<S> getInfo() {
		return info;
	}
}
