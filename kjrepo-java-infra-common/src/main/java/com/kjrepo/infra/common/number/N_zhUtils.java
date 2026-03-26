package com.kjrepo.infra.common.number;

import java.math.BigDecimal;

import com.annimon.stream.IntStream;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class N_zhUtils {

	public static String convert(int number) {
		return convert(BigDecimal.valueOf(number));
	}

	public static String convert(long number) {
		return convert(BigDecimal.valueOf(number));
	}

	public static String convert(double number) {
		return convert(BigDecimal.valueOf(number));
	}

	public static String convert(String number) {
		return convert(new BigDecimal(number));
	}

	public static String convert(BigDecimal number) {
		if (number == null) {
			return "";
		}

		boolean negative = number.compareTo(BigDecimal.ZERO) < 0;
		if (negative) {
			number = number.abs();
		}

		String numberStr = number.toPlainString();
		String[] parts = numberStr.split("\\.");

		String integerPart = parts[0];
		String decimalPart = parts.length > 1 ? parts[1] : null;

		StringBuilder result = new StringBuilder();

		if (negative) {
			result.append("负");
		}

		result.append(convertIntegerPart(integerPart));

		if (decimalPart != null && !decimalPart.equals("0")) {
			result.append("点");
			result.append(convertDecimalPart(decimalPart));
		}

		return result.toString();
	}

	private static final String[] CHINESE_NUMBERS = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
	private static final String[] CHINESE_UNITS = { "", "十", "百", "千" };
	private static final String[] CHINESE_SECTIONS = { "", "万", "亿", "兆" };

	private static String convertIntegerPart(String integerStr) {
		if (integerStr.equals("0")) {
			return CHINESE_NUMBERS[0];
		}

		integerStr = integerStr.replaceFirst("^0+", "");
		if (integerStr.isEmpty()) {
			return CHINESE_NUMBERS[0];
		}

		int sectionCount = (integerStr.length() + 3) / 4;
		String[] sections = new String[sectionCount];

		for (int i = sectionCount - 1; i >= 0; i--) {
			int end = integerStr.length() - i * 4;
			int start = Math.max(0, end - 4);
			sections[i] = integerStr.substring(start, end);
		}

		StringBuilder result = new StringBuilder();
		boolean lastSectionHasZero = false;

		for (int i = sections.length - 1; i >= 0; i--) {
			String section = sections[i];
			String sectionChinese = convertSection(section);

			if (!sectionChinese.isEmpty()) {
				if (lastSectionHasZero) {
					result.append(CHINESE_NUMBERS[0]);
				}
				result.append(sectionChinese);
				result.append(CHINESE_SECTIONS[i]);
				lastSectionHasZero = false;
			}

			if (section.contains("0")) {
				lastSectionHasZero = true;
			}
		}

		return result.toString();
	}

	private static String convertSection(String section) {
		StringBuilder sb = new StringBuilder();
		boolean lastIsZero = false;

		for (int i = 0; i < section.length(); i++) {
			int digit = section.charAt(i) - '0';
			int unitIndex = section.length() - i - 1;

			if (digit == 0) {
				if (!lastIsZero && i != section.length() - 1) {
					sb.append(CHINESE_NUMBERS[digit]);
				}
				lastIsZero = true;
			} else {
				if (lastIsZero && sb.length() > 0) {
					sb.append(CHINESE_NUMBERS[0]);
				}
				sb.append(CHINESE_NUMBERS[digit]);
				sb.append(CHINESE_UNITS[unitIndex]);
				lastIsZero = false;
			}
		}

		String result = sb.toString();
		if (result.startsWith("一十")) {
			result = result.substring(1);
		}

		return result;
	}

	private static String convertDecimalPart(String decimalStr) {
		StringBuilder sb = new StringBuilder();
		for (char c : decimalStr.toCharArray()) {
			int digit = c - '0';
			sb.append(CHINESE_NUMBERS[digit]);
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		IntStream.range(1, 100).forEach(i -> LoggerUtils.logger(N_zhUtils.class).info("{}", convert(i)));
	}

}