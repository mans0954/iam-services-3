package org.openiam.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openiam.util.SpringSecurityHelper;

public class OpenIAMScheduledExecutorService extends ScheduledThreadPoolExecutor {
	
	private RequestorIDProvider requestorIDProvider = new DefaultRequestorIDProvider();

	public OpenIAMScheduledExecutorService(int corePoolSize, RejectedExecutionHandler handler) {
		super(corePoolSize, handler);
		// TODO Auto-generated constructor stub
	}

	public OpenIAMScheduledExecutorService(int corePoolSize, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, threadFactory, handler);
		// TODO Auto-generated constructor stub
	}

	public OpenIAMScheduledExecutorService(int corePoolSize, ThreadFactory threadFactory) {
		super(corePoolSize, threadFactory);
		// TODO Auto-generated constructor stub
	}

	public OpenIAMScheduledExecutorService(int corePoolSize) {
		super(corePoolSize);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
		return new RunnableScheduledFutureWrapper<V>(task);
	}

	@Override
	protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
		return new RunnableScheduledFutureWrapper<V>(task);
	}
	
	private class RunnableScheduledFutureWrapper<V> implements RunnableScheduledFuture<V> {
		
		private RunnableScheduledFuture<V> future;
		private String userId = null;
		
		RunnableScheduledFutureWrapper(final RunnableScheduledFuture<V> future) {
			this.future = future;
			this.userId = requestorIDProvider.getRequestorId();
		}

		@Override
		public void run() {
			try {
				SpringSecurityHelper.setRequesterUserId(userId);
				this.future.run();
			} finally {
				SpringSecurityHelper.clearContext();
			}
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return this.future.cancel(mayInterruptIfRunning);
		}

		@Override
		public boolean isCancelled() {
			return this.future.isCancelled();
		}

		@Override
		public boolean isDone() {
			return this.future.isDone();
		}

		@Override
		public V get() throws InterruptedException, ExecutionException {
			return this.future.get();
		}

		@Override
		public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			return this.future.get(timeout, unit);
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return this.future.getDelay(unit);
		}

		@Override
		public int compareTo(Delayed o) {
			return this.future.compareTo(o);
		}

		@Override
		public boolean isPeriodic() {
			return this.future.isPeriodic();
		}
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		// TODO Auto-generated method stub
		return super.newTaskFor(new OpenIAMRunnable(runnable, requestorIDProvider.getRequestorId()), value);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		// TODO Auto-generated method stub
		return super.newTaskFor(new OpenIAMCallable<T>(callable, requestorIDProvider.getRequestorId()));
	}

	public void setRequestorIDProvider(RequestorIDProvider requestorIDProvider) {
		this.requestorIDProvider = requestorIDProvider;
	}

	
}
