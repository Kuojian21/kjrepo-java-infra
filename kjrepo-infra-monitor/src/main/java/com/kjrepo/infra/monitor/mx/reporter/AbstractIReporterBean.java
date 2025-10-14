package com.kjrepo.infra.monitor.mx.reporter;

import com.kjrepo.infra.reporter.bean.IReporterBean;

public class AbstractIReporterBean<D> implements IReporterBean {

	private final D data;

	public AbstractIReporterBean(D data) {
		super();
		this.data = data;
	}

	public D data() {
		return data;
	}

}
