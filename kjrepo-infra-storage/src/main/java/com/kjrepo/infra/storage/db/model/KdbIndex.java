package com.kjrepo.infra.storage.db.model;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({})
@Retention(RUNTIME)
public @interface KdbIndex {

	String name() default "";

	String[] columns();

	KdbIndexType type() default KdbIndexType.INDEX;

}
