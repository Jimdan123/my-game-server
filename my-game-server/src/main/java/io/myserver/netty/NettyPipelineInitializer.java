package io.myserver.netty;

import io.myserver.netty.codec.HttpUpgradeToWebSocketHandler;
import io.myserver.netty.codec.WebSocketDecoder;
import io.myserver.netty.codec.WebSocketEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

public class NettyPipelineInitializer extends ChannelInitializer<SocketChannel>
{
	private static final String WEBSOCKET_PATH 	= "/websocket";

	private static final int MAX_PACKAGE_SIZE 	= 128*1024;

	private static final LengthFieldPrepender LENGTH_FIELD_ENCODER = new LengthFieldPrepender(4);

	private static final StringDecoder DECODER = new StringDecoder(StandardCharsets.UTF_8);
	private static final StringEncoder ENCODER = new StringEncoder(StandardCharsets.UTF_8);

	public NettyPipelineInitializer()
	{

	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception
	{
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(MAX_PACKAGE_SIZE));
		pipeline.addLast(new HttpUpgradeToWebSocketHandler(WEBSOCKET_PATH));
		pipeline.addLast(new WebSocketServerCompressionHandler());
		pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
		pipeline.addLast(new WebSocketDecoder());
		pipeline.addLast(new WebSocketEncoder());

		// and then business logic.
		pipeline.addLast(new MyRequestHandler());
	}
}
