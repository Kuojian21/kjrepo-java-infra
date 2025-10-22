package com.kjrepo.infra.runner.sch.quatz;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
//import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.MutableTrigger;
//import org.springframework.stereotype.Service;

import com.annimon.stream.Optional;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

public class QuatzRunnerServer extends AbstractRunnerServer<QuatzRunner> {

	private static final AtomicInteger number = new AtomicInteger(0);

	private final LazySupplier<Scheduler> scheduler = LazySupplier.wrap(() -> {
		try {
			// return Scheduler sch = StdSchedulerFactory.getDefaultScheduler();
			Scheduler sch = new QuatzStdSchedulerFactory(properties()).getScheduler();
			sch.start();
			TermHelper.addTerm("Scheduler", () -> sch.shutdown(true));
			QuatzJobStat job = new QuatzJobStat(sch);
			sch.scheduleJob(QuatzJobDetailBuilder.job(job).withIdentity(job.name(), job.group()).build(),
					TriggerBuilder.newTrigger().withIdentity(job.name(), job.group()).startNow()
							.withSchedule(
									SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(3).repeatForever())
							.build());
			return sch;
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	});

	@Override
	public QuatzRunnerServer run(List<QuatzRunner> runners) {
		runners.forEach(runner -> {
			for (int i = 0; i < runner.crons().length; i++) {
				try {
					String jobID = Optional.ofNullable(runner.ID()).filter(id -> StringUtils.isNotEmpty(id))
							.orElseGet(() -> "job" + number.incrementAndGet()) + "#" + i;
					JobDetail jobDetail = QuatzJobDetailBuilder
							.job(runner.isConcurrentRunning() ? new QuatzJobConcurrent(runner)
									: new QuatzJobConcurrentDisallow(runner))
							.withIdentity(jobID, runner.module()).build();
					MutableTrigger trigger = CronScheduleBuilder.cronSchedule(runner.crons()[i]).build();
					trigger.setKey(TriggerKey.triggerKey(jobID, runner.module()));
					scheduler.get().scheduleJob(jobDetail, trigger);
				} catch (SchedulerException e) {
					logger.error("", e);
				}
			}
		});
		return this;
	}

	public Properties properties() {
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("schedule/quartz.properties")) {
			Properties props = new Properties();
			props.load(in);
			if (!props.containsKey("org.quartz.threadPool.threadCount")) {
				props.put("org.quartz.threadPool.threadCount",
						Math.min(Runtime.getRuntime().availableProcessors(), 6) + "");
			}
			props.putAll(System.getProperties());
			return props;
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}
	}
}
