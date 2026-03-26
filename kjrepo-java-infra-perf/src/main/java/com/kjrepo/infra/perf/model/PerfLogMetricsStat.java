package com.kjrepo.infra.perf.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Maps;

public class PerfLogMetricsStat {

	private final PerfLogMetrics perf;
	private long avg;
	private double variance;
	private TreeMap<Long, Long> timeTreeMap;

	public PerfLogMetricsStat(PerfLogMetrics perf) {
		this.perf = perf;
		this.avg = perf.getTotalMicro() / perf.getTotalCount();

		this.timeTreeMap = new TreeMap<>(perf.getTimeMap().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().longValue())));
		long truncatedMin = this.timeTreeMap.entrySet().stream().mapToLong(e -> e.getKey() * e.getValue()).min()
				.getAsLong();
		double truncatedRadix = truncatedRadix(truncatedMin);
		double truncatedSum = this.timeTreeMap.entrySet().stream().mapToDouble(e -> e.getKey() * e.getValue()).sum()
				/ truncatedRadix;
		double truncatedAvg = truncatedSum / perf.getTotalCount();
		this.timeTreeMap.forEach((number, count) -> {
			double diff = number / truncatedRadix - truncatedAvg;
			this.variance += diff * diff * count;
		});
		this.variance /= perf.getTotalCount();
	}

	public PerfLogMetrics getPerf() {
		return perf;
	}

	public long getAvg() {
		return avg;
	}

	public double getVariance() {
		return variance;
	}

	public Map<Double, Long> getPercentiles(List<Double> percentiles) {
		if (CollectionUtils.isEmpty(percentiles)) {
			return Collections.emptyMap();
		}

		Map<Double, Long> result = Maps.newHashMapWithExpectedSize(percentiles.size());
		percentiles = new ArrayList<>(percentiles);
		percentiles.sort(Comparator.reverseOrder());
		int i = 0;
		double j = 0;
		for (Map.Entry<Long, Long> entry : this.timeTreeMap.descendingMap().entrySet()) {
			j += entry.getValue().doubleValue();
			double per = (1.0 - j / this.perf.getTotalCount()) * 100;
			while (i < percentiles.size() && per < percentiles.get(i)) {
				result.put(percentiles.get(i), entry.getKey());
				i++;
			}
			if (i >= percentiles.size()) {
				break;
			}
		}
		return result;
	}

	private double truncatedRadix(long truncatedMin) {
		long radix = 1;
		while (truncatedMin > radix * 10) {
			radix *= 10;
		}
		return Double.valueOf(radix);
	}

}
