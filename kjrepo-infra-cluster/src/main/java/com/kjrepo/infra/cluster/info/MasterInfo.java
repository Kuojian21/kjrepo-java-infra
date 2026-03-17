package com.kjrepo.infra.cluster.info;

public class MasterInfo<I> {

	private InstanceInfo<I> master;

	private ClusterInfo<I> slaves;

	public InstanceInfo<I> getMaster() {
		return master;
	}

	public void setMaster(InstanceInfo<I> master) {
		this.master = master;
	}

	public ClusterInfo<I> getSlaves() {
		return slaves;
	}

	public void setSlaves(ClusterInfo<I> slaves) {
		this.slaves = slaves;
	}

}
