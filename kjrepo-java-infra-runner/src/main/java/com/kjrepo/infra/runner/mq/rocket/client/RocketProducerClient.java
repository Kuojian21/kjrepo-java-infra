package com.kjrepo.infra.runner.mq.rocket.client;

import java.util.Map;
import java.util.Properties;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.shaded.com.google.common.collect.Maps;

import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.utils.RunUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.runner.common.RunnerConstants;
import com.kjrepo.infra.runner.mq.ITopic;
import com.kjrepo.infra.text.json.ConfigUtils;

public class RocketProducerClient {

	private static final ClientServiceProvider provider = ClientServiceProvider.loadService();
	private static final Map<ITopic, LazySupplier<LazySupplier<Producer>>> producers = Maps.newConcurrentMap();

	public static SendReceipt send(ITopic topic, MessageBuilder builder) {
		return RunUtils.throwing(() -> producers.computeIfAbsent(topic, k -> LazySupplier.wrap(() -> {
			Register<Properties> register = RegisterFactory.getContext(topic.getClass()).getRegister(Properties.class);
			String path = RunnerConstants.register_rocket + topic.topic() + "/producer";
			LazySupplier<Producer> producer = LazySupplier
					.wrap(() -> RunUtils.throwing(() -> provider.newProducerBuilder().setTopics(topic.topic())
							.setClientConfiguration(
									ConfigUtils.config(ClientConfiguration.newBuilder(), register.get(path)).build())
							.build()));
			register.addListener(path, event -> {
				producer.refresh(p -> p.close());
			});
			return producer;
		})).get().get().send(builder.setTopic(topic.topic()).build()));
	}

}
