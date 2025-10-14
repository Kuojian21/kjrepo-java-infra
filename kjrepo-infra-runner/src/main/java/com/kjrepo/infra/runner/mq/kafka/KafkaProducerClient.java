package com.kjrepo.infra.runner.mq.kafka;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.kjrepo.infra.register.context.RegisterFactory;

public class KafkaProducerClient<K, V> {

	private final String topic;
	private final KafkaProducer<K, V> producer;

	public KafkaProducerClient(String topic) {
		super();
		this.topic = topic;
		this.producer = new KafkaProducer<K, V>(properties(topic));
	}

	public Future<RecordMetadata> send(K key, V value) {
		return this.producer.send(new ProducerRecord<K, V>(topic, key, value));
	}

	/**
	 * org.apache.kafka.clients.producer.ProducerConfig
	 */
	public Properties properties(String topic) {
		return RegisterFactory.getContext(ProducerConfig.class).getRegister(Properties.class).get(topic);
	}
}
