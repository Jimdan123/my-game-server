package io.myserver.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

public class WebSocketEncoder extends MessageToMessageEncoder<byte[]>
{
	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> list) throws Exception
	{
		//encode byte buf to BinaryWebSocketFrame
		BinaryWebSocketFrame bin_ws_frame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg));

		list.add(bin_ws_frame);
	}
}