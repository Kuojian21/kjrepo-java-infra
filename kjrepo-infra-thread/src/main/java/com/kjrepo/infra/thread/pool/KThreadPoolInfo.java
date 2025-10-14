package com.kjrepo.infra.thread.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

public class KThreadPoolInfo {
	private int corePoolSize = -1;
	private int maximumPoolSize = -1;
	private long keepAliveTime = -1;
	private TimeUnit unit;
	private BlockingQueue<Runnable> workQueue;
	private KThreadFactory threadFactory;
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

	public KThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public void setThreadFactory(KThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}

	public RejectedExecutionHandler getRejectedHandler() {
		return rejectedHandler;
	}

	public void setRejectedHandler(RejectedExecutionHandler rejectedHandler) {
		this.rejectedHandler = rejectedHandler;
	}

}
