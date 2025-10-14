package com.kjrepo.infra.register.resource;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public interface IResource<I, R> extends Resource<R> {

	@Override
	Function<I, R> mapper();

	@Override
	@SuppressWarnings("unchecked")
	default Class<I> iclazz() {
		return (Class<I>) TypeMapperUtils.mapper(getClass()).get(IResource.class)
				.get(IResource.class.getTypeParameters()[0]);
	}

}
