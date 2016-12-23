package org.openiam.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class OpenIAMThreadPoolExecutor extends ThreadPoolExecutor {

	private SecurityInfoProvider requestorIDProvider = new DefaultSecurityInfoProvider();

	public OpenIAMThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}
	
	

	public OpenIAMThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}



	public OpenIAMThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}



	public OpenIAMThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		if(!(runnable instanceof OpenIAMRunnable)) {
			runnable = new OpenIAMRunnable(runnable, requestorIDProvider.getRequestorId(), requestorIDProvider.getLanguageId());
		}
		return super.newTaskFor(runnable, value);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		// TODO Auto-generated method stub
		if(!(callable instanceof OpenIAMCallable)) {
			callable = new OpenIAMCallable<T>(callable, requestorIDProvider.getRequestorId(), requestorIDProvider.getLanguageId());
		}
			
		return super.newTaskFor(callable);
	}

	@Override
	public void execute(Runnable command) {
		if(command != null) {
			if(!(command instanceof OpenIAMRunnable)) {
				super.execute(new OpenIAMRunnable(command, requestorIDProvider.getRequestorId(), requestorIDProvider.getLanguageId()));
			}
		}
	}



	public void setRequestorIDProvider(SecurityInfoProvider requestorIDProvider) {
		this.requestorIDProvider = requestorIDProvider;
	}
	
	
}
