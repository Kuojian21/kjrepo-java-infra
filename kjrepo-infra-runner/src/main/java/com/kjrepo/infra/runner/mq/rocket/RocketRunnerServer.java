package com.kjrepo.infra.runner.mq.rocket;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;

import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;
import com.kjrepo.infra.text.json.ConfigUtils;

public class RocketRunnerServer extends AbstractRunnerServer<RocketRunner> {

	public RocketRunnerServer run(List<RocketRunner> runners) {
		runners.forEach(runner -> {
			try {
				PushConsumer consumer = ClientServiceProvider.loadService().newPushConsumerBuilder()
						.setClientConfiguration(ConfigUtils
								.config(ClientConfiguration.newBuilder(), this.properties(runner.topic())).build())
						.setConsumerGroup(runner.consumerGroup())
						.setSubscriptionExpressions(Collections.singletonMap(runner.topic(),
								new FilterExpression(runner.tag(), FilterExpressionType.TAG)))
						.setMessageListener(message -> {
							runner.handle(message);
							return ConsumeResult.SUCCESS;
						}).build();
				TermHelper.addTerm(runner.module(), () -> consumer.close());
			} catch (ClientException e) {
				logger.error("", e);
			}
		});
		return this;
	}

	public Properties properties(String topic) {
		return RegisterFactory.getContext(this.getClass()).getRegister(Properties.class).get(topic);
	}

}
