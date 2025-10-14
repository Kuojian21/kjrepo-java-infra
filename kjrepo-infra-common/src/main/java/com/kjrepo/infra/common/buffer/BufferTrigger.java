package com.kjrepo.infra.common.buffer;

public interface BufferTrigger<E, C> {

	static <E, C> BufferTriggerBuilder<E, C> builder() {
		return new BufferTriggerBuilder<>();
	}

	void enqueue(E element);

}
