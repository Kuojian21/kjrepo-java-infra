//package com.kjrepo.infra.buffer.legacy;
//
//import org.slf4j.Logger;
//
//import com.kjrepo.infra.buffer.legacy.GenericBatchConsumerTriggerBuilder;
//import com.kjrepo.infra.buffer.legacy.GenericSimpleBufferTriggerBuilder;
//import com.kjrepo.infra.common.logger.LoggerUtils;
//
//public class BufferTriggerBuilder<E> {
//
//	public static final Logger logger = LoggerUtils.logger(BufferTriggerBuilder.class);
//
//	public static <E, C> GenericSimpleBufferTriggerBuilder<E, C> simple() {
//		return new GenericSimpleBufferTriggerBuilder<>();
//	}
//
//	public static <E> GenericBatchConsumerTriggerBuilder<E> batchBlocking() {
//		return new GenericBatchConsumerTriggerBuilder<>();
//	}
//
//}
