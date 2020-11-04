package io.github.kimmking.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @description
 * @author yangjing
 * @date 2020/11/4 7:51 AM
 * @version v1.0
 */
public class HttpRequestHeaderAddFilter implements HttpRequestFilter {

	@Override
	public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
		fullRequest.headers().add("nio", "geektime");
	}
}
