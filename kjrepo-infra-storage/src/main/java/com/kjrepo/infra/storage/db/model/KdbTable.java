package com.kjrepo.infra.storage.db.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface KdbTable {

	String name() default "";

	KdbIndex[] indexes() default {};

}