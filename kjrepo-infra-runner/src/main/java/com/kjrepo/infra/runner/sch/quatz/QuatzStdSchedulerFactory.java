package com.kjrepo.infra.runner.sch.quatz;

import java.util.Properties;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class QuatzStdSchedulerFactory extends StdSchedulerFactory {

	private final Properties props;
	private volatile boolean inited = false;

	public QuatzStdSchedulerFactory(Properties props) {
		this.props = props;
	}

	@Override
	public void initialize() throws SchedulerException {
		if (this.props == null || this.inited) {
			super.initialize();
			return;
		}
		this.inited = true;
		super.initialize(this.props);
	}

}
