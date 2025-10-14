package com.kjrepo.infra.network.mail.legacy;

import java.util.Date;
import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.base.Joiner;
import com.sun.mail.smtp.SMTPMessage;
import com.sun.mail.smtp.SMTPTransport;

public class MailSender extends Mail<SMTPTransport> {

	public MailSender(MailInfo<SMTPTransport> info) {
		super(info);
	}

	public void send(String from, List<String> to, List<String> cc, List<String> bcc, String subject, String text)
			throws MessagingException {
		execute(holder -> {
			SMTPMessage msg = init(holder, from, to, cc, bcc, subject);
			msg.setText(text);
			holder.getService().sendMessage(msg, msg.getAllRecipients());
		});
	}

	public void send(String from, List<String> to, List<String> cc, List<String> bcc, String subject,
			MimeBodyPart... bodyParts) throws MessagingException {
		execute(holder -> {
			SMTPMessage msg = init(holder, from, to, cc, bcc, subject);
			MimeMultipart multipartBody = new MimeMultipart();
			for (MimeBodyPart part : bodyParts) {
				multipartBody.addBodyPart(part);
			}
			holder.getService().sendMessage(msg, msg.getAllRecipients());
		});
	}

	private SMTPMessage init(MailSessionHolder<SMTPTransport> holder, String from, List<String> to, List<String> cc,
			List<String> bcc, String subject) throws MessagingException {
		SMTPMessage msg = new SMTPMessage(holder.getSession());
		if (from == null) {
			msg.setFrom();
		} else {
			msg.setFrom(new InternetAddress(from));
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
		return msg;
	}

	@Override
	protected MailSessionHolder<SMTPTransport> create() throws Exception {
		return MailUtils.sender(info());
	}

}
