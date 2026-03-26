package com.kjrepo.infra.perf.model;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

public class PerfLogTag {

	private final String namespace;
	private final String tag;
	private final List<String> extras;

	public PerfLogTag(String namespace, String tag, List<String> extras) {
		this.namespace = namespace;
		this.tag = tag;
		this.extras = extras;
	}

	public static PerfLogTagBuilder builder() {
		return new PerfLogTagBuilder();
	}

	public String getNamespace() {
		return namespace;
	}

	public String getTag() {
		return tag;
	}

	public List<String> getExtras() {
		return extras;
	}

	@Override
	public int hashCode() {
		List<Object> values = Lists.newArrayList();
		values.add(namespace);
		values.add(tag);
		values.addAll(extras);
		return Objects.hash(values.toArray());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		}
		PerfLogTag other = (PerfLogTag) obj;
		if (!nullToEmpty(namespace).equals(nullToEmpty(other.namespace))
				|| !nullToEmpty(tag).equals(nullToEmpty(other.tag))) {
			return false;
		} else {
			List<String> ex1 = nullToEmpty(extras);
			List<String> ex2 = nullToEmpty(other.extras);
			if (ex1.size() != ex2.size()) {
				return false;
			}
			for (int i = 0, len = ex1.size(); i < len; i++) {
				if (!nullToEmpty(ex1.get(i)).equals(nullToEmpty(ex2.get(i)))) {
					return false;
				}
			}
			return true;
		}

	}

	public String nullToEmpty(String obj) {
		return obj == null ? "" : obj;
	}

	public List<String> nullToEmpty(List<String> list) {
		return list == null ? Lists.newArrayList() : list;
	}

}
