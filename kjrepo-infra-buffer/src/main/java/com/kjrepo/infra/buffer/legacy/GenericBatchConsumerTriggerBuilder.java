//package com.kjrepo.infra.buffer.legacy;
//
//import java.util.List;
//import java.util.function.Consumer;
//
//import com.github.phantomthief.collection.BufferTrigger;
//import com.github.phantomthief.collection.impl.BatchConsumeBlockingQueueTrigger;
//import com.github.phantomthief.util.ThrowableConsumer;
//import com.kjrepo.infra.common.term.TermHelper;
//import com.kjrepo.infra.common.trace.TraceIDUtils;
//
//public class GenericBatchConsumerTriggerBuilder<E>
//		extends com.github.phantomthief.collection.impl.GenericBatchConsumerTriggerBuilder<E> {
//
//	@SuppressWarnings("deprecation")
//	public GenericBatchConsumerTriggerBuilder() {
//		super(BatchConsumeBlockingQueueTrigger.newBuilder());
//	}
//
//	@Override
//	public GenericBatchConsumerTriggerBuilder<E> setConsumer(Consumer<? super List<E>> consumer) {
//		this.setConsumerEx(es -> consumer.accept(es));
//		return this;
//	}
//
//	@Override
//	public GenericBatchConsumerTriggerBuilder<E> setConsumerEx(ThrowableConsumer<? super List<E>, Exception> consumer) {
//		super.setConsumerEx(c -> {
//			String traceid = TraceIDUtils.get();
//			try {
//				TraceIDUtils.generate(traceid);
//				consumer.accept(c);
//			} finally {
//				TraceIDUtils.set(traceid);
//			}
//		});
//		return this;
//	}
//
//	@Override
//	public BufferTrigger<E> build() {
//		BufferTrigger<E> trigger = super.build();
//		TermHelper.addTerm("buffer-trigger", () -> {
//			trigger.manuallyDoTrigger();
//		});
//		return trigger;
//	}
//
//}
