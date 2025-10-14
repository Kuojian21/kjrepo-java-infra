package com.kjrepo.infra.monitor.mx.reporter;

public class OperatingSystemIReporter extends AbstractIReporter<OperatingSystemIReporterBean> {

	@Override
	public void report(OperatingSystemIReporterBean data) {
		logger.info("OS:{}", data.toString());
	}

}
