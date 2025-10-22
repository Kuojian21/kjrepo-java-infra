package com.kjrepo.infra.storage.legacy;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.google.common.collect.Sets;
import com.kjrepo.infra.common.lazy.LazyRunnable;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.common.utils.StackUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.thread.pool.KrExecutorService;
import com.kjrepo.infra.thread.pool.KrExecutorServiceInfo;
import com.kjrepo.infra.thread.pool.KrExecutors;

public class XLuceneRepository {

	private final Logger logger = LoggerUtils.logger(getClass());
	private final KrExecutorService service = KrExecutors.newExecutor(new KrExecutorServiceInfo());
	private final Set<String> keys = Sets.newConcurrentHashSet();

	private final LazyRunnable notify;

	public XLuceneRepository() {
		Register<Long> register = RegisterFactory.getContext(StackUtils.firstBusinessInvokerClassname())
				.getRegister(Long.class);
		Runnable notifyTask = () -> {
			try {
				Stream.of(keys).forEach(key -> {
					keys.remove(key);
					register.set(key, System.currentTimeMillis());
				});
			} catch (Throwable e) {
				logger.error("", e);
			}
		};
		notify = LazyRunnable.wrap(() -> {
			Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(notifyTask, 5, 5, TimeUnit.SECONDS);
			TermHelper.addTerm("lucene", () -> {
				service.shutdownBlocking();
				notifyTask.run();
			});
		});
	}

	public void execute(String key, Runnable runnable) {
		notify.run();
		this.service.execute(() -> {
			runnable.run();
			this.keys.add(key);
		});
	}

}
