package com.kjrepo.infra.text.json.config;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;

public class ConfigFacade implements Config {

	private final List<AbstractCacheConfig<?>> configs;

	public ConfigFacade(Map<Class<?>, Map<Type, Type>> mapper) {
		this.configs = Lists.newArrayList(new PropertyDescriptorConfig(mapper), new FieldConfig(mapper),
				new MethodConfig(mapper), new ConstructorConfig(mapper));
	}

	@Override
	public <T> T config(T obj, List<Map<String, Object>> jsons) {
		if (obj == null || jsons == null) {
			return obj;
		}
		Class<?> clazz = obj.getClass();
		jsons.forEach(valueMap -> valueMap.forEach((key, value) -> {
			try {
				String nkey = key.indexOf("-") >= 0 ? key.replaceAll("-", "_") : key;
				for (AbstractCacheConfig<?> config : configs) {
					if (config.setValue(clazz, nkey, obj, value)) {
						return;
					}
				}
				logger.error("no match key : {} value : {}", nkey, value);
			} catch (Exception e) {
				logger.error(key, e);
			}
		}));
		return obj;
	}

}
