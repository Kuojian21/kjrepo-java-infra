package com.kjrepo.infra.network.ftp;

import org.apache.commons.net.ftp.FTPClient;

public class KftpClient extends KftpBean<FTPClient, KftpBeanInfo<FTPClient>> {

	public KftpClient(KftpBeanInfo<FTPClient> info) {
		super(info);
	}

	@Override
	protected FTPClient create() throws Exception {
		return KftpUtils.ftpClient(info());
	}

}
