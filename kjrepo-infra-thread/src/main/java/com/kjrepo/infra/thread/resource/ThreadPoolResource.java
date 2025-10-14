package com.kjrepo.infra.thread.resource;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.register.resource.IResource;
import com.kjrepo.infra.thread.pool.KExecutorService;
import com.kjrepo.infra.thread.pool.KThreadFactory;
import com.kjrepo.infra.thread.pool.KThreadPoolInfo;
import com.kjrepo.infra.thread.pool.KThreadPoolUtils;

public interface ThreadPoolResource extends IResource<KThreadPoolInfo, KExecutorService> {

	default Function<KThreadPoolInfo, KExecutorService> mapper() {
		return info -> {
			if (info.getThreadFactory() == null) {
				KThreadFactory threadFactory = new KThreadFactory();
				threadFactory.setNamePrefix(namePrefix());
				info.setThreadFactory(threadFactory);
			}
			return KThreadPoolUtils.newExecutor(info);
		};
	}

	default String namePrefix() {
		return "kjrepo-thread-pool";
	}

}
