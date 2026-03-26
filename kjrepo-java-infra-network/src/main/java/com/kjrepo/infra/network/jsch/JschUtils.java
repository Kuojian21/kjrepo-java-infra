package com.kjrepo.infra.network.jsch;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@SuppressWarnings("unchecked")
public class JschUtils {

	/**
	 * com.jcraft.jsch.Channel.getChannel(String)
	 */
	public static <T extends Channel> T channel(JschBeanInfo<T> info, String channelType) throws JSchException {
		info.getSshConfig().putIfAbsent("StrictHostKeyChecking", "no");
		JSch jsch = new JSch();
		if (StringUtils.isNotEmpty(info.getPrvkey()) && StringUtils.isNotEmpty(info.getPubkey())
				&& StringUtils.isNotEmpty(info.getPassphrase())) {
			jsch.addIdentity(info.getPrvkey(), info.getPubkey(), info.getPassphrase().getBytes(StandardCharsets.UTF_8));
			Session session = jsch.getSession(info.getUsername(), info.getHost(), info.getPort());
			session.setConfig(info.getSshConfig());
			Channel channel = session.openChannel(channelType);
			channel.connect(info.getConnectTimeout());
			return (T) channel;
		} else if (StringUtils.isNotEmpty(info.getPassword())) {
			Session session = jsch.getSession(info.getUsername(), info.getHost(), info.getPort());
			session.setPassword(info.getPassword());
			session.setConfig(info.getSshConfig());
			Channel channel = session.openChannel(channelType);
			channel.connect(info.getConnectTimeout());
			return (T) channel;
		} else {
			throw new RuntimeException();
		}
	}

}
