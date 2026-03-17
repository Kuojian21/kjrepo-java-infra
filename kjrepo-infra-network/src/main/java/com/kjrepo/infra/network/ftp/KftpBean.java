package com.kjrepo.infra.network.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;

import com.kjrepo.infra.executor.pool.PoolExecutor;

public abstract class KftpBean<B extends FTPClient, I extends KftpBeanInfo<B>> extends PoolExecutor<B, I> {

	public KftpBean(I info) {
		super(info);
	}

	public final boolean upload(String ftpPath, String ftpFile, InputStream inputStream) throws IOException {
		return execute(client -> {
			client.enterLocalPassiveMode();
			client.setFileType(B.BINARY_FILE_TYPE);
			if (!client.changeWorkingDirectory(ftpPath)) {
				client.makeDirectory(ftpPath);
				client.changeWorkingDirectory(ftpPath);
			}
			return client.storeFile(ftpFile, inputStream);
		}, "upload");
	}

	public final boolean download(String ftpPath, String ftpFile, OutputStream outputStream) throws IOException {
		return execute(client -> {
			client.enterLocalPassiveMode();
			client.setFileType(B.BINARY_FILE_TYPE);
			if (!client.changeWorkingDirectory(ftpPath)) {
				client.makeDirectory(ftpPath);
				client.changeWorkingDirectory(ftpPath);
			}
			if (client.retrieveFile(ftpFile, outputStream)) {
				outputStream.flush();
				return true;
			}
			return false;
		}, "download");
	}

	static final ThreadLocal<String> DIR = new ThreadLocal<>();

	public void before(B bean) {
		try {
			DIR.set(bean.printWorkingDirectory());
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	public void after(B bean) {
		try {
			bean.changeWorkingDirectory(DIR.get());
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	@Override
	public boolean validate(B bean) {
		return bean.isConnected();
	}

	@Override
	public void destroy(B bean) throws IOException {
		bean.disconnect();
	}

	@Override
	protected String tag() {
		return info().getHostname();
	}
}
