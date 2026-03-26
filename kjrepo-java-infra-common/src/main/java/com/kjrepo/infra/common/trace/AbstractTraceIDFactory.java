package com.kjrepo.infra.common.trace;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.time.DateFormatUtils;

public class AbstractTraceIDFactory implements TraceIDFactory {

	private final AtomicLong no = new AtomicLong(0L);
	private final String perfix;
	private final String no_format;
	private final long no_mod;

	protected AbstractTraceIDFactory(String perfix, int noLen) {
		super();
		this.perfix = perfix;
		this.no_format = "%0" + noLen + "d";
		long nb = 10;
		for (int i = 1; i <= noLen; i++) {
			nb *= 10;
		}
		this.no_mod = nb;
	}

	@Override
	public final String generate() {
		return this.perfix + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")
				+ String.format(this.no_format, no.incrementAndGet() % this.no_mod);
	}

}
