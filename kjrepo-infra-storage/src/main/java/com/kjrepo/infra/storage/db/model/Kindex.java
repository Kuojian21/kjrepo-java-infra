package com.kjrepo.infra.storage.db.model;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({})
@Retention(RUNTIME)
public @interface Kindex {

	String name() default "";

	String[] columns();

	boolean primary() default false;

	boolean unique() default false;

}
