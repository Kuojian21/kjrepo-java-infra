package com.kjrepo.infra.network.ftp;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import com.kjrepo.infra.network.NetworkException;
import com.kjrepo.infra.network.NetworkRuntimeException;

public class KftpUtils {

	public static FTPClient ftpClient(KftpBeanInfo<FTPClient> info) throws NetworkException {
		try {
			FTPClient ftp = new FTPClient();
			ftp.connect(info.getHostname(), info.getPort());
			ftp.login(info.getUsername(), info.getPassword());
			ftp.setConnectTimeout(info.getConnectTimeout());
			ftp.setControlEncoding(info.getControlEncoding());
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				throw new NetworkException("ERROR");
			}
			return ftp;
		} catch (Exception e) {
			throw new NetworkException(e);
		}
	}

	public static FTPHTTPClient ftpHttpClient(KftphttpClientInfo info) throws NetworkException {
		try {
			FTPHTTPClient ftp = new FTPHTTPClient(info.getProxyHost(), info.getPort(), info.getProxyUser(),
					info.getProxyPass(), info.getEncoding() == null ? StandardCharsets.UTF_8
							: Charset.forName(info.getEncoding().toString()));
			ftp.connect(info.getHostname(), info.getPort());
			ftp.login(info.getUsername(), info.getPassword());
			ftp.setConnectTimeout(info.getConnectTimeout());
			ftp.setControlEncoding(info.getControlEncoding());
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				throw new NetworkRuntimeException("ERROR");
			}
			return ftp;
		} catch (Exception e) {
			throw new NetworkException(e);
		}
	}

	public static FTPSClient ftpsClient(KftpsClientInfo info) throws NetworkException {
		try {
			FTPSClient ftp = StringUtils.isEmpty(info.getProtocol()) ? new FTPSClient(info.isImplicit())
					: new FTPSClient(info.getProtocol(), info.isImplicit());
			ftp.connect(info.getHostname(), info.getPort());
			if (info.getPbsz() >= 0) {
				ftp.execPBSZ(info.getPbsz());
			}
			ftp.execPROT(info.getProt());
			ftp.login(info.getUsername(), info.getPassword());
			ftp.setConnectTimeout(info.getConnectTimeout());
			ftp.setControlEncoding(info.getControlEncoding());
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				throw new NetworkRuntimeException("ERROR");
			}
			return ftp;
		} catch (Exception e) {
			throw new NetworkException(e);
		}
	}

}
