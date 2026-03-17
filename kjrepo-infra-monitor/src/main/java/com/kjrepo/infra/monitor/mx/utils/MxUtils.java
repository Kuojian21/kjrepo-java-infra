package com.kjrepo.infra.monitor.mx.utils;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformManagedObject;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import com.sun.management.UnixOperatingSystemMXBean;
import com.annimon.stream.Optional;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.number.N_humanUtils;

public class MxUtils {

	private static final LazySupplier<Map<Class<? extends PlatformManagedObject>, String>> mxbean_name_map = LazySupplier
			.wrap(() -> {
				Map<Class<? extends PlatformManagedObject>, String> map = Maps.newHashMap();
				map.put(ClassLoadingMXBean.class, ManagementFactory.CLASS_LOADING_MXBEAN_NAME);
				map.put(CompilationMXBean.class, ManagementFactory.COMPILATION_MXBEAN_NAME);
				map.put(OperatingSystemMXBean.class, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
				map.put(com.sun.management.OperatingSystemMXBean.class, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
				map.put(UnixOperatingSystemMXBean.class, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
				map.put(RuntimeMXBean.class, ManagementFactory.RUNTIME_MXBEAN_NAME);
				map.put(ThreadMXBean.class, ManagementFactory.THREAD_MXBEAN_NAME);
				map.put(GarbageCollectorMXBean.class, ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE);
				map.put(MemoryMXBean.class, ManagementFactory.MEMORY_MXBEAN_NAME);
				map.put(MemoryManagerMXBean.class, ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE);
				map.put(MemoryPoolMXBean.class, ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE);
				return map;
			});

	public static <D extends PlatformManagedObject> Map<Class<? extends PlatformManagedObject>, String> mxbean_names() {
		return mxbean_name_map.get();
	}

	public static <D extends PlatformManagedObject> String mxbean_name(Class<D> clazz) {
		return mxbean_name_map.get().get(clazz);
	}

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
