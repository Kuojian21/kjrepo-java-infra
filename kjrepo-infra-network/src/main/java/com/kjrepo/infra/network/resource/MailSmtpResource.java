package com.kjrepo.infra.network.resource;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.network.mail.sender.MailSmtp;
import com.kjrepo.infra.network.mail.sender.MailSmtpInfo;
import com.kjrepo.infra.register.resource.IResource;

public interface MailSmtpResource extends IResource<MailSmtpInfo, MailSmtp> {

	default Function<MailSmtpInfo, MailSmtp> mapper() {
		return info -> new MailSmtp(info);
	}

}
