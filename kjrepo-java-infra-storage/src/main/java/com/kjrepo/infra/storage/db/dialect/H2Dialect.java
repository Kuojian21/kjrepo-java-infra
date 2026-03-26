package com.kjrepo.infra.storage.db.dialect;

import org.hibernate.dialect.identity.H2IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;

public class H2Dialect extends org.hibernate.dialect.H2Dialect {

	@Override
	public IdentityColumnSupport getIdentityColumnSupport() {
		return new H2IdentityColumnSupport() {
			@Override
			public String getIdentityColumnString(int type) {
				return "AUTO_INCREMENT";
			}
		};
	}

	@Override
	protected String columnType(int sqlTypeCode) {
		return super.columnType(sqlTypeCode);
	}

}
