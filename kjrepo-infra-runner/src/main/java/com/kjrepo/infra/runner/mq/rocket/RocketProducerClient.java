package com.kjrepo.infra.runner.mq.rocket;

import java.util.Properties;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;

import com.annimon.stream.function.Consumer;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.text.json.ConfigUtils;

public class RocketProducerClient {

	private final ClientServiceProvider provider = ClientServiceProvider.loadService();
	private final String topic;
	private final Producer producer;

	public RocketProducerClient(String topic) {
		this.topic = topic;
		try {
			this.producer = provider.newProducerBuilder().setTopics(topic)
					.setClientConfiguration(
							ConfigUtils.config(ClientConfiguration.newBuilder(), this.properties(topic)).build())
					.build();
		} catch (ClientException e) {
			throw new RuntimeException(e);
		}
	}

	public SendReceipt send(Consumer<MessageBuilder> consumer) {
		MessageBuilder builder = provider.newMessageBuilder().setTopic(topic);
		consumer.accept(builder);
		try {
			return producer.send(builder.build());
		} catch (ClientException e) {
			throw new RuntimeException(e);
		}
	}

	public Properties properties(String topic) {
		return RegisterFactory.getContext(this.getClass()).getRegister(Properties.class).get(topic);
	}
}
