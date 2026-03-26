package com.kjrepo.infra.network.mail;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.base.Joiner;
import com.kjrepo.infra.network.mail.MailInfo.AuthInfo;

public class MailUtils {

	public static Session session(MailInfo info) {
		AuthInfo auth = info.getAuth();
		if (auth == null) {
			return Session.getInstance(info.getProps());
		} else {
			return Session.getInstance(info.getProps(), new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(auth.getUsername(), auth.getPassword());
				}
			});
		}
	}

	public static MimeMessage message(MailInfo info, String fromNickname, List<String> to, List<String> cc,
			List<String> bcc, String subject, List<MimeBodyPart> bodyParts)
			throws MessagingException, UnsupportedEncodingException {
		return message(session(info), new InternetAddress(info.getAuth().getUsername(), fromNickname), to, cc, bcc,
				subject, bodyParts);
	}

	public static MimeMessage message(MailInfo info, InternetAddress from, List<String> to, List<String> cc,
			List<String> bcc, String subject, List<MimeBodyPart> bodyParts) throws MessagingException {
		return message(session(info), from, to, cc, bcc, subject, bodyParts);
	}

	public static MimeMessage message(Session session, InternetAddress from, List<String> to, List<String> cc,
			List<String> bcc, String subject, List<MimeBodyPart> bodyParts) throws MessagingException {
		MimeMessage msg = new MimeMessage(session);
		if (from == null) {
			msg.setFrom();
		} else {
			msg.setFrom(from);
		}
		msg.setRecipients(Message.RecipientType.TO, Joiner.on(",").join(to));
		if (CollectionUtils.isNotEmpty(cc)) {
			msg.setRecipients(Message.RecipientType.CC, Joiner.on(",").join(cc));
		}
		if (CollectionUtils.isNotEmpty(bcc)) {
			msg.setRecipients(Message.RecipientType.BCC, Joiner.on(",").join(bcc));
		}
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		MimeMultipart multipartBody = new MimeMultipart();
		for (MimeBodyPart part : bodyParts) {
			multipartBody.addBodyPart(part);
		}
		msg.setContent(multipartBody);
		return msg;
	}

}
