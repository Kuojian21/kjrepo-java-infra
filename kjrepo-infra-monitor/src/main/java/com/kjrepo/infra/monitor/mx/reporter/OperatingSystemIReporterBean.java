package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.OperatingSystemMXBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.utils.HumanUtils;

public class OperatingSystemIReporterBean extends AbstractIReporterBean<OperatingSystemMXBean> {

	public OperatingSystemIReporterBean(OperatingSystemMXBean data) {
		super(data);
	}

	public String toString() {
		StringSubstitutor str = new StringSubstitutor(key -> {
			switch (key) {
			case "name":
				return data().getName();
			case "arch":
				return data().getArch();
			case "availableProcessors":
				return data().getAvailableProcessors() + "";
			case "systemLoadAverage":
				return data().getSystemLoadAverage() + "";
			case "committedVirtualMemorySize":
				return "" + HumanUtils.formatByte(
						((com.sun.management.OperatingSystemMXBean) data()).getCommittedVirtualMemorySize());
			case "totalMemorySize":
				return "" + HumanUtils
						.formatByte(((com.sun.management.OperatingSystemMXBean) data()).getTotalMemorySize());
			case "freeMemorySize":
				return "" + HumanUtils
						.formatByte(((com.sun.management.OperatingSystemMXBean) data()).getFreeMemorySize());
			case "totalSwapSpaceSize":
				return "" + HumanUtils
						.formatByte(((com.sun.management.OperatingSystemMXBean) data()).getTotalSwapSpaceSize());
			case "freeSwapSpaceSize":
				return "" + HumanUtils
						.formatByte(((com.sun.management.OperatingSystemMXBean) data()).getFreeSwapSpaceSize());
			case "cpuLoad":
				return "" + ((com.sun.management.OperatingSystemMXBean) data()).getCpuLoad();
			case "processCpuLoad":
				return "" + ((com.sun.management.OperatingSystemMXBean) data()).getProcessCpuLoad();
			case "processCpuTime":
				return "" + HumanUtils
						.formatNanos(((com.sun.management.OperatingSystemMXBean) data()).getProcessCpuTime());
			default:
				return "";
			}
		});
		if (data() instanceof com.sun.management.OperatingSystemMXBean) {
			return str.replace(StringUtils.join(
					Stream.of("name", "arch", "availableProcessors", "systemLoadAverage", "committedVirtualMemorySize",
							"totalMemorySize", "freeMemorySize", "totalSwapSpaceSize", "freeSwapSpaceSize", "cpuLoad",
							"processCpuLoad", "processCpuTime").map(i -> i + ":${" + i + "}").toList(),
					" "));
		}
		return str.replace(StringUtils.join(Stream.of("name", "arch", "availableProcessors", "systemLoadAverage")
				.map(i -> i + ":${" + i + "}").toList(), " "));
	}
}
