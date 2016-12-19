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

	private String requestorId;

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

	


	public String getRequestorId() {
		return requestorId;
	}



	public void setRequestorId(String requestorId) {
		this.requestorId = requestorId;
	}



	@Override
	public void execute(Runnable command) {
		super.execute(new OpenIAMRunnable(command));
	}



	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
	}



	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
	}



	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return super.newTaskFor(new OpenIAMRunnable(runnable), value);
	}



	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		// TODO Auto-generated method stub
		return super.newTaskFor(new OpenIAMCallable<T>(callable));
	}



	@Override
	public Future<?> submit(Runnable task) {
		// TODO Auto-generated method stub
		return super.submit(new OpenIAMRunnable(task));
	}



	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		// TODO Auto-generated method stub
		return super.submit(new OpenIAMRunnable(task), result);
	}



	@Override
	public <T> Future<T> submit(Callable<T> task) {
		// TODO Auto-generated method stub
		return super.submit(new OpenIAMCallable<T>(task));
	}



	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		if(tasks != null) {
			return super.invokeAny(tasks.stream().map(e -> new OpenIAMCallable<T>(e)).collect(Collectors.toList()));
		} else {
			return null;
		}
	}



	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		if(tasks != null) {
			return super.invokeAny(tasks.stream().map(e -> new OpenIAMCallable<T>(e)).collect(Collectors.toList()), timeout, unit);
		} else {
			return null;
		}
	}



	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		if(tasks != null) {
			return super.invokeAll(tasks.stream().map(e -> new OpenIAMCallable<T>(e)).collect(Collectors.toList()));
		} else {
			return null;
		}
		
	}



	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		if(tasks != null) {
			return super.invokeAll(tasks.stream().map(e -> new OpenIAMCallable<T>(e)).collect(Collectors.toList()), timeout, unit);
		} else {
			return null;
		}
	}



	
}
