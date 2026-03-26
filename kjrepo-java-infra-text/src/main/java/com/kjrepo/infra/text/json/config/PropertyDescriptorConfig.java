package com.kjrepo.infra.text.json.config;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kjrepo.infra.text.json.ConfigUtils;

public class PropertyDescriptorConfig extends AbstractCacheConfig<PropertyDescriptor> {

	public PropertyDescriptorConfig(Map<Class<?>, Map<Type, Type>> mapper) {
		super(clazz -> Stream.of(Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors())
				.collect(Collectors.toMap(p -> p.getName(), p -> p)), mapper);
	}

	@Override
	public <V> V setValue(V obj, PropertyDescriptor descriptor, Object json) throws Exception {
		if (obj == null || descriptor == null || descriptor.getWriteMethod() == null) {
			return obj;
		}
		Method method = descriptor.getWriteMethod();
		method.invoke(obj, new Object[] { ConfigUtils.valueUnchecked(json, method.getGenericParameterTypes()[0],
				this.mapper().get(method.getDeclaringClass())) });

		return obj;
	}

	protected boolean canSet(Class<?> clazz, String name) {
		return super.canSet(clazz, name) && element(clazz, name).getWriteMethod() != null;
	}

}
