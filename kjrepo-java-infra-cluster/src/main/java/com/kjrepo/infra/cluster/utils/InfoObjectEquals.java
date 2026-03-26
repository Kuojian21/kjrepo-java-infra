package com.kjrepo.infra.cluster.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kjrepo.infra.text.json.JsonUtils;

public class InfoObjectEquals {

	public static boolean equals(Object obj1, Object obj2) {
		return equal(JsonUtils.fromJson(JsonUtils.toJson(obj1), Object.class),
				JsonUtils.fromJson(JsonUtils.toJson(obj2), Object.class));
	}

	@SuppressWarnings("unchecked")
	public static boolean equal(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null) {
			return true;
		} else if (obj1 == null || obj2 == null) {
			return false;
		} else if (obj1 instanceof List && obj2 instanceof List) {
			return equal((List<?>) obj1, (List<?>) obj2);
		} else if (obj1 instanceof Map && obj2 instanceof Map) {
			return equal((Map<String, ?>) obj1, (Map<String, ?>) obj2);
		} else {
			return obj1.equals(obj2);
		}
	}

	public static boolean equal(List<?> obj1, List<?> obj2) {
		if (obj1.size() != obj2.size()) {
			return false;
		}
		for (int i = 0; i < obj1.size(); i++) {
			if (equal(obj1.get(i), obj2.get(i))) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	public static boolean equal(Map<String, ?> obj1, Map<String, ?> obj2) {
		if (obj1.size() != obj2.size()) {
			return false;
		}
		for (Entry<String, ?> entry : obj1.entrySet()) {
			if (equal(entry.getValue(), obj2.get(entry.getKey()))) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

}
