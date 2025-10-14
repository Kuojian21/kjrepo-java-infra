package com.kjrepo.infra.runner.mq.kafka;

import com.kjrepo.infra.runner.Runner;

public abstract class KafkaRunner<K, V> implements Runner {

	public abstract void handle(K key, V value);

	public abstract String topic();

	public abstract String consumerGroup();

}
