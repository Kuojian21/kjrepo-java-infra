package com.kjrepo.infra.monitor.mx;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.PlatformManagedObject;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.utils.RunUtils;
import com.kjrepo.infra.monitor.IMonitor;
import com.kjrepo.infra.monitor.mx.utils.MxUtils;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public abstract class AbstractMonitor<D extends PlatformManagedObject> implements IMonitor {

	private static final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	static {
		Stream.of(server.getDomains()).forEach(domain -> logger.info("domain:{}", domain));
	}

	private final Class<D> mxbeanInterface;
	private final String mxbeanName;

	@SuppressWarnings("unchecked")
	protected AbstractMonitor() {
		this.mxbeanInterface = (Class<D>) Lists
				.newArrayList(TypeMapperUtils.mapper(this.getClass()).get(AbstractMonitor.class).values()).get(0);
		this.mxbeanName = MxUtils.mxbean_name(mxbeanInterface);
	}

	protected final D bean() {
		try {
			return ManagementFactory.newPlatformMXBeanProxy(server, mxbeanName, mxbeanInterface);
		} catch (IOException e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}

	protected final List<D> beans() {
		try {
			return server.queryNames(new ObjectName(mxbeanName + ",*"), null).stream()
					.map(objectName -> RunUtils.throwing(() -> ManagementFactory.newPlatformMXBeanProxy(server,
							objectName.toString(), mxbeanInterface)))
					.collect(toList());
		} catch (MalformedObjectNameException e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}

}
