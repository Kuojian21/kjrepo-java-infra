package com.kjrepo.infra.storage.db.model;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CaseFormat;

public class KdbProperty {

	public static KdbProperty of(Field field, PropertyDescriptor descriptor) {
		return new KdbProperty(field, descriptor);
	}

	private final String name;
	private final Class<?> type;
	private final KdbColumn kdbColumn;
	private final PropertyDescriptor descriptor;

	public KdbProperty(Field field, PropertyDescriptor descriptor) {
		this.name = field.getName();
		this.type = field.getType();
		this.kdbColumn = field.getAnnotation(KdbColumn.class);
		this.descriptor = descriptor;
	}

	public Object readAndCast(Object obj) {
		return cast(read(obj));
	}

	public Object read(Object obj) {
		try {
			return this.descriptor.getReadMethod().invoke(obj, new Object[] {});
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public Object cast(Object obj) {
		if (obj instanceof Enum<?>/* this.type.isEnum() */) {
			return ((Enum<?>) obj).name();
		}
		return obj;
	}

	public String name() {
		return this.name;
	}

	public Class<?> type() {
		return this.type;
	}

	public boolean identity() {
		if (kdbColumn != null) {
			return kdbColumn.identity();
		}
		return false;
	}

	public boolean primary() {
		if (kdbColumn != null) {
			return kdbColumn.primary();
		}
		return false;
	}

	public boolean unique() {
		if (kdbColumn != null) {
			return kdbColumn.unique();
		}
		return false;
	}

	public boolean nullable() {
		if (kdbColumn != null) {
			return kdbColumn.nullable();
		}
		return true;
	}

	public String column() {
		if (kdbColumn != null && StringUtils.isNotEmpty(kdbColumn.name())) {
			return kdbColumn.name();
		}
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name());
	}

	public String definition() {
		if (kdbColumn != null) {
			return kdbColumn.definition();
		}
		return "";
	}

	public boolean defInsert() {
		if (kdbColumn != null) {
			return kdbColumn.defInsert();
		}
		return false;
	}

	public boolean defUpdate() {
		if (kdbColumn != null) {
			return kdbColumn.defUpdate();
		}
		return false;
	}

	public String comment() {
		if (kdbColumn != null) {
			return kdbColumn.comment();
		}
		return "";
	}

	public KdbColumn kdbColumn() {
		return this.kdbColumn;
	}

	public PropertyDescriptor descriptor() {
		return this.descriptor;
	}

}
