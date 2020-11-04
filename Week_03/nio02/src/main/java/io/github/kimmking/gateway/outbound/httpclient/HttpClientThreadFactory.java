/**
 * 深圳市灵智数科有限公司版权所有
 */
package io.github.kimmking.gateway.outbound.httpclient;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description
 * @author yangjing
 * @date 2020/11/4 10:08 PM
 * @version v1.0
 */
public class HttpClientThreadFactory implements ThreadFactory{
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);

	private final String namePrefix;
	private final boolean daemon;

	public HttpClientThreadFactory(String namePrefix, boolean daemon) {
		this.daemon = daemon;
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() :
			Thread.currentThread().getThreadGroup();
		this.namePrefix = namePrefix;
	}

	public HttpClientThreadFactory(String namePrefix) {
		this(namePrefix, false);
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix + "-httpClient-thread-" + threadNumber.getAndIncrement(), 0);
		t.setDaemon(daemon);
		return t;
	}
}
