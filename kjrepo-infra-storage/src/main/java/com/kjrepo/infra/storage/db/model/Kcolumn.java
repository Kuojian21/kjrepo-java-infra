package com.kjrepo.infra.storage.db.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Kcolumn {

	String name() default "";

	boolean primary() default false;

	boolean unique() default false;

	boolean identity() default false;

	String comment() default "";

	String definition() default "";

}