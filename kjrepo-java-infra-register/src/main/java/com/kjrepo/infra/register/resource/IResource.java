package com.kjrepo.infra.register.resource;

import org.slf4j.Logger;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.common.logger.LoggerUtils;

public interface IResource<I, R> {

	Logger logger = LoggerUtils.logger(IResource.class);

	String ID();

	Function<I, R> mapper();

	default R get() {
		return IResourceHolder.get(this);
	}

	default void refresh() {
		IResourceHolder.refresh(this);
	}

}
