package com.kjrepo.infra.storage.db.utils;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.jdbc.UncategorizedSQLException;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

public class DB_jdbc_utils {

	public static boolean checkIntegrityConstraint(UncategorizedSQLException e) {
		Throwable cause = e.getCause();
		if (cause == null) {

		} else if (cause instanceof SQLIntegrityConstraintViolationException) {
			return true;
		} else if (Optional.ofNullable(cause.getMessage()).map(String::toLowerCase)
				.map(s -> Stream.of("UNIQUE constraint failed").map(String::toLowerCase).anyMatch(c -> s.contains(c)))
				.orElse(false)) {
			return true;
		}
		return false;
	}

}
