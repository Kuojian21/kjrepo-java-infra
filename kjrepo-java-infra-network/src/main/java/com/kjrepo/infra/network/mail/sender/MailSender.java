package com.kjrepo.infra.network.mail.sender;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import com.google.common.collect.Lists;
import com.kjrepo.infra.executor.lazy.LazyExecutor;
import com.kjrepo.infra.network.mail.MailInfo;
import com.kjrepo.infra.network.mail.MailUtils;

public class MailSender extends LazyExecutor<Session, MailInfo> {

	public MailSender(MailInfo info) {
		super(info, () -> MailUtils.session(info));
	}

	public void send(String fromNickname, List<String> to, String subject, String content) throws Exception {
		send(fromNickname, to, null, null, subject, content);
	}

	public void send(String fromNickname, List<String> to, List<String> cc, List<String> bcc, String subject,
			String content) throws MessagingException {
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(content, "text/html;charset=utf-8");
		send(fromNickname, to, cc, bcc, subject, Lists.newArrayList(bodyPart));
	}

	public void send(String fromNickname, List<String> to, List<String> cc, List<String> bcc, String subject,
			List<MimeBodyPart> bodyParts) throws MessagingException {
		execute(session -> {
			try {
				Transport.send(
						MailUtils.message(session, new InternetAddress(info().getAuth().getUsername(), fromNickname),
								to, cc, bcc, subject, bodyParts));
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	protected String tag() {
		String username = this.info().getAuth().getUsername();
		return username.substring(username.indexOf('@') + 1);
	}

}
