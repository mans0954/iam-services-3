package org.openiam.concurrent;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class OpenIAMThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {
	
	@Override
	protected ScheduledExecutorService createExecutor(
			int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
		return new OpenIAMScheduledExecutorService(poolSize, threadFactory, rejectedExecutionHandler);
	}
	
	public void setRequestorIDProvider(final SecurityInfoProvider provider) {
		((OpenIAMScheduledExecutorService)getScheduledExecutor()).setRequestorIDProvider(provider);
	}
}
