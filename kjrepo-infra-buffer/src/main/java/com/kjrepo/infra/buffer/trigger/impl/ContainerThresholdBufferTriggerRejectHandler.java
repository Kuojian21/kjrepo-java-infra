package com.kjrepo.infra.buffer.trigger.impl;

public interface ContainerThresholdBufferTriggerRejectHandler<E> {

	boolean onReject(E element);

}
