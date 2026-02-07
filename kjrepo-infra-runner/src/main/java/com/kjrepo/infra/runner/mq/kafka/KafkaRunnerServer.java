package com.kjrepo.infra.runner.mq.kafka;

import java.time.Duration;
//import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.google.common.collect.Lists;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;
import com.kjrepo.infra.text.json.JsonUtils;

@SuppressWarnings("rawtypes")
public class KafkaRunnerServer extends AbstractRunnerServer<KafkaRunner> {

	@SuppressWarnings("unchecked")
	@Override
	protected void doRun(KafkaRunner runner) {
//		runners.forEach(runner -> {
		Properties properties = properties(runner.topic());
		properties.setProperty(CommonClientConfigs.GROUP_ID_CONFIG, runner.consumerGroup());
		KafkaConsumer<?, ?> consumer = new KafkaConsumer<>(properties);
		try {
			consumer.subscribe(Lists.newArrayList(runner.topic()));
			TermHelper.addTerm(runner.module(), () -> consumer.close(Duration.ofMinutes(1)));
			while (!TermHelper.isStopping()) {
				ConsumerRecords<?, ?> records = consumer.poll(Duration.ofMinutes(1));
				if (records != null && records.count() > 0) {
					for (ConsumerRecord record : records) {
						runner.handle(record.key(), record.value());
					}
				}
			}
			consumer.commitAsync((offsets, exception) -> {
				logger.info("offsets:{}", JsonUtils.toJson(offsets), exception);
			});
		} finally {
			try {
				consumer.commitSync();
			} finally {
				consumer.close();
			}
		}
//		});
//		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T cast(Object obj) {
		return (T) obj;
	}

	/**
	 * org.apache.kafka.clients.consumer.ConsumerConfig
	 */
	public Properties properties(String topic) {
		return RegisterFactory.getContext(ConsumerConfig.class).getRegister(Properties.class).get(topic);
	}

	@Override
	protected boolean nlock() {
		return false;
	}

}
