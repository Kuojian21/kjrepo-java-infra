package com.kjrepo.infra.perf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;

public class PerfLogMetrics implements BiConsumer<Long, Long> {
	private static final int DEFAULT_PRECISION = 2;
	private final Map<Long, LongAdder> timeMap = new ConcurrentHashMap<>();
	private final int precision;
	private LongAdder totalCount = new LongAdder();
	private LongAdder totalMicro = new LongAdder();
	private AtomicLong minValue = new AtomicLong(Long.MAX_VALUE);
	private AtomicLong maxValue = new AtomicLong(Long.MIN_VALUE);

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
		if (time < minValue.get()) {
			minValue.updateAndGet(old -> Math.min(old, time));
		}
		if (time > maxValue.get()) {
			maxValue.updateAndGet(old -> Math.max(old, time));
		}
	}

	public PerfLogMetricsStat toMetricsStat() {
		return new PerfLogMetricsStat(this);
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

	public long getCount() {
		return totalCount.longValue();
	}

	public long getMicro() {
		return totalMicro.longValue();
	}

	public long getMinValue() {
		return minValue.longValue();
	}

	public long getMaxValue() {
		return maxValue.longValue();
	}
}
