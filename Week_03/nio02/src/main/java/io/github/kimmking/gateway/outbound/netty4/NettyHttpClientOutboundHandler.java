package io.github.kimmking.gateway.outbound.netty4;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import java.net.URI;

public class NettyHttpClientOutboundHandler  extends ChannelInboundHandlerAdapter {
    private HttpRequest request;

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        String url = "http://localhost:8088/api/hello";
        //HttpUriRequest request = RequestBuilder.get()
        //    .setUri(new URI(url)).build();
	    DefaultFullHttpRequest request = new DefaultFullHttpRequest(
		    HttpVersion.HTTP_1_1, HttpMethod.GET, new URI(url).toASCIIString());
	    request.headers().set(HttpHeaderNames.HOST, "localhost");
	    request.headers().set(HttpHeaderNames.CONNECTION,
		    HttpHeaderNames.CONNECTION);
	    request.headers().set(HttpHeaderNames.CONTENT_LENGTH,
		    request.content().readableBytes());
	    System.out.println("channelActive: ");
        System.out.println(ctx);
        ctx.writeAndFlush(request);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("msg: ");
        System.out.println(msg);
        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;
	        System.out.println("channelRead : request => ");
	        System.out.println(request);
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            //System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
            //buf.release();

            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                OK, Unpooled.wrappedBuffer(buf));
            response.headers().set(CONTENT_TYPE, "text/plain;charset=UTF-8");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
	        System.out.println("channelRead : response => ");
            System.out.println(response);
            ctx.writeAndFlush(response);
        }
    }

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
		ctx.flush();
	}
}