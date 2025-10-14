package com.kjrepo.infra.network.jsch;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.kjrepo.infra.common.executor.PooledInfoExecutor;

public abstract class JschBean<B extends Channel, I extends JschBeanInfo<B>> extends PooledInfoExecutor<B, I> {

	/**
	 * @com.jcraft.jsch.Channel.getChannel(String)
	 */
	private final String channel;

	public JschBean(String channel, I info) {
		super(info);
		this.channel = channel;
	}

	@Override
	public void destroy(B bean) throws JSchException {
		bean.getSession().disconnect();
		bean.disconnect();
	}

	@Override
	public boolean validate(B bean) {
		return bean.isConnected() && !bean.isClosed();
	}

	@Override
	protected final B create() throws Exception {
		return JschUtils.channel(info(), this.channel);
	}

}
