package com.kjrepo.infra.cluster.standby;

import com.kjrepo.infra.cluster.ClusterInfo;

public class StandbyInfo<I> {

	private I master;

	private ClusterInfo<I> slaves;

	public I getMaster() {
		return master;
	}

	public void setMaster(I master) {
		this.master = master;
	}

	public ClusterInfo<I> getSlaves() {
		return slaves;
	}

	public void setSlaves(ClusterInfo<I> slaves) {
		this.slaves = slaves;
	}

}
