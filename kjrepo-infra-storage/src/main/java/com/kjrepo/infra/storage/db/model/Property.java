package com.kjrepo.infra.storage.db.model;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.SqlTypes;

import com.google.common.base.CaseFormat;
import com.kjrepo.infra.storage.db.dialect.DialectUtils;

public class Property {

	public static Property of(Field field, PropertyDescriptor descriptor) {
		return new Property(field, descriptor);
	}

	public static Property of(String name, Class<?> type, Kcolumn kcolumn) {
		return new Property(name, type, kcolumn);
	}

	private final String name;
	private final Class<?> type;
	private final Kcolumn kcolumn;
	private final PropertyDescriptor descriptor;

	public Property(Field field, PropertyDescriptor descriptor) {
		this.name = field.getName();
		this.type = field.getType();
		this.kcolumn = field.getAnnotation(Kcolumn.class);
		this.descriptor = descriptor;
	}

	public Property(String name, Class<?> type, Kcolumn kcolumn) {
		this.name = name;
		this.type = type;
		this.kcolumn = kcolumn;
		this.descriptor = null;
	}

	public Object readAndCast(Object obj) {
		return cast(read(obj));
	}

	public Object read(Object obj) {
		try {
			return this.descriptor.getReadMethod().invoke(obj, new Object[] {});
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public Object cast(Object obj) {
		if (this.type.isEnum()) {
			return ((Enum<?>) obj).name();
		}
		return obj;
	}

	public String name() {
		return this.name;
	}

	public boolean auto() {
		if (kcolumn != null) {
			return kcolumn.identity();
		}
		return false;
	}

	public String definition(Dialect dialect) {
		if (kcolumn != null && StringUtils.isNotEmpty(kcolumn.definition())) {
			return kcolumn.definition();
		}
		if (type() == byte.class || type() == Byte.class || type() == short.class || type() == Short.class
				|| type() == int.class || type() == Integer.class) {
			return DialectUtils.columnType(dialect, SqlTypes.INTEGER);
		}
		if (type() == long.class || type() == Long.class) {
			return DialectUtils.columnType(dialect, SqlTypes.BIGINT);
		}
		if (type() == float.class || type() == Float.class || type() == double.class || type() == Double.class
				|| type() == BigDecimal.class) {
			return DialectUtils.columnType(dialect, SqlTypes.DECIMAL).replace("$p", "12").replace("$s", "2");
		}
		if (type() == boolean.class || type == Boolean.class) {
			return DialectUtils.columnType(dialect, SqlTypes.BOOLEAN);
		}
		if (type() == String.class) {
			return DialectUtils.columnType(dialect, SqlTypes.VARCHAR).replace("$l", "60");
		}
		if (type().isEnum()) {
			return DialectUtils.columnType(dialect, SqlTypes.VARCHAR).replace("$l", "30");
		}
		if (type() == Timestamp.class) {
			return DialectUtils.columnType(dialect, SqlTypes.TIMESTAMP);
		}
		if (java.util.Date.class.isAssignableFrom(type())) {
			return DialectUtils.columnType(dialect, SqlTypes.DATE);
		}
		throw new RuntimeException("unknown data type:" + type.getName());
	}

	public Class<?> type() {
		return this.type;
	}

	public String column() {
		if (kcolumn != null && StringUtils.isNotEmpty(kcolumn.name())) {
			return kcolumn.name();
		}
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name());
	}

	public String toCreateSql(Dialect dialect) {
		StringBuilder sql = new StringBuilder();
		sql.append(this.column()).append(" ").append(this.definition(dialect));
		if (this.kcolumn != null && this.kcolumn.primary()) {
			sql.append(" primary key");
		}
		if (this.kcolumn != null && this.kcolumn.unique()) {
			sql.append(" unique");
		}
		if (this.kcolumn != null && this.kcolumn.identity()) {
			sql.append(" " + dialect.getIdentityColumnSupport().getIdentityColumnString(Types.BIGINT));
		}
		if (this.kcolumn != null && StringUtils.isNotEmpty(this.kcolumn.comment())) {
			sql.append(" " + dialect.getColumnComment(this.column()));
		}
		return sql.toString();
	}

}
