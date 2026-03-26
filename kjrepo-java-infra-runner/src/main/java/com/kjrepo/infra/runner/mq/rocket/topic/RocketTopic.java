package com.kjrepo.infra.runner.mq.rocket.topic;

import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.SendReceipt;

import com.kjrepo.infra.runner.mq.ITopic;
import com.kjrepo.infra.runner.mq.rocket.client.RocketProducerClient;

public interface RocketTopic extends ITopic {

	default SendReceipt send(MessageBuilder builder) {
		return RocketProducerClient.send(this, builder);
	}

}
