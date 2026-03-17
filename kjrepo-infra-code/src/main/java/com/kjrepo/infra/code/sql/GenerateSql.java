package com.kjrepo.infra.code.sql;

import java.util.List;


import com.kjrepo.infra.storage.db.model.KdbDialect;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.utils.DB_ddl_utils;

public class GenerateSql {

	public static String toCreateTableSql(Class<?> clazz, KdbDialect dialect) {
		return toCreateTableSql(KdbModel.of(clazz), dialect, true);
	}

	public static String toCreateTableSql(KdbModel model, KdbDialect dialect) {
		return toCreateTableSql(model, dialect, true);
	}

	public static String toCreateTableSql(KdbModel model, KdbDialect dialect, boolean ifNotExists) {
		return DB_ddl_utils.toCreateTableSql(model, dialect, ifNotExists);
	}

	public static List<String> toCreateIndexSql(KdbModel model, KdbDialect dialect) {
		return DB_ddl_utils.toCreateIndexSql(model, dialect);
	}

}
