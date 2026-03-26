package com.kjrepo.infra.common.string;

import java.util.Set;

import org.apache.commons.text.StringSubstitutor;

import com.google.common.collect.Sets;

public class StringSubstitutors {

	public static Set<String> vars(String str, String prefix, String suffix) {
		Set<String> set = Sets.newHashSet();
		new StringSubstitutor(key -> {
			set.add(key);
			return key;
		}, prefix, suffix, StringSubstitutor.DEFAULT_ESCAPE).replace(str);
		return set;
	}

}
