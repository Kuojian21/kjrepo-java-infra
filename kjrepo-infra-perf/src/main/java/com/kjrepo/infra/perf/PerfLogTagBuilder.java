package com.kjrepo.infra.perf;

import java.util.List;

import com.google.common.collect.Lists;

public class PerfLogTagBuilder {
	private String namespace;
	private String tag;
	private List<Object> extras = Lists.newArrayList();

	public PerfLogTagBuilder setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	public PerfLogTagBuilder setTag(String tag) {
		this.tag = tag;
		return this;
	}

	public PerfLogTagBuilder addExtras(Object... extras) {
		for (Object obj : extras) {
			this.extras.add(obj);
		}
		return this;
	}

	public PerfLogTag build() {
		return new PerfLogTag(namespace, tag, extras);
	}
}
