package com.kjrepo.infra.storage.db.sql;

import java.util.concurrent.atomic.AtomicLong;

class SqlUtils {

	private static final ThreadLocal<AtomicLong> number = ThreadLocal.withInitial(() -> new AtomicLong(0));

	public static String var() {
		return "v" + number.get().incrementAndGet() + "v";
	}

}
