package com.kjrepo.infra.code;

import java.util.List;


import com.kjrepo.infra.storage.db.model.KdbDialect;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.utils.KdbUtils;

public class GenerateSql {

	public static String toCreateTableSql(Class<?> clazz, KdbDialect dialect) {
		return toCreateTableSql(KdbModel.of(clazz), dialect, true);
	}

	public static String toCreateTableSql(KdbModel model, KdbDialect dialect) {
		return toCreateTableSql(model, dialect, true);
	}

	public static String toCreateTableSql(KdbModel model, KdbDialect dialect, boolean ifNotExists) {
		return KdbUtils.toCreateTableSql(model, dialect, ifNotExists);
	}

	public static List<String> toCreateIndexSql(KdbModel model, KdbDialect dialect) {
		return KdbUtils.toCreateIndexSql(model, dialect);
	}

}
