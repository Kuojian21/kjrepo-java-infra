package com.kjrepo.infra.common.term;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.phantomthief.util.ThrowableRunnable;

public class Hook {

	public static Hook of(ThrowableRunnable<? extends Throwable> hook, StackTraceElement element) {
		return new Hook(hook, element);
	}

	private static final AtomicInteger number = new AtomicInteger(0);

	private final int no = number.incrementAndGet();
	private final ThrowableRunnable<? extends Throwable> hook;
	private final StackTraceElement element;

	private Hook(ThrowableRunnable<? extends Throwable> hook, StackTraceElement element) {
		super();
		this.hook = hook;
		this.element = element;
	}

	public int no() {
		return this.no;
	}

	public ThrowableRunnable<? extends Throwable> hook() {
		return this.hook;
	}

	public StackTraceElement element() {
		return this.element;
	}

}
