package com.kjrepo.infra.perf.utils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.annimon.stream.IntStream;
import com.google.common.util.concurrent.Uninterruptibles;
import com.kjrepo.infra.perf.PerfContext;
import com.kjrepo.infra.perf.PerfFactory;
import com.kjrepo.infra.perf.model.PerfLogTag;

public class PerfUtils {

	public static final String N_monitor = "Monitor";
	public static final String N_executor = "Executor.";
	public static final String N_storage_kjdbc = "Storage.kjdbc";

	public static final String N_client_grpc = "Client.grpc";
	public static final String N_client_grpc2 = "Client.grpc2";
	public static final String N_client_kafka = "Client.kafka";
	public static final String N_client_rocket = "Client.rocket";

	public static final String N_runner_grpc = "Runner.grpc";
	public static final String N_runner_grpc2 = "Runner.grpc2";
	public static final String N_runner_quatz = "Runner.quatz";
	public static final String N_runner_ksch = "Runner.ksch";
	public static final String N_runner_simple = "Runner.simple";
	public static final String N_runner_kafka = "Runner.kafka";
	public static final String N_runner_rocket = "Runner.rocket";
	public static final String N_runner_binlog = "Runner.binlog";

	public static PerfContext perf(String namespace, String tag, String... extras) {
		return PerfFactory.DEFAULT
				.perfContext(PerfLogTag.builder().setNamespace(namespace).setTag(tag).addExtras(extras).build());
	}

	public static void main(String[] args) {
		IntStream.range(0, 10).forEach(i -> {
			new Thread(() -> {
				while (true) {
					Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MILLISECONDS);
					PerfUtils.perf("demo", "tag", "thread-" + i, "arg2").count(1)
							.micro(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE)).logstash();
				}
			}).start();
		});
		Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MINUTES);
		System.exit(0);
	}
}