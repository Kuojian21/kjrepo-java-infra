package com.kjrepo.infra.monitor.mxbean.handler;

import java.lang.management.PlatformManagedObject;
import java.util.Map;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kjrepo.infra.monitor.mxbean.IMxbeanHandler;
import com.kjrepo.infra.monitor.mxbean.holder.AbstractMxbeanHolder;
import com.kjrepo.infra.text.json.JsonUtils;

public abstract class AbstractMxbeanHandler<D extends PlatformManagedObject, T extends AbstractMxbeanHolder<D>>
		implements IMxbeanHandler<T> {

	@Override
	public final void handle(T data) {
		logger.info("{}", JsonUtils.toPrettyJson(Stream.ofNullable(data.beans())
				.collect(Collectors.toMap(bean -> bean.getObjectName().toString(), bean -> doHandle(bean)))));
	}

	public abstract Map<String, Object> doHandle(D bean);

}
