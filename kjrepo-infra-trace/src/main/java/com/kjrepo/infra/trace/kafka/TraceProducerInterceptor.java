package com.kjrepo.infra.trace.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;

import com.kjrepo.infra.trace.utils.TraceIDUtils;

public class TraceProducerInterceptor<K, V> implements ProducerInterceptor<K, V> {

	@Override
	public void configure(Map<String, ?> configs) {
		// TODO Auto-generated method stub

	}

	@Override
	public ProducerRecord<K, V> onSend(ProducerRecord<K, V> record) {
		String traceid = TraceIDUtils.get();
		if (!StringUtils.isEmpty(traceid)) {
			record.headers().add(new RecordHeader(TraceIDUtils.ID_REQUEST, traceid.getBytes(StandardCharsets.UTF_8)));
		}
		return record;
	}

	@Override
	public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
