package com.kjrepo.infra.thread.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class KrThreadFactory implements ThreadFactory {

	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private final int curPoolNumber;
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);

	public KrThreadFactory() {
		curPoolNumber = poolNumber.getAndIncrement();
		group = Thread.currentThread().getThreadGroup();
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(group, r,
				namePrefix + "-" + curPoolNumber + "-thread-" + threadNumber.getAndIncrement(), 0);
		thread.setDaemon(daemon);
		thread.setPriority(priority);
		return thread;
	}

	private String namePrefix = "pool";
	private boolean daemon;
	private int priority = Thread.NORM_PRIORITY;

	public String getNamePrefix() {
		return namePrefix;
	}

	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
