package com.kjrepo.infra.perf.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;

import com.kjrepo.infra.common.lazy.LazySupplier;

public class PerfLogMetrics implements BiConsumer<Long, Long> {
	private static final int DEFAULT_PRECISION = 2;
	private final Map<Long, LongAdder> timeMap = new ConcurrentHashMap<>();
	private final int precision;
	private final LongAdder totalCount = new LongAdder();
	private final LongAdder totalMicro = new LongAdder();
	private final AtomicLong minMicro = new AtomicLong(Long.MAX_VALUE);
	private final AtomicLong maxMicro = new AtomicLong(Long.MIN_VALUE);
	private final LazySupplier<PerfLogMetricsStat> metricsStat = LazySupplier.wrap(() -> new PerfLogMetricsStat(this));

	public PerfLogMetrics(Long count, Long micro) {
		this(DEFAULT_PRECISION, count, micro);
	}

	public PerfLogMetrics(int precision, Long count, Long micro) {
		this.precision = precision;
		this.accept(count, micro);
	}

	@Override
	public void accept(Long count, Long micro) {
		if (count <= 0L) {
			return;
		}
		long time = micro / count;
		this.timeMap.computeIfAbsent(timeKey(time), it -> new LongAdder()).add(count);
		this.totalCount.add(count);
		this.totalMicro.add(micro);
		if (time < minMicro.get()) {
			minMicro.updateAndGet(old -> Math.min(old, time));
		}
		if (time > maxMicro.get()) {
			maxMicro.updateAndGet(old -> Math.max(old, time));
		}
		this.metricsStat.refresh();
	}

	public PerfLogMetricsStat getMetricsStat() {
		return this.metricsStat.get();
	}

	private long timeKey(long time) {
		long m = 1L;
		for (int i = 0; i < precision; i++) {
			time /= 10;
			m *= 10;
		}
		return time * m;
	}

	public Map<Long, LongAdder> getTimeMap() {
		return timeMap;
	}

	public long getTotalCount() {
		return totalCount.longValue();
	}

	public long getTotalMicro() {
		return totalMicro.longValue();
	}

	public long getMinMicro() {
		return minMicro.longValue();
	}

	public long getMaxMicro() {
		return maxMicro.longValue();
	}
}
