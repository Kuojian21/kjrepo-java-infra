package com.kjrepo.infra.perf;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

public class PerfLogTag {

	private final String namespace;
	private final String tag;
	private final List<Object> extras;

	public PerfLogTag(String namespace, String tag, List<Object> extras) {
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

	public List<Object> getExtras() {
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
			List<Object> ex1 = nullToEmpty(extras);
			List<Object> ex2 = nullToEmpty(other.extras);
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

	public Object nullToEmpty(Object obj) {
		return obj == null ? "" : obj;
	}

	public List<Object> nullToEmpty(List<Object> list) {
		return list == null ? Lists.newArrayList() : list;
	}

}
