package com.kjrepo.infra.common.utils;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Stream;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hankcs.hanlp.HanLP;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class PinyinUtils {

	private static final LoadingCache<String, String> cache = CacheBuilder.newBuilder().build(new CacheLoader<>() {
		@Override
		public String load(String key) throws Exception {
			return StringUtils.join(Stream.of(StringUtils.split(HanLP.convertToPinyinString(key, " ", false), " "))
					.map(s -> s.substring(0, 1)).toList(), "").toLowerCase();
		}
	});

	public static String abbr(String str) {
		return cache.getUnchecked(str);
	}

	public static void main(String[] args) {
		LoggerUtils.logger(PinyinUtils.class).info("{}", abbr("小米-w"));
	}

}
