package com.kjrepo.infra.runner.mq.kafka.topic;

import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.RecordMetadata;

import com.kjrepo.infra.runner.mq.ITopic;
import com.kjrepo.infra.runner.mq.kafka.client.KafkaProducerClient;

public interface KafkaTopic extends ITopic {

	default <K, V> Future<RecordMetadata> send(K key, V value) {
		return KafkaProducerClient.send(this, key, value);
	}

}
