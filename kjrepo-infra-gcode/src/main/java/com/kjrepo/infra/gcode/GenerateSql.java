package com.kjrepo.infra.gcode;

import java.util.List;

import org.hibernate.dialect.Dialect;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.utils.KdbUtils;

public class GenerateSql {

	public static String toCreateTableSql(Class<?> clazz, Dialect dialect) {
		return toCreateTableSql(KdbModel.of(clazz), dialect, true);
	}

	public static String toCreateTableSql(KdbModel model, Dialect dialect) {
		return toCreateTableSql(model, dialect, true);
	}

	public static String toCreateTableSql(KdbModel model, Dialect dialect, boolean ifNotExists) {
		return KdbUtils.toCreateTableSql(model, dialect, ifNotExists);
	}

	public static List<String> toCreateIndexSql(KdbModel model, Dialect dialect) {
		return KdbUtils.toCreateIndexSql(model, dialect);
	}

}
