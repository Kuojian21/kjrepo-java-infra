package com.kjrepo.infra.network.netty.ws;

import java.util.List;
import java.util.Map;

import com.annimon.stream.function.BiConsumer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.kjrepo.infra.network.netty.Netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
//import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WsServer {

	public static Channel server(int port, Map<String, SimpleChannelInboundHandler<WebSocketFrame>> handlers) {
		return server(port, Lists.newArrayList(new SimpleChannelInboundHandler<FullHttpRequest>() {
			@Override
			protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
				String uri = req.uri();
				for (Map.Entry<String, SimpleChannelInboundHandler<WebSocketFrame>> entry : handlers.entrySet()) {
					if (uri.startsWith(entry.getKey())) {
						ctx.pipeline().addLast(new WebSocketServerProtocolHandler(entry.getKey()));
						ctx.pipeline().addLast(entry.getValue());
						ctx.fireChannelRead(req.retain());
						return;
					}
				}
				ctx.writeAndFlush(new DefaultHttpResponse(req.protocolVersion(), HttpResponseStatus.NOT_FOUND))
						.addListener(ChannelFutureListener.CLOSE);
			}
		}));
	}

	public static Channel server(int port, String webSocketPath,
			BiConsumer<ChannelHandlerContext, TextWebSocketFrame> frameConsumer) {
		return server(port, Lists.newArrayList(new WebSocketServerProtocolHandler(webSocketPath),
				new SimpleChannelInboundHandler<WebSocketFrame>() {
					@Override
					protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
						if (msg instanceof TextWebSocketFrame) {
							frameConsumer.accept(ctx, (TextWebSocketFrame) msg);
						} else {
							throw new RuntimeException("unsupported msg-type:" + msg.getClass());
						}
					}
				}));
	}

	public static Channel server(int port, List<ChannelHandler> handlers) {
		return Netty.server(port, //
				new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						// HTTP 编解码器
						pipeline.addLast(new HttpServerCodec());
						// 支持大数据流
						pipeline.addLast(new ChunkedWriteHandler());
						// HTTP 消息聚合
						pipeline.addLast(new HttpObjectAggregator(65536));
						handlers.forEach(handler -> {
							pipeline.addLast(handler);
						});
					}
				}, //
				ImmutableMap.of(ChannelOption.SO_BACKLOG, 128), //
				ImmutableMap.of(ChannelOption.SO_KEEPALIVE, true) //
		);
	}

}