package com.kjrepo.infra.text.pinyin;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Stream;
import com.google.common.base.Joiner;
import com.hankcs.hanlp.HanLP;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class PinyinUtils {

	public static String abbr(String text) {
		return Joiner.on("").join(Stream.of(StringUtils.split(pinyin(text, " ", false), " "))
				.map(s -> s.substring(0, 1)).map(String::toLowerCase).toList());
	}

	public static String pinyin(String text) {
		return pinyin(text, "", false);
	}

	public static String pinyin(String text, String separator, boolean remainNone) {
		return HanLP.convertToPinyinString(text, separator, remainNone);
	}

	public static void main(String[] args) {
		LoggerUtils.logger(PinyinUtils.class).info("{}", abbr("小米-w"));
	}

}
