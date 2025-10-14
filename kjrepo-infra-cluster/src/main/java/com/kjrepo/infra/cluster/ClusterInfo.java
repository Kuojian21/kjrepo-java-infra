package com.kjrepo.infra.cluster;

import java.util.List;
import java.util.Map;

import com.annimon.stream.Stream;
import com.kjrepo.infra.cluster.instance.InstanceInfo;

public class ClusterInfo<I> {

	private String selector;

	private List<InstanceInfo<I>> instanceInfos;

	private Map<String, Object> commonSetting;

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public List<InstanceInfo<I>> getInstanceInfos() {
		return instanceInfos;
	}

	public void setInstanceInfos(List<InstanceInfo<I>> instances) {
		this.instanceInfos = instances;
	}

	public Map<String, Object> getCommonSetting() {
		return commonSetting;
	}

	public void setCommonSetting(Map<String, Object> instanceCommonSetting) {
		this.commonSetting = instanceCommonSetting;
	}

	public ClusterInfo<I> init() {
		Stream.of(this.instanceInfos).forEach(iinfo -> iinfo.clusterInfo(this));
		return this;
	}

}
