package com.kjrepo.infra.network.netty.ws;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.annimon.stream.IntStream;
import com.kjrepo.infra.common.logger.LoggerUtils;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WsDemo {

	public static void main(String[] args) throws InterruptedException {
		Logger logger = LoggerUtils.logger(WsDemo.class);

		new Thread(() -> {
			WsServer.server(8080, "/ws", (ctx, tmsg) -> {
				logger.info("Server:{}", tmsg.text());
				ctx.channel().writeAndFlush(new TextWebSocketFrame(tmsg.text().toUpperCase()));
			});
		}).start();

		Thread.sleep(TimeUnit.SECONDS.toMillis(10));

		new Thread(() -> {
			WsClient client = WsClient.client("ws://127.0.0.1:8080/ws", (ctx, tmsg) -> {
				logger.info("Client:{}", tmsg.text());
			});
			IntStream.range(0, 100).forEach(i -> {
				client.sendMessage("message:" + i);
			});
		}).start();
	}

}
