package com.kjrepo.infra.monitor.mxbean.utils;

import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import com.annimon.stream.Optional;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.number.N_humanUtils;

public class MxbeanUtils {

	public static Map<String, Object> toMap(MemoryUsage usage) {
		Map<String, Object> data = Maps.newLinkedHashMap();
		Optional.ofNullable(usage).ifPresent(u -> {
			data.put("used", N_humanUtils.formatByte(u.getUsed()));
			data.put("committed", N_humanUtils.formatByte(u.getCommitted()));
			data.put("init", N_humanUtils.formatByte(u.getInit()));
			data.put("max", N_humanUtils.formatByte(u.getMax()));
			data.put("rate",
					Optional.of(usage.getCommitted()).filter(p -> p > 0)
							.map(p -> BigDecimal.valueOf(usage.getUsed() * 100.0d / usage.getCommitted())
									.setScale(2, RoundingMode.HALF_UP).doubleValue())
							.orElse(0.0d));
		});
		return data;
	}

}
