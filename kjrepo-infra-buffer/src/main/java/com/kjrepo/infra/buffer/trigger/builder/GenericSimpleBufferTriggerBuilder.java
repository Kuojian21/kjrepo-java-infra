package com.kjrepo.infra.buffer.trigger.builder;

import com.github.phantomthief.collection.BufferTrigger;
import com.github.phantomthief.collection.impl.SimpleBufferTrigger;
import com.github.phantomthief.util.ThrowableConsumer;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.common.trace.TraceIDUtils;

public class GenericSimpleBufferTriggerBuilder<E, C>
		extends com.github.phantomthief.collection.impl.GenericSimpleBufferTriggerBuilder<E, C> {

	@SuppressWarnings("deprecation")
	public GenericSimpleBufferTriggerBuilder() {
		super(SimpleBufferTrigger.newBuilder());
	}

	@Override
	public GenericSimpleBufferTriggerBuilder<E, C> consumer(ThrowableConsumer<? super C, Throwable> consumer) {
		super.consumer(c -> {
			String traceid = TraceIDUtils.get();
			try {
				TraceIDUtils.generate(traceid);
				consumer.accept(c);
			} finally {
				TraceIDUtils.set(traceid);
			}
		});
		return this;
	}

	@Override
	public BufferTrigger<E> build() {
		BufferTrigger<E> trigger = super.build();
		TermHelper.addTerm("buffer-trigger", () -> {
			trigger.manuallyDoTrigger();
		});
		return trigger;
	}

}
