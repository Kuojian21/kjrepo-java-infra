package com.kjrepo.infra.network.jsch.sftp;

import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.kjrepo.infra.network.jsch.JschBean;

public class JschSftp extends JschBean<ChannelSftp, JschSftpInfo> {

	public JschSftp(JschSftpInfo info) {
		super("sftp", info);
	}

	public void upload(String sftpPath, String sftpFile, InputStream inputStream) throws SftpException {
		super.execute(sftp -> {
			String pwd = sftp.pwd();
			try {
				try {
					sftp.cd(sftpPath);
				} catch (SftpException e) {
					if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
						sftp.mkdir(sftpPath);
						sftp.cd(sftpPath);
					} else {
						throw e;
					}
				}
				sftp.put(inputStream, sftpFile);
			} finally {
				sftp.cd(pwd);
			}
		});
	}

	public void download(String sftpPath, String sftpFile, OutputStream outputStream) throws Exception {
		super.execute(sftp -> {
			String pwd = sftp.pwd();
			try {
				sftp.cd(sftpPath);
				sftp.get(sftpFile, outputStream);
				outputStream.flush();
			} finally {
				sftp.cd(pwd);
			}
		});
	}

}
