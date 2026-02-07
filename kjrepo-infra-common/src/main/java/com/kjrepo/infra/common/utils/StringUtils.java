package com.kjrepo.infra.common.utils;

import java.util.Set;

import org.apache.commons.text.StringSubstitutor;

import com.google.common.collect.Sets;

public class StringUtils {

	public static Set<String> extract(String str, String prefix, String suffix) {
		Set<String> set = Sets.newHashSet();
		new StringSubstitutor(key -> {
			set.add(key);
			return key;
		}, prefix, suffix, StringSubstitutor.DEFAULT_ESCAPE).replace(str);
		return set;
	}

}
