package com.kjrepo.infra.network.resource;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.network.mail.sender.MailSender;
import com.kjrepo.infra.network.mail.sender.MailSenderInfo;
import com.kjrepo.infra.register.resource.IResource;

public interface MailSenderResource extends IResource<MailSenderInfo, MailSender> {

	default Function<MailSenderInfo, MailSender> mapper() {
		return info -> new MailSender(info);
	}

}
