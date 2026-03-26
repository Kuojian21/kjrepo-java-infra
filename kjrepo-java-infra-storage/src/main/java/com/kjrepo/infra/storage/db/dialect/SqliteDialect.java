package com.kjrepo.infra.storage.db.dialect;

import java.sql.Types;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.id.insert.GetGeneratedKeysDelegate;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;

@SuppressWarnings("deprecation")
public class SqliteDialect extends Dialect {

	@Override
	protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		super.registerColumnTypes(typeContributions, serviceRegistry);
		DdlTypeRegistry ddlTypeRegistry = typeContributions.getTypeConfiguration().getDdlTypeRegistry();
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.BIT, "boolean", this));
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.DECIMAL, "decimal", this));
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.CHAR, "char", this));
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.LONGVARCHAR, "longvarchar", this));
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.TIMESTAMP, "datetime", this));
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.BINARY, "blob", this));
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.VARBINARY, "blob", this));
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(Types.LONGVARBINARY, "blob", this));
	}

	@Override
	protected String columnType(int sqlTypeCode) {
		return super.columnType(sqlTypeCode);
	}

	public IdentityColumnSupport getIdentityColumnSupport() {
		return new IdentityColumnSupport() {
			@Override
			public boolean supportsIdentityColumns() {
				return true;
			}

			@Override
			public boolean supportsInsertSelectIdentity() {
				return true;
			}

			@Override
			public boolean hasDataTypeInIdentityColumn() {
				return false;
			}

			@Override
			public String appendIdentitySelectToInsert(String insertString) {
				return insertString + " RETURNING rowid";
			}

			@Override
			public String getIdentitySelectString(String table, String column, int type) {
				return "SELECT last_insert_rowid()";
			}

			@Override
			public String getIdentityColumnString(int type) {
				return "autoincrement";
			}

			@Override
			public String getIdentityInsertString() {
				return null;
			}

			@Override
			public GetGeneratedKeysDelegate buildGetGeneratedKeysDelegate(EntityPersister persister) {
				return null;
			}
		};
	}

}
