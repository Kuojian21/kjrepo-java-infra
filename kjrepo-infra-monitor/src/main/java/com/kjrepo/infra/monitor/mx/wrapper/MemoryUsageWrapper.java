package com.kjrepo.infra.monitor.mx.wrapper;

import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.text.StringSubstitutor;

import com.annimon.stream.Optional;
import com.kjrepo.infra.common.utils.HumanUtils;

public class MemoryUsageWrapper {

	public static MemoryUsageWrapper of(MemoryUsage usage) {
		return new MemoryUsageWrapper(usage);
	}

	private final MemoryUsage usage;

	private MemoryUsageWrapper(MemoryUsage usage) {
		super();
		this.usage = usage;
	}

	public MemoryUsage usage() {
		return this.usage;
	}

	public String used() {
		return HumanUtils.formatByte(usage.getUsed());
	}

	public String committed() {
		return HumanUtils.formatByte(usage.getCommitted());
	}

	public String init() {
		return HumanUtils.formatByte(usage.getInit());
	}

	public String max() {
		return HumanUtils.formatByte(usage.getMax());
	}

	public double rate() {
		return Optional.of(usage.getCommitted()).filter(p -> p > 0)
				.map(p -> BigDecimal.valueOf(usage.getUsed() * 100.0d / usage.getCommitted())
						.setScale(2, RoundingMode.HALF_UP).doubleValue())
				.orElse(0.0d);
	}

	public String toString() {
		return usage == null ? "" : new StringSubstitutor(key -> {
			switch (key) {
			case "used":
				return used();
			case "committed":
				return committed();
			case "init":
				return init();
			case "max":
				return max();
			case "rate":
				return rate() + "";
			default:
				return "";
			}
		}).replace("used:${used} committed:${committed} init:${init} max:${max} rate:${rate}%");
	}

}
