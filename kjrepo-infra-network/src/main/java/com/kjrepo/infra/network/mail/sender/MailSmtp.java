package com.kjrepo.infra.network.mail.sender;

import java.io.Closeable;
import java.util.List;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import com.google.common.collect.Lists;
import com.kjrepo.infra.common.executor.PooledInfoExecutor;
import com.kjrepo.infra.network.mail.MailUtils;
import com.sun.mail.smtp.SMTPTransport;

public class MailSmtp extends PooledInfoExecutor<SMTPTransport, MailSmtpInfo> implements Closeable {

	public void send(String fromNickname, List<String> to, String subject, String content) throws Exception {
		send(fromNickname, to, null, null, subject, content);
	}

	public void send(String fromNickname, List<String> to, List<String> cc, List<String> bcc, String subject,
			String content) throws Exception {
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(content, "text/html;charset=utf-8");
		send(fromNickname, to, cc, bcc, subject, Lists.newArrayList(bodyPart));
	}

	public void send(String fromNickname, List<String> to, List<String> cc, List<String> bcc, String subject,
			List<MimeBodyPart> bodyParts) throws Exception {
		execute(transport -> {
			MimeMessage message = MailUtils.message(info(), fromNickname, to, cc, bcc, subject, bodyParts);
			transport.sendMessage(message, message.getAllRecipients());
		});
	}

	public MailSmtp(MailSmtpInfo info) {
		super(info);
	}

	@Override
	protected SMTPTransport create() throws Exception {
		SMTPTransport transport = (SMTPTransport) MailUtils.session(this.info()).getTransport();
		transport.connect();
		return transport;
	}

	@Override
	public void destroy(SMTPTransport bean) throws Exception {
		if (bean.isConnected()) {
			bean.close();
		}
	}

	@Override
	public boolean validate(SMTPTransport bean) {
		return bean.isConnected();
	}

}
