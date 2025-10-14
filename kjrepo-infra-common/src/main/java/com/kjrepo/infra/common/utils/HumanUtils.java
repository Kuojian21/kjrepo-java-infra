package com.kjrepo.infra.common.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import com.annimon.stream.IntStream;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.info.Pair;

public class HumanUtils {

	public static String formatMills(long mills) {
		return formatMills(mills, 2);
	}

	public static String formatMills(long mills, int scale) {
		return format(mills, scale, Base.DE.MILLS.base);
	}

	public static String formatNanos(long nanos) {
		return formatNanos(nanos, 2);
	}

	public static String formatNanos(long nanos, int scale) {
		return format(nanos, scale, Base.DE.NANO.base);
	}

	public static String formatNumber(long l) {
		return formatNumber(l, 2);
	}

	public static String formatNumber(long l, int scale) {
		return format(l, scale, Base.DE.THOU.base);
	}

	public static String formatByte(long l) {
		return formatByte(l, 2);
	}

	public static String formatByte(long l, int scale) {
		return format(l, scale, Base.DE.BYTE.base);
	}

	public static String format(long l, int scale, Base base) {
		for (int i = base.units.size() - 1; i >= 0; i--) {
			if (l >= base.units.get(i).getValue() * 3) {
				return BigDecimal.valueOf(l)
						.divide(BigDecimal.valueOf(base.units.get(i).getValue()), scale, RoundingMode.HALF_UP)
						.toPlainString() + base.units.get(i).getKey();
			}
		}
		return l + "";
	}

	public static class Base {
		private final List<Pair<String, Long>> units;

		public Base(int[] base, List<String> units) {
			if (base.length != units.size()) {
				throw new RuntimeException("error params");
			}
			this.units = Lists.newArrayList(Pair.pair(units.get(0), (long) base[0]));
			for (int i = 1; i < base.length; i++) {
				this.units.add(Pair.pair(units.get(i), this.units.get(this.units.size() - 1).getValue() * base[i]));
			}
		}

		public Base(int base, List<String> units) {
			super();
			this.units = IntStream.range(0, units.size())
					.mapToObj(i -> Pair.pair(units.get(i), BigInteger.valueOf(base).pow(i + 1).longValue())).toList();
		}

		enum DE {
			THOU(new Base(1000, Lists.newArrayList("T", "M", "B"))),
			BYTE(new Base(1024, Lists.newArrayList("K", "M", "G"))),
			MILLS(new Base(new int[] { 1, 1000, 60, 60, 24 }, Lists.newArrayList("ms", "s", "m", "h", "d"))),
			NANO(new Base(new int[] { 1, 1000, 1000, 1000, 60, 60, 24 },
					Lists.newArrayList("nanos", "micros", "ms", "s", "m", "h", "d")));

			private final Base base;

			private DE(Base base) {
				this.base = base;
			}
		}

	}

}
