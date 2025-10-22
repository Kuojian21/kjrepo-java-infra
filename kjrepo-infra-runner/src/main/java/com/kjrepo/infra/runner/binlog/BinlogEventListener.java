package com.kjrepo.infra.runner.binlog;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;

public class BinlogEventListener implements EventListener {

	private final Logger logger = LoggerUtils.logger(getClass());

	private final ConcurrentMap<Long, TableMapEventData> tableMapRepo = Maps.newConcurrentMap();
	private final Map<String, BinlogResolver<?>> resolvers;
	private final LazySupplier<AtomicReference<BinlogStatusInfo>> statusInfoRef;
	private final Register<BinlogStatusInfo> register;

	public BinlogEventListener(BinlogRunner runner) {
		super();
		this.register = RegisterFactory.getContext(runner.getClass()).getRegister(BinlogStatusInfo.class);
		this.resolvers = Stream.of(runner.resolvers().entrySet())
				.collect(Collectors.toMap(e -> e.getKey().toLowerCase(), Map.Entry::getValue));
		this.statusInfoRef = LazySupplier.wrap(() -> {
			AtomicReference<BinlogStatusInfo> ref = new AtomicReference<BinlogStatusInfo>(register.get(runner.ID()));
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (!Thread.interrupted()) {
						try {
							register.set(runner.ID(), statusInfoRef.get().get());
							Thread.sleep(TimeUnit.MINUTES.toMillis(1));
						} catch (Exception e) {
							logger.error("", e);
						}
					}
				}
			});
			thread.start();
			return ref;
		});
	}

	@Override
	public void onEvent(Event event) {
		EventHeaderV4 head = event.getHeader();
		EventType eventType = head.getEventType();
		if (eventType == EventType.GTID) {
			GtidEventData data = event.getData();
			statusInfoRef.get().get().setGtidSet(data.getMySqlGtid().toString());
		} else if (eventType == EventType.ROTATE) {
			RotateEventData data = event.getData();
			BinlogStatusInfo statusInfo = statusInfoRef.get().get().clone();
			statusInfo.setBinlogFilename(data.getBinlogFilename());
			statusInfo.setBinlogPosition(data.getBinlogPosition());
			statusInfoRef.get().set(statusInfo);
		} else if (eventType == EventType.TABLE_MAP) {
			TableMapEventData data = event.getData();
			tableMapRepo.put(data.getTableId(), data);
		} else if (EventType.isRowMutation(eventType)) {
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
			statusInfoRef.get().get().setBinlogPosition(head.getNextPosition());
		}
	}

	private List<?> data(BinlogMapper<?> mapper, List<Serializable[]> rows, long tableId) {
		List<String> columns = tableMapRepo.get(tableId).getEventMetadata().getColumnNames();
		return Stream.of(rows)
				.map(row -> IntStream.range(0, row.length).mapToObj(i -> i)
						.collect(Collectors.toMap(i -> columns.get(i), i -> row[i])))
				.map(row -> mapper.map(row)).toList();
	}

	private BinlogResolver<?> resolver(long tableId) {
		TableMapEventData tableMap = tableMapRepo.get(tableId);
		if (tableMap == null) {
			BinlogStatusInfo statusInfo = statusInfoRef.get().get();
			logger.error("no TableMapEventData binlogFilename:{} binlogPosition:{}", statusInfo.getBinlogFilename(),
					statusInfo.getBinlogPosition());
			return null;
		}
		return this.resolvers.get(tableMap.getDatabase().toLowerCase() + "." + tableMap.getTable().toLowerCase());
	}

}
