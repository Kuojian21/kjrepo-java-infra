package com.kjrepo.infra.runner.binlog;

import com.github.shyiko.mysql.binlog.event.deserialization.json.JsonBinary;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.support.DefaultConversionService;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.base.CaseFormat;
import com.kjrepo.infra.common.logger.LoggerUtils;

public final class BinlogMapper<T> {

	private final Logger logger = LoggerUtils.logger(getClass());

	private Class<T> clazz;
	private Map<String, PropertyDescriptor> descriptors;

	public BinlogMapper(Class<T> clazz) {
		this.clazz = clazz;
		this.descriptors = Stream.of(BeanUtils.getPropertyDescriptors(clazz)).collect(Collectors
				.toMap(d -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, d.getName()).toLowerCase(), d -> d));
	}

	public T map(Map<String, Serializable> row) {
		T obj = BeanUtils.instantiateClass(this.clazz);

		if (row.size() <= 0) {
			return obj;
		}

		BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
		wrapper.setConversionService(DefaultConversionService.getSharedInstance());
		row.forEach((column, value) -> {
			PropertyDescriptor descriptor = descriptors.get(column.toLowerCase());
			if (descriptor == null) {
				logger.warn("Unknown column : {}, value : {}", column, value);
				return;
			}
			try {
				wrapper.setPropertyValue(descriptor.getName(), cast(value, descriptor.getPropertyType()));
			} catch (Exception e) {
				logger.error("Set PropertyValue fail!!! column : {}, value : {}", column, value, e);
			}
		});
		return obj;
	}

	private Object cast(Object value, Class<?> requiredType) throws Exception {
		if (value == null) {
			return null;
		}
		if (requiredType.isEnum()) {
			return castToEnum(value, requiredType);
		} else if (requiredType == String.class) {
			return castToString(value);
		} else if (requiredType == Date.class) {
			return castToDate(value);
		} else if (requiredType == Long.class || requiredType == long.class) {
			return castToLong(value);
		} else if (requiredType == Integer.class || requiredType == int.class) {
			return castToInteger(value);
		} else if (requiredType == Double.class || requiredType == double.class) {
			return castToDouble(value);
		} else if (requiredType == Float.class || requiredType == float.class) {
			return castToFloat(value);
		} else if (requiredType == Boolean.class || requiredType == boolean.class) {
			return castToBoolean(value);
		} else {
			throw new ClassCastException("Unknown requiredType : " + requiredType.getClass());
		}
	}

	private Object castToEnum(Object value, Class<?> requiredType) throws Exception {
		Enum<?>[] enums = (Enum<?>[]) requiredType.getEnumConstants();
		if (value instanceof Integer) {
			for (Enum<?> e : enums) {
				if (e.ordinal() == (int) value) {
					return e;
				}
			}
		} else {
			for (Enum<?> e : enums) {
				if (e.name().equals(value)) {
					return e;
				}
			}
		}
		throw new ClassCastException(
				"Can not cast value to enum : " + requiredType.getName() + " value : " + value.toString());
	}

	private boolean castToBoolean(Object value) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		} else if (value instanceof String) {
			return Boolean.valueOf((String) value);
		} else if (value instanceof Number) {
			final Number n = (Number) value;
			return n.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
		} else {
			throw new ClassCastException("Can not cast value to boolean! value class : " + value.getClass());
		}
	}

	private double castToDouble(Object value) {
		if (value instanceof Double) {
			return (double) value;
		} else if (value instanceof Number) {
			Number number = (Number) value;
			return number.doubleValue();
		} else {
			throw new ClassCastException("Can not cast value to double! value class : " + value.getClass());
		}
	}

	private Float castToFloat(Object value) {
		if (value instanceof Float) {
			return (float) value;
		} else if (value instanceof Number) {
			Number number = (Number) value;
			return number.floatValue();
		} else {
			throw new ClassCastException("Can not cast value to float! value class : " + value.getClass());
		}
	}

	private int castToInteger(Object value) {
		if (value instanceof Integer) {
			return (int) value;
		} else if (value instanceof Number) {
			Number number = (Number) value;
			return number.intValue();
		} else {
			throw new ClassCastException("Can not cast value to int! value class : " + value.getClass());
		}
	}

	private long castToLong(Object value) {
		if (value instanceof Long) {
			return (long) value;
		} else if (value instanceof Number) {
			Number number = (Number) value;
			return number.longValue();
		} else {
			throw new ClassCastException("Can not cast value to long! value class : " + value.getClass());
		}
	}

	private Date castToDate(Object value) {
		if (value instanceof java.sql.Timestamp) {
			return (Date) value;
		} else if (value instanceof Date) {
			return new Date(((Date) value).getTime());
		} else {
			throw new ClassCastException("Can not cast value to Date! value class : " + value.getClass());
		}
	}

	private String castToString(Object value) throws Exception {
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof byte[]) {
			return JsonBinary.parseAsString((byte[]) value);
		} else {
			return value.toString();
		}
	}

}
