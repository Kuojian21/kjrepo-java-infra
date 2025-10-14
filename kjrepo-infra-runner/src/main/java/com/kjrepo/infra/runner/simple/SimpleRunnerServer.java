package com.kjrepo.infra.runner.simple;

import java.util.List;

//import org.springframework.stereotype.Service;

import com.kjrepo.infra.runner.server.AbstractRunnerServer;

//@Service
public class SimpleRunnerServer extends AbstractRunnerServer<SimpleRunner> {

	@Override
	public AbstractRunnerServer<SimpleRunner> run(List<SimpleRunner> runners) {
		runners.forEach(runner -> {
			new Thread(new SimpleRunnerRunnable(runner)).start();
		});
		return this;
	}

}
