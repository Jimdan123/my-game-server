package io.myserver.netty.codec;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;

public class HttpUpgradeToWebSocketHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{
	protected String wsPath;

	public HttpUpgradeToWebSocketHandler(String wsPath)
	{
		this.wsPath = wsPath;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req)
	{
		// Handle a bad request.
		if (!req.decoderResult().isSuccess())
		{
			sendHttpResponse(ctx,
					req,
					new DefaultFullHttpResponse(req.protocolVersion(),
							BAD_REQUEST,
							ctx.alloc().buffer(0)));

			return;
		}

		// Handle websocket upgrade request.
		if (req.headers().contains(HttpHeaderNames.UPGRADE,
				HttpHeaderValues.WEBSOCKET,
				true))
		{
			//check path
			if (wsPath.equals(req.uri()))
			{
				//forward to WebSocketServerProtocolHandler
				ctx.fireChannelRead(req.retain());
				return;
			}
		}

		// FORBIDDEN all other requests.
		sendHttpResponse(ctx,
				req,
				new DefaultFullHttpResponse(req.protocolVersion(), FORBIDDEN, ctx.alloc().buffer(0)));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		ctx.close();
	}

	private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res)
	{
		// Generate an error page if response getStatus code is not OK (200).
		HttpResponseStatus responseStatus = res.status();
		if (responseStatus.code() != 200)
		{
			ByteBufUtil.writeUtf8(res.content(), responseStatus.toString());
			HttpUtil.setContentLength(res, res.content().readableBytes());
		}

		// Send the response and close the connection if necessary.
		boolean keepAlive = HttpUtil.isKeepAlive(req) && responseStatus.code() == 200;

		HttpUtil.setKeepAlive(res, keepAlive);
		ChannelFuture future = ctx.writeAndFlush(res);

		if (!keepAlive)
		{
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
