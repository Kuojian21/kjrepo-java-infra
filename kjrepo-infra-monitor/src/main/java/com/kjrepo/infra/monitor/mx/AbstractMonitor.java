package com.kjrepo.infra.monitor.mx;

import static com.github.phantomthief.util.MoreFunctions.throwing;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.monitor.IMonitor;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public abstract class AbstractMonitor<D> implements IMonitor {

	private static final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	private static final Map<Class<?>, String> nameInterfaceMap = Maps.newHashMap();
	static {
		nameInterfaceMap.put(ClassLoadingMXBean.class, ManagementFactory.CLASS_LOADING_MXBEAN_NAME);
		nameInterfaceMap.put(CompilationMXBean.class, ManagementFactory.COMPILATION_MXBEAN_NAME);
		nameInterfaceMap.put(MemoryMXBean.class, ManagementFactory.MEMORY_MXBEAN_NAME);
		nameInterfaceMap.put(OperatingSystemMXBean.class, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
		nameInterfaceMap.put(com.sun.management.OperatingSystemMXBean.class,
				ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
		nameInterfaceMap.put(RuntimeMXBean.class, ManagementFactory.RUNTIME_MXBEAN_NAME);
		nameInterfaceMap.put(ThreadMXBean.class, ManagementFactory.THREAD_MXBEAN_NAME);
		nameInterfaceMap.put(GarbageCollectorMXBean.class, ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE);
		nameInterfaceMap.put(MemoryManagerMXBean.class, ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE);
		nameInterfaceMap.put(MemoryPoolMXBean.class, ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE);
	}

	protected final Logger logger = LoggerUtils.logger(getClass());

	private final Class<D> mxbeanInterface;
	private final String mxbeanName;

	@SuppressWarnings("unchecked")
	protected AbstractMonitor() {
		this.mxbeanInterface = (Class<D>) Lists
				.newArrayList(TypeMapperUtils.mapper(this.getClass()).get(AbstractMonitor.class).values()).get(0);
		this.mxbeanName = nameInterfaceMap.get(mxbeanInterface);
	}

	protected final D bean() {
		try {
			return ManagementFactory.newPlatformMXBeanProxy(server, mxbeanName, mxbeanInterface);
		} catch (IOException e) {
			logger.error("", e);
			throw new RuntimeException();
		}
	}

	protected final List<D> beans() {
		try {
			return server.queryNames(new ObjectName(mxbeanName + ",*"), null).stream().map(objectName -> throwing(
					() -> ManagementFactory.newPlatformMXBeanProxy(server, objectName.toString(), mxbeanInterface)))
					.collect(toList());
		} catch (MalformedObjectNameException e) {
			logger.error("", e);
			throw new RuntimeException();
		}
	}

}
