package com.kjrepo.infra.network.netty.ws;

import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;

import com.annimon.stream.function.BiConsumer;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.network.netty.Netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class WsClient {

	public static WsClient client(String url, BiConsumer<ChannelHandlerContext, TextWebSocketFrame> frameConsumer) {
		try {
			return new WsClient(url, frameConsumer);
		} catch (URISyntaxException | SSLException e) {
			throw new RuntimeException(e);
		}
	}

	private final Logger logger = LoggerUtils.logger(getClass());
	private final WsClientHandler handler;
	private final Channel channel;

	public WsClient(String url, BiConsumer<ChannelHandlerContext, TextWebSocketFrame> frameConsumer)
			throws URISyntaxException, SSLException {
		URI uri = new URI(url);
		String scheme = uri.getScheme();
		SslContext sslCtx;
		int[] port = new int[] { uri.getPort() };
		if (scheme.equals("ws")) {
			sslCtx = null;
			if (port[0] == -1) {
				port[0] = 80;
			}
		} else if (scheme.equals("wss")) {
			sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			if (port[0] == -1) {
				port[0] = 443;
			}
		} else {
			throw new RuntimeException("not supported scheme!!!");
		}
		this.handler = new WsClientHandler(uri, frameConsumer);
		this.channel = Netty.client(uri.getHost(), port[0], new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline p = ch.pipeline();
				if (sslCtx != null) {
					p.addLast(sslCtx.newHandler(ch.alloc(), uri.getHost(), port[0]));
				}
				p.addLast(new HttpClientCodec(), //
						new HttpObjectAggregator(8192), //
						WebSocketClientCompressionHandler.INSTANCE, //
						WsClient.this.handler);
			}
		});
	}

	public void sendMessage(String msg) {
		try {
			handler.handshakeFuture().sync();
			channel.writeAndFlush(new TextWebSocketFrame(msg));
		} catch (InterruptedException e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}

}
