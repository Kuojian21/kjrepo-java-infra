package com.kjrepo.infra.network.mail.sender;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import com.google.common.collect.Lists;
import com.kjrepo.infra.common.executor.LazyInfoExecutor;
import com.kjrepo.infra.network.mail.MailInfo;
import com.kjrepo.infra.network.mail.MailUtils;

public class MailSender extends LazyInfoExecutor<Session, MailInfo> {

	public MailSender(MailInfo info) {
		super(info, () -> MailUtils.session(info));
	}

	public void send(String fromNickname, List<String> to, String subject, String content) throws Exception {
		send(fromNickname, to, null, null, subject, content);
	}

	public void send(String fromNickname, List<String> to, List<String> cc, List<String> bcc, String subject,
			String content) throws Exception {
//		try {
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(content, "text/html;charset=utf-8");
		send(fromNickname, to, cc, bcc, subject, Lists.newArrayList(bodyPart));
//		} catch (MessagingException e) {
//			logger.error("", e);
//		}
	}

	public void send(String fromNickname, List<String> to, List<String> cc, List<String> bcc, String subject,
			List<MimeBodyPart> bodyParts) throws MessagingException {
//		try {
		execute(session -> {
			try {
				Transport.send(
						MailUtils.message(session, new InternetAddress(info().getAuth().getUsername(), fromNickname),
								to, cc, bcc, subject, bodyParts));
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		});
//		} catch (Exception e) {
//			logger.error("", e);
//		}
	}

}
