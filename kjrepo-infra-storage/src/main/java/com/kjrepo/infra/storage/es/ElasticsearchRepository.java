package com.kjrepo.infra.storage.es;

import java.io.IOException;

//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClient.FailureListener;

import com.kjrepo.infra.common.executor.PooledInfoExecutor;

public class ElasticsearchRepository extends PooledInfoExecutor<RestClient, ElasticsearchInfo> {

	public ElasticsearchRepository(ElasticsearchInfo info) {
		super(info);
	}

	@Override
	protected RestClient create() {
		return ElasticsearchUtils.client(info(), new FailureListener());
	}

	@Override
	protected boolean validate(RestClient bean) {
		return bean.isRunning();
	}

	@Override
	protected void destroy(RestClient bean) throws IOException {
		bean.close();
	}

}
