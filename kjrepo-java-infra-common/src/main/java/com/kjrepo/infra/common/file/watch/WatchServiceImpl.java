package com.kjrepo.infra.common.file.watch;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazyRunnable;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.utils.RunUtils;

/**
 * 不稳定
 */
public class WatchServiceImpl implements IWatch {

	public static WatchServiceImpl of() {
		return RunUtils.throwing(WatchServiceImpl::new);
	}

	private final Logger logger = LoggerUtils.logger(getClass());
	private final ConcurrentMap<String, Map<WatchEvent.Kind<Path>, WatchServiceHandlers>> handlers = Maps
			.newConcurrentMap();
	private final LazySupplier<WatchServiceHolder> watcher;
	private final LazyRunnable runnable;

	@SuppressWarnings("unchecked")
	private WatchServiceImpl() throws IOException {
		this.watcher = LazySupplier.wrap(() -> {
			return RunUtils.throwing(() -> {
				WatchServiceHolder holder = WatchServiceHolder.of();
				handlers.forEach((key, val) -> {
					val.keySet().forEach(event -> holder.register(key, event));
				});
				return holder;
			});
		});
		this.runnable = LazyRunnable.wrap(() -> {
			new Thread(() -> {
				while (true) {
					try {
						WatchServiceHolder holder = watcher.get();
						WatchKey key = holder.service().take();
						Optional.ofNullable(holder.path(key)).ifPresent(path -> {
							for (WatchEvent<?> event : key.pollEvents()) {
								handlers.get(path).get((WatchEvent.Kind<Path>) event.kind())
										.handle(Paths.get(path, ((Path) event.context()).toFile().getName()));
							}
						});
						if (!key.reset()) {
							watcher.refresh();
						}
					} catch (InterruptedException e) {
						logger.error("", e);
					}
				}
			}).start();
		});
	}

	@Override
	public void watch(String path, FileAlterationListener handler) {
		watch(path, Lists.newArrayList(StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY), handler);
	}

	public void watch(String path, List<WatchEvent.Kind<Path>> events, FileAlterationListener handler) {
		runnable.run();
		events.forEach(event -> {
			watcher.get().register(path, event);
			handlers.computeIfAbsent(path, k -> Maps.newConcurrentMap())
					.computeIfAbsent(event, WatchServiceHandlers::of).addHandler(handler);
		});
	}

}
