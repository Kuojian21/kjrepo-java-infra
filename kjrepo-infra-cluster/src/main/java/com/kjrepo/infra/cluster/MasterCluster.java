package com.kjrepo.infra.cluster;

public interface MasterCluster<R> {

	Master<R> getResource();

	Master<R> getResource(Long key);

}