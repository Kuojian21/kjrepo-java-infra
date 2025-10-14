package com.kjrepo.infra.common.buffer;

import java.util.concurrent.locks.Condition;

public interface BufferTriggerRejectHandler<E> {

	boolean onReject(E element, Condition condition);

}
