package com.kjrepo.infra.buffer.trigger.builder;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

public class ContainerThresholdBatchBufferTriggerBuilder<E>
		extends ContainerThresholdBufferTriggerBuilder<E, BlockingQueue<E>> {

	public ContainerThresholdBatchBufferTriggerBuilder() {
		super.setContainer(LinkedBlockingQueue::new, BlockingQueue::add).disableEnqueueLock();
	}

	public ContainerThresholdBatchBufferTriggerBuilder<E> setBatchConsumer(int batchsize, Consumer<List<E>> consumer) {
		super.setConsumerTriggerThreshold(batchsize).setConsumer(queue -> {
			do {
				List<E> list = Lists.newArrayList();
				queue.drainTo(list, batchsize);
				consumer.accept(list);
				if (list.size() != batchsize) {
					return;
				}
			} while (true);
		});
		return this;
	}

}
