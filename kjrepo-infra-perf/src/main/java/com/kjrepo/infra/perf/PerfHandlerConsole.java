package com.kjrepo.infra.perf;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class PerfHandlerConsole implements PerfHandler {

	private static final Logger logger = LoggerUtils.logger(PerfHandler.class);

	private String header = format(Lists.newArrayList("name", "micro", "count", "maxMicro", "minMicro", "avg",
			"variance", "top-95", "top-99"));

	@Override
	public void handle(Map<PerfLogTag, PerfLogMetrics> perfs) {
		List<String> msgs = Lists.newArrayList();
		msgs.add(header);
		for (Map.Entry<PerfLogTag, PerfLogMetrics> entry : perfs
				.entrySet().stream().sorted(Comparator.comparing(e -> Joiner.on(".").join(e.getKey().getNamespace(),
						e.getKey().getTag(), Joiner.on(".").join(e.getKey().getExtras()))))
				.collect(Collectors.toList())) {
			PerfLogTag tag = entry.getKey();
			PerfLogMetrics metrics = entry.getValue();
			PerfLogMetricsStat stat = metrics.toMetricsStat();
			List<Object> params = Lists.newArrayList();
			List<Object> names = Lists.newArrayList(tag.getNamespace(), tag.getTag());
			if (CollectionUtils.isNotEmpty(tag.getExtras())) {
				names.addAll(tag.getExtras());
			}
			params.add(Joiner.on(".").join(names));
			params.add(metrics.getMicro());
			params.add(metrics.getCount());
			params.add(metrics.getMaxValue());
			params.add(metrics.getMinValue());
			params.add(stat.getAvg());
			params.add(stat.getVariance());
			List<Double> percentiles = Lists.newArrayList(95D, 99D);
			Map<Double, Long> perMap = stat.getPercentiles(percentiles);
			params.addAll(percentiles.stream().map(perMap::get).collect(Collectors.toList()));
			System.out.println(format(params));
			msgs.add(format(params));
		}
		logger.info("\n" + Joiner.on("\n").join(msgs));
	}

	private String format(List<Object> params) {
		return String.format("%-30s %10s %10s %10s %10s %10s %10s %10s %10s", params.get(0).toString(),
				params.get(1).toString(), params.get(2).toString(), params.get(3).toString(), params.get(4).toString(),
				params.get(5).toString(), params.get(6).toString(), params.get(7).toString(), params.get(8).toString());
	}

}
