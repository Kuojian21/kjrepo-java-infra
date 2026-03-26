package com.kjrepo.infra.runner.mq.rocket.server;

import java.util.Collections;
import java.util.Properties;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;

import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.common.utils.RunUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.runner.common.RunnerConstants;
import com.kjrepo.infra.runner.mq.rocket.RocketRunner;
import com.kjrepo.infra.runner.mq.rocket.holder.RocketRunnerHolder;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;
import com.kjrepo.infra.text.json.ConfigUtils;

public class RocketRunnerServer extends AbstractRunnerServer<RocketRunner> {

	@Override
	protected void doRun(RocketRunner runner) {
		RocketRunnerHolder holder = RocketRunnerHolder.of(runner);
		String path = RunnerConstants.register_rocket + runner.topic().topic() + "/consumer";
		Register<Properties> register = holder.context().getRegister(Properties.class);

		LazySupplier<RocketRunnerHolder> holder_supplier = LazySupplier.wrap(() -> RunUtils.throwing(() -> {
			PushConsumer consumer = ClientServiceProvider.loadService().newPushConsumerBuilder()
					.setClientConfiguration(
							ConfigUtils.config(ClientConfiguration.newBuilder(), register.get(path)).build())
					.setConsumerGroup(runner.group().group())
					.setSubscriptionExpressions(Collections.singletonMap(runner.topic().topic(),
							new FilterExpression(runner.tag(), FilterExpressionType.TAG)))
					.setMessageListener(message -> {
						runner.handle(message);
						return ConsumeResult.SUCCESS;
					}).build();
			holder.consumer(consumer);
			return holder;
		}));

		register.addListener(path, event -> {
			holder_supplier.refresh(RocketRunnerHolder::close);
			holder_supplier.get();
		});
		TermHelper.addTerm(runner.module(), () -> holder.close());
		holder_supplier.get();
	}

	@Override
	protected boolean nlock() {
		return false;
	}

}
