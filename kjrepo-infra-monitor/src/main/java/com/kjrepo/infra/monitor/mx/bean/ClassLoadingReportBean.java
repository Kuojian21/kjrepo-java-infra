package com.kjrepo.infra.monitor.mx.bean;

import java.lang.management.ClassLoadingMXBean;
import java.util.List;

public class ClassLoadingReportBean extends AbstractReporterBean<ClassLoadingMXBean> {

	public ClassLoadingReportBean(List<ClassLoadingMXBean> data) {
		super(data);
	}

}
