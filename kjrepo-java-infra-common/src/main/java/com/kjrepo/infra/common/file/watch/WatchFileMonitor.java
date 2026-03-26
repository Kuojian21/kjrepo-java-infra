package com.kjrepo.infra.common.file.watch;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.google.common.collect.Maps;
import com.kjrepo.infra.common.hook.HookHelper;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.utils.RunUtils;

public class WatchFileMonitor implements IWatch {

	public static WatchFileMonitor of(long interval) {
		return new WatchFileMonitor(interval);
	}

	private final ConcurrentMap<String, LazySupplier<FileAlterationObserver>> observers = Maps.newConcurrentMap();
	private final LazySupplier<FileAlterationMonitor> monitor;

	public WatchFileMonitor(long interval) {
		this.monitor = LazySupplier.wrap(() -> RunUtils.throwing(() -> {
			FileAlterationMonitor monitor = new FileAlterationMonitor(interval);
			monitor.setThreadFactory(new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread thread = new Thread(r);
					thread.setName("FileMonitor");
					thread.setDaemon(true);
					return thread;
				}
			});
			monitor.start();
			HookHelper.addHook("FileMonitor", monitor::stop);
			return monitor;
		}));
	}

	@Override
	public void watch(String path, FileAlterationListener listener) {
		observers.computeIfAbsent(path, k -> LazySupplier.wrap(() -> RunUtils.throwing(() -> {
			FileAlterationObserver observer = FileAlterationObserver.builder().setPath(path).get();
			monitor.get().addObserver(observer);
			return observer;
		}))).get().addListener(listener);
	}

}