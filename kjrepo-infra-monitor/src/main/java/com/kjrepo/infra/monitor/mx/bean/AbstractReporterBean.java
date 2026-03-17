package com.kjrepo.infra.monitor.mx.bean;

import java.lang.management.PlatformManagedObject;
import java.util.List;

import com.kjrepo.infra.reporter.bean.IReporterBean;

public class AbstractReporterBean<D extends PlatformManagedObject> implements IReporterBean {

	private final List<D> beans;

	public AbstractReporterBean(List<D> beans) {
		super();
		this.beans = beans;
	}

	public List<D> beans() {
		return beans;
	}

}
