package org.openiam.concurrent;

import java.util.concurrent.Callable;

import org.openiam.util.SpringSecurityHelper;

public class OpenIAMCallable<V> implements Callable<V> {
	
	private Callable<V> callable;
	private String requestorId;

	private OpenIAMCallable() {}
	
	public OpenIAMCallable(final Callable<V> callable) {
		this.callable = callable;
		this.requestorId = SpringSecurityHelper.getRequestorUserId();
	}

	@Override
	public V call() throws Exception {
		try {
			SpringSecurityHelper.setRequesterUserId(requestorId);
			return this.callable.call();
		} finally {
			SpringSecurityHelper.clearContext();
		}
	}
}
