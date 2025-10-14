package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ThreadIReporter extends AbstractIReporter<ThreadIReporterBean> {

	@Override
	public void report(ThreadIReporterBean data) {
		ThreadMXBean mxbean = data.data();
		long[] ids = mxbean.findDeadlockedThreads();
		if (ids == null) {

		} else {
			ThreadInfo[] infos = mxbean.getThreadInfo(ids, 1);
			logger.warn("thread {}", (Object) infos);
		}
	}

}
