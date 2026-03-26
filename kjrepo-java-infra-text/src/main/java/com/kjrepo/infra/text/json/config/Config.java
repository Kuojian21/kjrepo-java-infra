package com.kjrepo.infra.text.json.config;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.bean.BeanBuilder;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.JsonUtils;

public interface Config {

	Logger logger = LoggerUtils.logger(Config.class);

	<T> T config(T obj, List<Map<String, Object>> jsons);

	@SuppressWarnings("unchecked")
	default <T> T config(T obj, Object jsons) {
		if (obj == null || jsons == null) {
			return obj;
		}
		if (jsons instanceof List) {
			return config(obj, (List<Map<String, Object>>) jsons);
		} else if (jsons instanceof Map) {
			return config(obj, (Map<String, Object>) jsons);
		} else {
			logger.error("error config values:{}", JsonUtils.toPrettyJson(jsons));
		}
		return obj;
	}

	default <T> T config(T obj, Map<String, Object> jsons) {
		if (obj == null || jsons == null) {
			return obj;
		}
		return config(obj,
				Stream.of(jsons)
						.map(en -> BeanBuilder
								.builder(Maps.newHashMap()).accept(bean -> bean.put(en.getKey(), en.getValue()))
								.build()/* ImmutableMap.of(en.getKey(), en.getValue()) */)
						.toList());
	}
}
