package com.kjrepo.infra.runner.mq.kafka;

import com.kjrepo.infra.runner.mq.MQRunner;

public interface KafkaRunner<K, V> extends MQRunner {

	void handle(K key, V value);

}
