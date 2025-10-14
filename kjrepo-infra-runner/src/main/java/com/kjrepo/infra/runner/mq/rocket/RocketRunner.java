package com.kjrepo.infra.runner.mq.rocket;

import org.apache.rocketmq.client.apis.message.MessageView;

import com.kjrepo.infra.runner.Runner;

public abstract class RocketRunner implements Runner {

	public abstract String topic();

	public abstract String consumerGroup();

	public abstract String tag();

	public abstract void handle(MessageView message);

}
