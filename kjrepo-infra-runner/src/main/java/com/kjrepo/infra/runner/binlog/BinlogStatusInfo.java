package com.kjrepo.infra.runner.binlog;

public class BinlogStatusInfo {

	private String gtidSet;
	private String binlogFilename;
	private long binlogPosition;

	public String getGtidSet() {
		return gtidSet;
	}

	public void setGtidSet(String gtidSet) {
		this.gtidSet = gtidSet;
	}

	public String getBinlogFilename() {
		return binlogFilename;
	}

	public void setBinlogFilename(String binlogFilename) {
		this.binlogFilename = binlogFilename;
	}

	public long getBinlogPosition() {
		return binlogPosition;
	}

	public void setBinlogPosition(long binlogPosition) {
		this.binlogPosition = binlogPosition;
	}

	public BinlogStatusInfo clone() {
		BinlogStatusInfo info = new BinlogStatusInfo();
		info.gtidSet = this.gtidSet;
		info.binlogFilename = this.binlogFilename;
		info.binlogPosition = this.binlogPosition;
		return info;

	}

}
