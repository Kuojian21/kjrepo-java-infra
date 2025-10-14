package com.kjrepo.infra.network.mail.receiver;

import javax.mail.Store;

import com.sun.mail.imap.IMAPStore;
import com.google.common.collect.ImmutableMap;
import com.kjrepo.infra.common.executor.PooledInfoExecutor;
import com.kjrepo.infra.network.mail.MailUtils;

public class MailReceiver extends PooledInfoExecutor<Store, MailReceiverInfo> {

	public MailReceiver(MailReceiverInfo info) {
		super(info);
	}

	@Override
	protected Store create() throws Exception {
		Store store = MailUtils.session(info()).getStore();
		store.connect();
		if (store instanceof IMAPStore) {
			((IMAPStore) store).id(ImmutableMap.of("name", "kjrepo"));
		}
		return store;
	}

}
