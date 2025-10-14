package com.kjrepo.infra.network.netty;

import java.util.Map;

import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.term.TermHelper;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Netty {

	private static final Logger logger = LoggerUtils.logger();

	public static Channel server(int port, ChannelInitializer<SocketChannel> initializer) {
		return server(port, initializer, null, null);
	}

	public static Channel server(int port, ChannelInitializer<SocketChannel> initializer,
			Map<ChannelOption<?>, Object> options, Map<ChannelOption<?>, ?> childOptions) {
		EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory()); // accept 线程池
		EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup) //
					.channel(NioServerSocketChannel.class) //
					.childHandler(initializer);
			options.forEach((option, obj) -> {
				option(bootstrap, option, obj);
			});
			childOptions.forEach((option, obj) -> {
				childOption(bootstrap, option, obj);
			});
			Channel channel = bootstrap.bind(port).sync().channel();
			TermHelper.addTerm("", () -> {
				try {
					channel.close();
					channel.closeFuture().sync();
				} catch (Exception e) {
					logger.error("", e);
				} finally {
					bossGroup.shutdownGracefully();
					workerGroup.shutdownGracefully();
				}
			});
			return channel;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Channel client(String host, int port, ChannelInitializer<SocketChannel> initializer) {
		return client(host, port, initializer, null);
	}

	public static Channel client(String host, int port, ChannelInitializer<SocketChannel> initializer,
			Map<ChannelOption<?>, Object> options) {
		try {
			EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class);
			bootstrap.handler(initializer);
			Optional.ofNullable(options).orElseGet(() -> Maps.newHashMap()).forEach((option, obj) -> {
				option(bootstrap, option, obj);
			});
			Channel channel = bootstrap.connect(host, port).sync().channel();
			TermHelper.addTerm("", () -> {
				try {
					channel.close();
					channel.closeFuture().sync();
				} catch (Exception e) {
					logger.error("", e);
				} finally {
					group.shutdownGracefully();
				}
			});
			return channel;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	static <T> void option(AbstractBootstrap<?, ?> bootstrap, ChannelOption<?> option, Object obj) {
		bootstrap.option((ChannelOption<T>) option, (T) obj);
	}

	@SuppressWarnings("unchecked")
	static <T> void childOption(ServerBootstrap bootstrap, ChannelOption<?> option, Object obj) {
		bootstrap.childOption((ChannelOption<T>) option, (T) obj);
	}

}
