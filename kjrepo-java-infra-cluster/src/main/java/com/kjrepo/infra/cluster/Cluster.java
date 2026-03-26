package com.kjrepo.infra.cluster;

public interface Cluster<R> {

	R getResource();

	R getResource(Long key);

}
