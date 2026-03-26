package com.kjrepo.infra.monitor.mxbean.holder;

import java.lang.management.PlatformManagedObject;
import java.util.List;

import com.kjrepo.infra.monitor.mxbean.IMxbeanHolder;

public class AbstractMxbeanHolder<D extends PlatformManagedObject> implements IMxbeanHolder {

	private final List<D> beans;

	public AbstractMxbeanHolder(List<D> beans) {
		super();
		this.beans = beans;
	}

	public List<D> beans() {
		return beans;
	}

}
