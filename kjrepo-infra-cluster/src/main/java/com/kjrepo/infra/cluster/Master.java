package com.kjrepo.infra.cluster;

public interface Master<R> {

	R master();

	R slave();

}
