package com.kjrepo.infra.perf;

public class PerfUtils {

	public static PerfContext perf(String namespace, String tag, Object... extras) {
		return PerfFactory.DEFAULT
				.perfContext(PerfLogTag.builder().setNamespace(namespace).setTag(tag).addExtras(extras).build());
	}

}
