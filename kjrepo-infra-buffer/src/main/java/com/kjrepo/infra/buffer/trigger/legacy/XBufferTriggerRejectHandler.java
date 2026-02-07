package com.kjrepo.infra.buffer.trigger.legacy;

import java.util.concurrent.locks.Condition;

interface XBufferTriggerRejectHandler<E> {

	boolean onReject(E element, Condition condition);

}
