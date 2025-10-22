package com.kjrepo.infra.thread.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.kjrepo.infra.thread.factory.KrThreadFactory;

public class KrExecutorServiceInfo {

	private int corePoolSize;
	private int maximumPoolSize;
	private long keepAliveTime;
	private TimeUnit unit;
	private BlockingQueue<Runnable> workQueue;
	private KrThreadFactory threadFactory;
	private RejectedExecutionHandler rejectedHandler;

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public long getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public TimeUnit getUnit() {
		return unit;
	}

	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}

	public BlockingQueue<Runnable> getWorkQueue() {
		return workQueue;
	}

	public void setWorkQueue(BlockingQueue<Runnable> workQueue) {
		this.workQueue = workQueue;
	}

	public KrThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public void setThreadFactory(KrThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}

	public RejectedExecutionHandler getRejectedHandler() {
		return rejectedHandler;
	}

	public void setRejectedHandler(RejectedExecutionHandler rejectedHandler) {
		this.rejectedHandler = rejectedHandler;
	}

	public KrExecutorServiceInfo ensure() {
		if (this.getCorePoolSize() < 0 || this.getMaximumPoolSize() <= 0) {
			this.setCorePoolSize(Math.min(6, Runtime.getRuntime().availableProcessors()));
			this.setMaximumPoolSize(Math.min(6, Runtime.getRuntime().availableProcessors()));
		}
		if (this.getCorePoolSize() > this.getMaximumPoolSize()) {
			this.setMaximumPoolSize(this.getCorePoolSize());
		}
		if (this.getKeepAliveTime() < 0 || this.getUnit() == null) {
			this.setKeepAliveTime(0);
			this.setUnit(TimeUnit.MILLISECONDS);
		}
		if (this.getWorkQueue() == null) {
			this.setWorkQueue(new LinkedBlockingQueue<>());
		}
		if (this.getThreadFactory() == null) {
			this.setThreadFactory(new KrThreadFactory());
		}
		if (this.getRejectedHandler() == null) {
			this.setRejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		}
		return this;
	}

}
