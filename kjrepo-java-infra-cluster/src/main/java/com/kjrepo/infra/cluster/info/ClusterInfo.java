package com.kjrepo.infra.cluster.info;

import java.util.List;
import java.util.Map;

import com.annimon.stream.Stream;
import com.kjrepo.infra.cluster.selector.RandomSelector;
import com.kjrepo.infra.cluster.selector.Selector;

public class ClusterInfo<I> {

	private Selector selector = new RandomSelector();

	private List<InstanceInfo<I>> instanceInfos;

	private Map<String, Object> commonSettings;

	public Selector getSelector() {
		return selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public List<InstanceInfo<I>> getInstanceInfos() {
		return instanceInfos;
	}

	public void setInstanceInfos(List<InstanceInfo<I>> instances) {
		this.instanceInfos = instances;
	}

	public Map<String, Object> getCommonSettings() {
		return commonSettings;
	}

	public void setCommonSettings(Map<String, Object> commonSettings) {
		this.commonSettings = commonSettings;
	}

	public void init() {
		Stream.of(this.instanceInfos).forEach(iinfo -> iinfo.clusterInfo(this));
	}

}
