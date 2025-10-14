package com.kjrepo.infra.cluster.instance;

import java.util.Objects;

import com.kjrepo.infra.cluster.ClusterInfo;

public class InstanceInfo<I> {

	public static <I> InstanceInfo<I> of(String name, I info) {
		InstanceInfo<I> ins = new InstanceInfo<>();
		ins.name = name;
		ins.info = info;
		return ins;
	}

	private ClusterInfo<I> clusterInfo;
	private String name;
	private I info;

	public void clusterInfo(ClusterInfo<I> clusterInfo) {
		this.clusterInfo = clusterInfo;
	}

	public ClusterInfo<I> clusterInfo() {
		return this.clusterInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public I getInfo() {
		return info;
	}

	public void setInfo(I info) {
		this.info = info;
	}

	@Override
	public int hashCode() {
		return Objects.hash(info, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstanceInfo<?> other = (InstanceInfo<?>) obj;
		return Objects.equals(info, other.info) && Objects.equals(name, other.name);
	}

}
