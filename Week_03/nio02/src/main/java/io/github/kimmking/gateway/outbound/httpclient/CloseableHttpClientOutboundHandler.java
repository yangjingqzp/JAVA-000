package io.github.kimmking.gateway.outbound.httpclient;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @description
 * @author yangjing
 * @date 2020/11/2 11:55 PM
 * @version v1.0
 */
public class CloseableHttpClientOutboundHandler {
	private String backendUrl;
	private CloseableHttpClient httpclient;
	private ExecutorService proxyService;

	public CloseableHttpClientOutboundHandler(String backendUrl){
		this.backendUrl = backendUrl.endsWith("/")?backendUrl.substring(0,backendUrl.length()-1):backendUrl;

		int cores = Runtime.getRuntime().availableProcessors() * 2;
		long keepAliveTime = 1000;
		int queueSize = 2048;
		RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
		proxyService = new ThreadPoolExecutor(cores, cores,
			keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
			new HttpClientThreadFactory("proxyService"), handler);

		this.httpclient = HttpClients.custom().setMaxConnTotal(40)
			.setMaxConnPerRoute(8)
			.setKeepAliveStrategy((response,context) -> 6000)
			.build();
	}

	public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) {
		final String url = this.backendUrl + fullRequest.uri();
		proxyService.submit(()->fetchGet(fullRequest, ctx, url));
	}

	private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {
		try {
			final HttpGet httpGet = new HttpGet(url);
			// add headers
			for (Entry<String, String> entry : inbound.headers()) {
				httpGet.addHeader(entry.getKey(), entry.getValue());
			}
			CloseableHttpResponse response = httpclient.execute(httpGet);
			handleResponse(inbound, ctx, response);
		}catch (ClientProtocolException e) {
			System.out.println("ClientProtocolException: "+ e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException: "+ e.getMessage());
			e.printStackTrace();
		}
	}

	private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final HttpResponse endpointResponse) {
		FullHttpResponse response;
		try {
			byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());

			response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
			response.headers().set("Content-Type", "application/json");
			response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));
		} catch (IOException e) {
			System.out.println("IOException: "+ e.getMessage());
			e.printStackTrace();
			response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
		}

        for (Header e : endpointResponse.getAllHeaders()) {
            response.headers().set(e.getName(),e.getValue());
            System.out.println(e.getName() + " => " + e.getValue());
        }

		if (fullRequest != null) {
			if (!HttpUtil.isKeepAlive(fullRequest)) {
				ctx.write(response).addListener(ChannelFutureListener.CLOSE);
			} else {
				ctx.write(response);
			}
		}
		ctx.flush();
	}
}
