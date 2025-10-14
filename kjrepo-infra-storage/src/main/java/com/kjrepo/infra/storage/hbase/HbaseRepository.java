package com.kjrepo.infra.storage.hbase;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.common.collect.Lists;
import com.kjrepo.infra.common.executor.LazyInfoExecutor;

public class HbaseRepository extends LazyInfoExecutor<Connection, Configuration> {

	public HbaseRepository(Configuration config) {
		super(config, () -> {
			try {
				return ConnectionFactory.createConnection(config);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void create(String table, List<String> familes) throws IOException {
		super.execute(resource -> {
			try (Admin admin = resource.getAdmin()) {
				TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(TableName.valueOf(table));
				familes.forEach(family -> {
					builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(family));
				});
				admin.createTable(builder.build());
			}
		});

	}

	public void disable(String table) throws IOException {
		super.execute(resource -> {
			try (Admin admin = resource.getAdmin()) {
				admin.disableTable(TableName.valueOf(table));
			}
		});
	}

	public void truncate(String table) throws IOException {
		super.execute(resource -> {
			try (Admin admin = resource.getAdmin()) {
				admin.truncateTable(TableName.valueOf(table), true);
			}
		});
	}

	public void delete(String table) throws IOException {
		super.execute(resource -> {
			try (Admin admin = resource.getAdmin()) {
				admin.deleteTable(TableName.valueOf(table));
			}
		});

	}

	public void put(String tablename, List<Put> puts) throws IOException {
		super.execute(resource -> {
			try (Table table = resource.getTable(TableName.valueOf(tablename))) {
				table.put(puts);
			}
		});
	}

	public void put(String tablename, String rowId, List<Cell> cells) throws IOException {
		Put put = new Put(Bytes.toBytes(rowId));
		for (Cell cell : cells) {
			put.add(cell);
		}
		put(tablename, Lists.newArrayList(put));

	}

	public Result[] get(String tablename, List<Get> gets) throws IOException {
		return super.execute(resource -> {
			try (Table table = resource.getTable(TableName.valueOf(tablename))) {
				return table.get(gets);
			}
		});
	}

	public Result get(String tablename, String rowID) throws IOException {
		Get get = new Get(Bytes.toBytes(rowID));
		Result result = get(tablename, Lists.newArrayList(get))[0];
		return result;
	}

	public void delete(String tablename, List<Delete> deletes) throws IOException {
		super.execute(resource -> {
			try (Table table = resource.getTable(TableName.valueOf(tablename))) {
				table.delete(deletes);
			}
		});
	}

	public void delete(String tablename, String rowID) throws IOException {
		Delete delete = new Delete(Bytes.toBytes(rowID));
		delete(tablename, Lists.newArrayList(delete));
	}

	public ResultScanner scan(String tablename, Scan scan) throws IOException {
		return super.execute(resource -> {
			try (Table table = resource.getTable(TableName.valueOf(tablename))) {
				return table.getScanner(scan);
			}
		});
	}

}
