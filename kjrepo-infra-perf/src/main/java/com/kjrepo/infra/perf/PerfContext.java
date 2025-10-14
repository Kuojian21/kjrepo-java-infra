package com.kjrepo.infra.perf;

import java.util.concurrent.TimeUnit;

public class PerfContext {

	private final PerfLogger perfLogger;
	private final PerfLogTag perfLog;
	private long count = 1;
	private long micro;

	public PerfContext(PerfLogger perfLogger, PerfLogTag perfLog) {
		super();
		this.perfLogger = perfLogger;
		this.perfLog = perfLog;
	}

	public PerfLogTag getPerfLog() {
		return perfLog;
	}

	public long getCount() {
		return count;
	}

	public PerfContext count(long count) {
		this.count = count > 0 ? count : 1;
		return this;
	}

	public long getMicro() {
		return micro;
	}

	public PerfContext millis(long millis) {
		this.micro = TimeUnit.MILLISECONDS.toMicros(millis);
		return this;
	}

	public PerfContext micro(long micro) {
		this.micro = micro;
		return this;
	}

	public void logstash() {
		this.perfLogger.logstash(this);
	}
}
