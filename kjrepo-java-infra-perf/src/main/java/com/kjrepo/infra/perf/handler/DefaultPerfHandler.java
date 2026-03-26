package com.kjrepo.infra.perf.handler;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.perf.model.PerfLogHolder;
import com.kjrepo.infra.perf.model.PerfLogMetrics;
import com.kjrepo.infra.perf.model.PerfLogTag;
import com.kjrepo.infra.perf.utils.PerfUtils;

public class DefaultPerfHandler implements IPerfHandler {

	private final Logger logger = LoggerUtils.logger(getClass());
	private final String[] header = new String[] { "name", "total", "count", "max", "min", "avg", "variance", "top-95",
			"top-99" };

	@Override
	public void handle(List<PerfLogHolder> perfs) {
		List<Line> lines = Stream.of(perfs).map(Line::of).toList();
		String format = format(lines);
		List<String> msgs = Lists.newArrayList();
		msgs.add(String.format(format, (Object[]) header));
		msgs.add("ORDER-BY-TOTAL");
		Stream.of(lines).filter(filter())
				.sorted(Comparator.<Line>comparingLong(l -> l.metrics().getTotalMicro()).reversed())
				.forEach(line -> msgs.add(String.format(format, line.line().get(0), line.line().get(1),
						line.line().get(2), line.line().get(3), line.line().get(4), line.line().get(5),
						line.line().get(6), line.line().get(7), line.line().get(8))));
		msgs.add("ORDER-BY-AVG");
		Stream.of(lines).filter(filter())
				.sorted(Comparator.<Line>comparingLong(l -> l.metrics().getMetricsStat().getAvg()).reversed())
				.forEach(line -> msgs.add(String.format(format, line.line().get(0), line.line().get(1),
						line.line().get(2), line.line().get(3), line.line().get(4), line.line().get(5),
						line.line().get(6), line.line().get(7), line.line().get(8))));
		logger.info("\n" + Joiner.on("\n").join(msgs));
	}

	private String format(List<Line> lines) {
		List<Integer> lens = IntStream.range(0, lines.get(0).line().size())
				.map(i -> Math.max(header[i].length(), IntStream.range(0, lines.size())
						.map(j -> lines.get(j).line().get(i).length()).max().getAsInt()))
				.mapToObj(Integer::valueOf).toList();
		if (lens.get(0) <= 80) {
			return "%-" + lens.get(0) + "s "
					+ Joiner.on(" ").join(Stream.of(lens.subList(1, lens.size())).map(l -> "%" + l + "s").toList());
		} else {
			return "%s\n" + "%-" + lens.get(1) + "s "
					+ Joiner.on(" ").join(Stream.of(lens.subList(2, lens.size())).map(l -> "%" + l + "s").toList());
		}

	}

	protected Predicate<Line> filter() {
		return line -> !line.tag().getNamespace().startsWith(PerfUtils.N_executor) ? true
				: line.tag().getTag().endsWith("done");
	}

	@Override
	public String pkg() {
		return "";
	}

	@Override
	public String[] pkgs() {
		return !this.getClass().equals(DefaultPerfHandler.class) ? new String[] { pkg() }
				: new String[] { pkg(), PerfUtils.N_executor.substring(0, PerfUtils.N_executor.length() - 1) };
	}

	public static class Line {

		public static Line of(PerfLogHolder holder) {
			return new Line(holder.getTag(), holder.getMetrics());
		}

		private final PerfLogTag tag;
		private final PerfLogMetrics metrics;
		private final LazySupplier<List<String>> line;

		public Line(PerfLogTag tag, PerfLogMetrics metrics) {
			this.tag = tag;
			this.metrics = metrics;
			this.line = LazySupplier.wrap(() -> {
				List<String> line = Lists.newArrayList();
				List<String> names = Lists.newArrayList(tag.getNamespace());
				if (StringUtils.isNotEmpty(tag.getTag())) {
					names.add(tag.getTag());
				}
				if (CollectionUtils.isNotEmpty(tag.getExtras())) {
					names.addAll(tag.getExtras());
				}
				line.add(Joiner.on("_").join(names));
				line.add(N_humanUtils.formatMicros(metrics.getTotalMicro()));
				line.add(N_humanUtils.formatNumber(metrics.getTotalCount()));
				line.add(N_humanUtils.formatMicros(metrics.getMaxMicro()));
				line.add(N_humanUtils.formatMicros(metrics.getMinMicro()));
				line.add(N_humanUtils.formatMicros(metrics.getMetricsStat().getAvg()));
				line.add(N_humanUtils.formatNumber(metrics.getMetricsStat().getVariance()));
				List<Double> percentiles = Lists.newArrayList(95D, 99D);
				Map<Double, Long> perMap = metrics.getMetricsStat().getPercentiles(percentiles);
				line.addAll(percentiles.stream().map(perMap::get).map(N_humanUtils::formatMicros)
						.collect(Collectors.toList()));
				return line;
			});
		}

		public List<String> line() {
			return this.line.get();
		}

		public PerfLogTag tag() {
			return this.tag;
		}

		public PerfLogMetrics metrics() {
			return this.metrics;
		}

	}

}
