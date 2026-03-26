package com.kjrepo.infra.network.netty.ws;

import java.net.URI;

import org.slf4j.Logger;

import com.annimon.stream.function.BiConsumer;
import com.kjrepo.infra.common.logger.LoggerUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.CharsetUtil;

public class WsClientHandler extends SimpleChannelInboundHandler<Object> {

	private final Logger logger = LoggerUtils.logger(getClass());

	private final WebSocketClientHandshaker handshaker;
	private final BiConsumer<ChannelHandlerContext, TextWebSocketFrame> handler;
	private volatile ChannelPromise handshakeFuture;

	public WsClientHandler(URI webSocketURL, BiConsumer<ChannelHandlerContext, TextWebSocketFrame> handler) {
		this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(webSocketURL, WebSocketVersion.V13, null, true,
				new DefaultHttpHeaders());
		this.handler = handler;
	}

	public ChannelFuture handshakeFuture() {
		return handshakeFuture;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		handshakeFuture = ctx.newPromise();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		handshaker.handshake(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		logger.info("WebSocket Client disconnected!");
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		Channel ch = ctx.channel();
		if (!handshaker.isHandshakeComplete()) {
			try {
				handshaker.finishHandshake(ch, (FullHttpResponse) msg);
				logger.info("WebSocket Client connected!");
				handshakeFuture.setSuccess();
			} catch (WebSocketHandshakeException e) {
				logger.error("WebSocket Client failed to connect");
				handshakeFuture.setFailure(e);
			}
			return;
		}

		if (msg instanceof FullHttpResponse) {
			FullHttpResponse response = (FullHttpResponse) msg;
			throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content="
					+ response.content().toString(CharsetUtil.UTF_8) + ')');
		}

		WebSocketFrame frame = (WebSocketFrame) msg;
		if (frame instanceof TextWebSocketFrame) {
			TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
			this.handler.accept(ctx, textFrame);
		} else if (frame instanceof PongWebSocketFrame) {
			logger.info("WebSocket Client received pong");
		} else if (frame instanceof CloseWebSocketFrame) {
			logger.info("WebSocket Client received closing");
			ch.close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("", cause);
		if (!handshakeFuture.isDone()) {
			handshakeFuture.setFailure(cause);
		}
		ctx.close();
	}
}
