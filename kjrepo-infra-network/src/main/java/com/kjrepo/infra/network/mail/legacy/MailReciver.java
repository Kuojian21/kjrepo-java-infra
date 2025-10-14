package com.kjrepo.infra.network.mail.legacy;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MailReciver extends Mail<Store> {

	public MailReciver(MailInfo<Store> info) {
		super(info);
	}

	public List<Folder> folders() throws Exception {
		return execute(holder -> {
			return dump(holder.getService().getDefaultFolder(), Lists.newArrayList());
		});
	}

	public Map<Folder, Message[]> messages() throws Exception {
		return execute(holder -> {
			return dump(holder.getService().getDefaultFolder(), Maps.newHashMap());
		});
	}

	public List<Folder> dump(Folder folder, List<Folder> folders) throws MessagingException, IOException {
		folders.add(folder);
		if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0) {
			for (Folder f : folder.list()) {
				dump(f, folders);
			}
		}
		return folders;
	}

	public Map<Folder, Message[]> dump(Folder folder, Map<Folder, Message[]> msgs)
			throws MessagingException, IOException {
		if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
			folder.open(Folder.READ_ONLY);
			msgs.put(folder, folder.getMessages());
			folder.close(false);

		}
		if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0) {
			for (Folder f : folder.list()) {
				dump(f, msgs);
			}
		}
		return msgs;
	}

	@Override
	protected MailSessionHolder<Store> create() throws Exception {
		return MailUtils.reciver(info());
	}

}
