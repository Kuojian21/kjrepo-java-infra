package com.kjrepo.infra.common.file;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.term.HookHelper;

public class FileMonitor {

	public static FileMonitor monitor() {
		return monitor(10_000);
	}

	public static FileMonitor monitor(long interval) {
		FileMonitor monitor = new FileMonitor(interval);
		monitor.start();
		return monitor;
	}

	private final FileAlterationMonitor monitor;

	public FileMonitor(long interval) {
		monitor = new FileAlterationMonitor(interval);
		monitor.setThreadFactory(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setName("File-Monitor");
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	private final ConcurrentMap<String, LazySupplier<FileAlterationObserver>> observers = Maps.newConcurrentMap();

	public void monitor(String path, FileAlterationListener listener) {
		observers.computeIfAbsent(path, k -> LazySupplier.wrap(() -> {
			try {
				FileAlterationObserver observer = FileAlterationObserver.builder().setPath(path).get();
				monitor.addObserver(observer);
				return observer;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		})).get().addListener(listener);
	}

	public void stop() {
		try {
			monitor.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void start() {
		try {
			monitor.start();
			HookHelper.addHook("file-monitor", () -> stop(), false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}