package com.kjrepo.infra.runner.sch.quatz.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.spi.MutableTrigger;

import com.annimon.stream.Optional;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.runner.sch.quatz.QuatzRunner;
import com.kjrepo.infra.runner.sch.quatz.detail.QuatzJobDetailBuilder;
import com.kjrepo.infra.runner.sch.quatz.job.QuatzJob;
import com.kjrepo.infra.runner.sch.quatz.job.QuatzJobStat;
import com.kjrepo.infra.runner.sch.quatz.scheduler.QuatzStdSchedulerFactory;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

public class QuatzRunnerServer extends AbstractRunnerServer<QuatzRunner> {

	private static final AtomicInteger number = new AtomicInteger(0);

	private final LazySupplier<Scheduler> scheduler = LazySupplier.wrap(() -> {
		try {
			// return Scheduler sch = StdSchedulerFactory.getDefaultScheduler();
			Scheduler sch = new QuatzStdSchedulerFactory(properties()).getScheduler();
			sch.start();
			TermHelper.addTerm("Scheduler", () -> sch.shutdown(true));
			sch.scheduleJob(
					JobBuilder.newJob(QuatzJobStat.class).withIdentity(QuatzJobStat.NAME, QuatzJobStat.GROUP).build(),
					TriggerBuilder.newTrigger().withIdentity(QuatzJobStat.NAME, QuatzJobStat.GROUP).startNow()
							.withSchedule(
									SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(3).repeatForever())
							.build());
			return sch;
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	});

	@Override
	protected void doRun(QuatzRunner runner) {
		for (int i = 0; i < runner.crons().length; i++) {
			try {
				String jobID = Optional.ofNullable(runner.ID()).filter(id -> StringUtils.isNotEmpty(id))
						.orElseGet(() -> "job" + number.incrementAndGet()) + "#" + i;
				JobDetail jobDetail = QuatzJobDetailBuilder.job(new QuatzJob(runner))
						.withIdentity(jobID, runner.module()).build();
				MutableTrigger trigger = CronScheduleBuilder.cronSchedule(runner.crons()[i]).build();
				trigger.setKey(TriggerKey.triggerKey(jobID, runner.module()));
				scheduler.get().scheduleJob(jobDetail, trigger);
			} catch (SchedulerException e) {
				logger.error("", e);
			}
		}
	}

	public Properties properties() {
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("schedule/quartz.properties")) {
			Properties props = new Properties();
			props.load(in);
			if (!props.containsKey("org.quartz.threadPool.threadCount")) {
				props.put("org.quartz.threadPool.threadCount",
						Math.min(Math.max(Runtime.getRuntime().availableProcessors(), 6), 12) + "");
			}
			props.putAll(System.getProperties());
			return props;
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}
	}

	@Override
	protected boolean nlock() {
		return true;
	}
}
