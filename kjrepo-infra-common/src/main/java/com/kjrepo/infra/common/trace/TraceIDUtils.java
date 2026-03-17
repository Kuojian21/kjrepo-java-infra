package com.kjrepo.infra.common.trace;

import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;

import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazySupplier;

public class TraceIDUtils {

	public static final String ID_REQUEST = "#TRACEID#";
	public static final String ID_LOGGER = "TRACEID";

	private static final ThreadLocal<String> TRACEID_HOLDER = new ThreadLocal<>();
	private static final TraceIDFactory factory;
	static {
		List<TraceIDFactory> factories = Lists.newArrayList(ServiceLoader.load(TraceIDFactory.class));
		if (factories.size() == 0) {
			factory = new SimpleTraceIDFactory();
		} else if (factories.size() == 1) {
			factory = factories.get(0);
		} else {
			throw new RuntimeException("The trace-id-factory's counter is greater than 1!!!");
		}
	}

	public static String get() {
		return TRACEID_HOLDER.get();
	}

	public static void set(String traceID) {
		TRACEID_HOLDER.set(traceID);
		logger(traceID);
	}

	public static void generate() {
		generate(null);
	}

	public static void generate(String traceID) {
		if (StringUtils.isEmpty(traceID)) {
			traceID = factory.generate();
		}
		set(traceID);
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
