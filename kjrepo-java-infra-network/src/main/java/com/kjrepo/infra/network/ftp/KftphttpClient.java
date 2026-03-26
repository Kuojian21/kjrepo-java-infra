package com.kjrepo.infra.network.ftp;

import org.apache.commons.net.ftp.FTPHTTPClient;

public class KftphttpClient extends KftpBean<FTPHTTPClient, KftphttpClientInfo> {

	public KftphttpClient(KftphttpClientInfo info) {
		super(info);
	}

	@Override
	protected FTPHTTPClient create() throws Exception {
		return KftpUtils.ftpHttpClient(info());
	}

}
