package com.kjrepo.infra.perf;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.buffer.BufferTrigger;

public class PerfLogger {

	private final BufferTrigger<PerfContext, Map<PerfLogTag, PerfLogMetrics>> bufferTrigger = BufferTrigger
			.<PerfContext, Map<PerfLogTag, PerfLogMetrics>>builder().setConsumer(this::handle)
			.setContainer(Maps::newConcurrentMap, (container, builder) -> {
				container.merge(builder.getPerfLog(), new PerfLogMetrics(builder.getCount(), builder.getMicro()),
						(value1, value2) -> {
							value1.accept(value2.getCount(), value2.getMicro());
							return value1;
						});
			}).build();

	private final List<PerfHandler> handlers;

	public PerfLogger(List<PerfHandler> handlers) {
		super();
		this.handlers = Collections.unmodifiableList(Lists.newArrayList(handlers));
	}

	public void logstash(PerfContext builder) {
		bufferTrigger.enqueue(builder);
	}

	protected void handle(Map<PerfLogTag, PerfLogMetrics> perfs) {
		handlers.forEach(handler -> {
			handler.handle(perfs);
		});
	}
}
