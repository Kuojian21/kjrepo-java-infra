package com.kjrepo.infra.trace.utils;

import java.util.concurrent.atomic.AtomicLong;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;

import com.kjrepo.infra.common.lazy.LazySupplier;

public class TraceIDUtils {

	public static final String ID_REQUEST = "#TRACEID_KJREPO#";
	public static final String ID_LOGGER = "TRACEID";

	private static final ThreadLocal<String> TRACEID_HOLDER = new ThreadLocal<>();
	private static final AtomicLong number = new AtomicLong(0L);

	public static String get() {
		return TRACEID_HOLDER.get();
	}

	public static void set(String traceid) {
		TRACEID_HOLDER.set(traceid);
		logger(traceid);
	}

	public static void generate() {
		generate(null);
	}

	public static void generate(String traceid) {
		if (StringUtils.isEmpty(traceid)) {
			traceid = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS-")
					+ String.format("%04d", number.incrementAndGet() % 10000);
		}
		set(traceid);
	}

	public static void clear() {
		set(null);
	}

	private static void logger(String traceid) {
		if (log4j2.get()) {
			Log4j2.set(traceid);
		}
		if (slf4j2.get()) {
			Slf4j.set(traceid);
		}
	}

	private static final LazySupplier<Boolean> slf4j2 = LazySupplier.wrap(() -> {
		try {
			return Class.forName("org.slf4j.MDC") != null;
		} catch (Throwable e) {
			return false;
		}
	});

	private static final LazySupplier<Boolean> log4j2 = LazySupplier.wrap(() -> {
		try {
			return Class.forName("org.apache.logging.log4j.ThreadContext") != null;
		} catch (Throwable e) {
			return false;
		}
	});

	static class Log4j2 {
		static void set(String traceid) {
			ThreadContext.put(TraceIDUtils.ID_LOGGER, traceid);
		}
	}

	static class Slf4j {
		static void set(String traceid) {
			MDC.put(TraceIDUtils.ID_LOGGER, traceid);
		}
	}
}
