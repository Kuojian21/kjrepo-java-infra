package com.kjrepo.infra.runner.binlog.listener;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.annimon.stream.Collectors;
import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.GtidEventData;
import com.github.shyiko.mysql.binlog.event.RotateEventData;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.runner.binlog.BinlogMapper;
import com.kjrepo.infra.runner.binlog.BinlogResolver;
import com.kjrepo.infra.runner.binlog.holder.BinlogRunnerHolder;
import com.kjrepo.infra.runner.binlog.info.BinlogStatusInfo;

public class BinlogEventListener implements EventListener {

	private final Logger logger = LoggerUtils.logger(getClass());

	private final ConcurrentMap<Long, TableMapEventData> tableMap = Maps.newConcurrentMap();

	private final BinlogRunnerHolder holder;
	private final Map<String, BinlogResolver<?>> tableResolverMap;

	public BinlogEventListener(BinlogRunnerHolder holder) {
		this.holder = holder;
		this.tableResolverMap = Stream.of(holder.runner().tableResolverMap().entrySet())
				.collect(Collectors.toMap(e -> e.getKey().toLowerCase(), Map.Entry::getValue));
	}

	@Override
	public void onEvent(Event event) {
		EventHeaderV4 head = event.getHeader();
		EventType eventType = head.getEventType();
		switch (eventType) {
		case GTID: {
			GtidEventData data = event.getData();
			holder.status(data.getMySqlGtid().toString());
			break;
		}
		case ROTATE: {
			RotateEventData data = event.getData();
			holder.status(data.getBinlogFilename(), data.getBinlogPosition());
			break;
		}
		case TABLE_MAP: {
			TableMapEventData data = event.getData();
			tableMap.put(data.getTableId(), data);
			break;
		}
		default:
			if (EventType.isWrite(eventType)) {
				WriteRowsEventData data = event.getData();
				BinlogResolver<?> resolver = resolver(data.getTableId());
				if (resolver == null) {
					return;
				}
				resolver.insert(data(resolver.mapper(), data.getRows(), data.getTableId()));
			} else if (EventType.isUpdate(eventType)) {
				UpdateRowsEventData data = event.getData();
				BinlogResolver<?> resolver = resolver(data.getTableId());
				if (resolver == null) {
					return;
				}
				resolver.update(
						data(resolver.mapper(), Stream.of(data.getRows()).map(e -> e.getKey()).toList(),
								data.getTableId()),
						data(resolver.mapper(), Stream.of(data.getRows()).map(e -> e.getValue()).toList(),
								data.getTableId()));
			} else if (EventType.isDelete(eventType)) {
				DeleteRowsEventData data = event.getData();
				BinlogResolver<?> resolver = resolver(data.getTableId());
				if (resolver == null) {
					return;
				}
				resolver.delete(data(resolver.mapper(), data.getRows(), data.getTableId()));
			}
			holder.status(head.getNextPosition());
		}
	}

	private List<?> data(BinlogMapper<?> mapper, List<Serializable[]> rows, long tableId) {
		List<String> columns = tableMap.get(tableId).getEventMetadata().getColumnNames();
		return Stream.of(rows)
				.map(row -> IntStream.range(0, row.length).mapToObj(i -> i)
						.collect(Collectors.toMap(i -> columns.get(i), i -> row[i])))
				.map(row -> mapper.map(row)).toList();
	}

	private BinlogResolver<?> resolver(long tableId) {
		TableMapEventData tableMapData = tableMap.get(tableId);
		if (tableMapData == null) {
			BinlogStatusInfo statusInfo = holder.status();
			logger.error("no TableMapEventData binlogFilename:{} binlogPosition:{}", statusInfo.getBinlogFilename(),
					statusInfo.getBinlogPosition());
			return null;
		}
		BinlogResolver<?> resolver = this.tableResolverMap
				.get(tableMapData.getDatabase().toLowerCase() + "." + tableMapData.getTable().toLowerCase());
		if (resolver == null) {
			resolver = this.tableResolverMap.get(tableMapData.getTable().toLowerCase());
		}
		return resolver;
	}

}
