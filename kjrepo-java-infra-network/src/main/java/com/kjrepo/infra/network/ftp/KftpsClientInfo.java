package com.kjrepo.infra.network.ftp;

import org.apache.commons.net.ftp.FTPSClient;

public class KftpsClientInfo extends KftpBeanInfo<FTPSClient> {
	private String protocol;
	private boolean isImplicit;
	private long pbsz = -1L;
	private String prot;

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public boolean isImplicit() {
		return isImplicit;
	}

	public void setImplicit(boolean isImplicit) {
		this.isImplicit = isImplicit;
	}

	public long getPbsz() {
		return pbsz;
	}

	public void setPbsz(long pbsz) {
		this.pbsz = pbsz;
	}

	public String getProt() {
		return prot;
	}

	public void setProt(String prot) {
		this.prot = prot;
	}

}
