package com.kjrepo.infra.network.mail.legacy;

import javax.mail.Store;

import com.sun.mail.smtp.SMTPTransport;

public class MailUtils {

	public static MailSessionHolder<SMTPTransport> sender(MailInfo<SMTPTransport> info) throws Exception {
		return new MailSessionHolder<>(info, session -> (SMTPTransport) session.getTransport(info.getProtocol()));
	}

	public static MailSessionHolder<Store> reciver(MailInfo<Store> info) throws Exception {
		return new MailSessionHolder<>(info, session -> session.getStore(info.getProtocol()));
	}
}
