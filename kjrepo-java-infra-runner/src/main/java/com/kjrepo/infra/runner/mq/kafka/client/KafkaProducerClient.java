package com.kjrepo.infra.runner.mq.kafka.client;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.rocketmq.shaded.com.google.common.collect.Maps;

import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.runner.common.RunnerConstants;
import com.kjrepo.infra.runner.mq.ITopic;

@SuppressWarnings("unchecked")
public class KafkaProducerClient {

	private static final Map<ITopic, LazySupplier<LazySupplier<KafkaProducer<?, ?>>>> producers = Maps
			.newConcurrentMap();

	public static <K, V> Future<RecordMetadata> send(ITopic topic, K key, V value) {
		return ((KafkaProducer<K, V>) producers.computeIfAbsent(topic, k -> LazySupplier.wrap(() -> {
			Register<Properties> register = RegisterFactory.getContext(topic.getClass()).getRegister(Properties.class);
			String path = RunnerConstants.register_kafka + topic.topic() + "/producer";
			LazySupplier<KafkaProducer<?, ?>> producer = LazySupplier
					.wrap(() -> new KafkaProducer<K, V>(register.get(path)));
			register.addListener(path, event -> {
				producer.refresh(p -> p.close(Duration.ofMinutes(1)));
			});
			return producer;
		})).get().get()).send(new ProducerRecord<K, V>(topic.topic(), key, value));
	}

}
