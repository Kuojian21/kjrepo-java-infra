package com.kjrepo.infra.storage.db.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KdbColumn {

	String name() default "";

	boolean nullable() default true;

	boolean primary() default false;

	boolean unique() default false;

	boolean identity() default false;

	int length() default 60;

	int precision() default 12;

	int scope() default 2;

	String comment() default "";

	String definition() default "";

}