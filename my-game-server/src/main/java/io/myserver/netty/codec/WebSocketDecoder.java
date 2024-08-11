package io.myserver.netty.codec;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.List;

public class WebSocketDecoder extends MessageToMessageDecoder<WebSocketFrame>
{
	@Override
	public void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception
	{
		if (frame instanceof BinaryWebSocketFrame)
		{
			byte[] data = ByteBufUtil.getBytes(frame.content());

			out.add(data); // decode BinaryWebSocketFrame to byte array
		}
		else
		{
			//discard other ws frames
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
	{
		//fire from WebSocketServerProtocolHandler
		if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete)
		{
			//Channel upgrade to websocket, remove HttpUpgradeToWebSocketHandler.
			ctx.pipeline().remove(HttpUpgradeToWebSocketHandler.class);
		}
		else
		{
			super.userEventTriggered(ctx, evt);
		}
	}
}
