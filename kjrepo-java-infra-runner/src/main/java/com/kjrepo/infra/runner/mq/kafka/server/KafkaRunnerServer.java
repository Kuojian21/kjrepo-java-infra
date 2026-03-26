package com.kjrepo.infra.runner.mq.kafka.server;

import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.runner.common.RunnerConstants;
import com.kjrepo.infra.runner.mq.kafka.KafkaRunner;
import com.kjrepo.infra.runner.mq.kafka.holder.KafkaRunnerHolder;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

@SuppressWarnings("rawtypes")
public class KafkaRunnerServer extends AbstractRunnerServer<KafkaRunner> {

	@Override
	protected void doRun(KafkaRunner runner) {
		KafkaRunnerHolder holder = KafkaRunnerHolder.of(runner);
		String path = RunnerConstants.register_kafka + runner.topic().topic() + "/consumer";
		Register<Properties> register = holder.context().getRegister(Properties.class);

		LazySupplier<KafkaRunnerHolder> holder_supplier = LazySupplier.wrap(() -> {
			Properties properties = register.get(path);
			properties.setProperty(CommonClientConfigs.GROUP_ID_CONFIG, runner.group().group());
			KafkaConsumer<?, ?> consumer = new KafkaConsumer<>(properties);
			consumer.subscribe(Lists.newArrayList(runner.topic().topic()));
			holder.consumer(consumer);
			return holder;
		});
		register.addListener(path, event -> {
			holder_supplier.refresh(KafkaRunnerHolder::close);
			holder_supplier.get();
		});
		TermHelper.addTerm(runner.module(), () -> holder_supplier.refresh(KafkaRunnerHolder::close));
		holder_supplier.get();
	}

	@Override
	protected boolean nlock() {
		return false;
	}

}
