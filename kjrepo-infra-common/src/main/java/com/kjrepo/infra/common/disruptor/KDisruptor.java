package com.kjrepo.infra.common.disruptor;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.logger.LoggerUtils;
//import com.kjrepo.infra.text.json.JsonUtils;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class KDisruptor<T> {

	public static <T> KDisruptor<T> disruptor(int ringBufferSize) {
		return disruptor(ringBufferSize, new BlockingWaitStrategy());
	}

	public static <T> KDisruptor<T> disruptor(int ringBufferSize, WaitStrategy waitStrategy) {
		ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		};
		return disruptor(ringBufferSize, threadFactory, ProducerType.MULTI, waitStrategy);
	}

	public static <T> KDisruptor<T> disruptor(int ringBufferSize, ThreadFactory threadFactory,
			ProducerType producerType, WaitStrategy waitStrategy) {
		return new KDisruptor<T>(ringBufferSize, threadFactory, producerType, waitStrategy);
	}

	private final Logger logger = LoggerUtils.logger();
	private final Disruptor<KDisruptorEvent<T>> disruptor;
	private final List<KDisruptorHandler<T>> handlers;

	public KDisruptor(int ringBufferSize, ThreadFactory threadFactory, ProducerType producerType,
			WaitStrategy waitStrategy) {
		this.disruptor = new Disruptor<KDisruptorEvent<T>>(new KDisruptorEventFactory<T>(), ringBufferSize,
				threadFactory, producerType, waitStrategy);
		this.handlers = Lists.newArrayList();
	}

	public KDisruptor<T> addDisrutorHandler(KDisruptorHandler<T> handler) {
		this.handlers.add(handler);
		return this;
	}

	@SuppressWarnings("unchecked")
	public KDisruptor<T> start() {
		if (handlers.isEmpty()) {
			this.handlers.add(new KDisruptorHandler<T>() {
				@Override
				public void onEvent(T event) {
					logger.info("{}", event);
				}
			});
		}
		this.disruptor.handleEventsWith((KDisruptorEventHandler<T>[]) this.handlers.stream()
				.map(KDisruptorEventHandler::new).toArray(len -> new KDisruptorEventHandler[len]));
		this.disruptor.start();
		return this;
	}

	@SuppressWarnings("unchecked")
	public void add(T... data) {
		this.add(Optional.ofNullable(data).map(ds -> Stream.of(ds).collect(Collectors.toList()))
				.orElseGet(Lists::newArrayList));
	}

	public void add(List<T> data) {
		RingBuffer<KDisruptorEvent<T>> ringBuffer = this.disruptor.getRingBuffer();
		long sequence = ringBuffer.next(data.size());
		for (int i = 0; i < data.size(); i++) {
			long seq = sequence - data.size() + i + 1;
			ringBuffer.get(seq).setData(data.get(i));
			ringBuffer.publish(seq);
		}
	}
}
