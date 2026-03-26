package com.kjrepo.infra.network.ftp;

import org.apache.commons.net.ftp.FTPSClient;

public class KftpsClient extends KftpBean<FTPSClient, KftpsClientInfo> {

	public KftpsClient(KftpsClientInfo info) {
		super(info);
	}

	@Override
	protected FTPSClient create() throws Exception {
		return KftpUtils.ftpsClient(info());
	}

}
