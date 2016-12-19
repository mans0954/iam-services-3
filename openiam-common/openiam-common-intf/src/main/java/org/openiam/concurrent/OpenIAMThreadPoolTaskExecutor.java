package org.openiam.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class OpenIAMThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
	
	private int queueCapacity;
	private boolean allowCoreThreadTimeOut;
	
	private OpenIAMThreadPoolExecutor executor;

	@Override
	protected ExecutorService initializeExecutor(
			ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
		super.initializeExecutor(threadFactory, rejectedExecutionHandler);
		
		OpenIAMThreadPoolExecutor executor  = new OpenIAMThreadPoolExecutor(
				getCorePoolSize(), getMaxPoolSize(), getKeepAliveSeconds(), TimeUnit.SECONDS,
				createQueue(this.queueCapacity), threadFactory, rejectedExecutionHandler);
		if (this.allowCoreThreadTimeOut) {
			executor.allowCoreThreadTimeOut(true);
		}
		this.executor = executor;
		return executor;
	}
	
	@Override
	public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
		if(this.executor == null) {
			throw new IllegalStateException("Thread Executor not initalized");
		}
		return this.executor;
	}
	
	@Override
	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}
	
	public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
		this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
	}
	
	public void setRequestorId(final String requestorId) {
		this.executor.setRequestorId(requestorId);
	}
}
