package org.openiam.concurrent;

import java.util.concurrent.Callable;

import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.util.SpringSecurityHelper;

public class OpenIAMCallable<V> implements Callable<V> {
	
	private Callable<V> callable;
	private String requestorId;

	private OpenIAMCallable() {}
	
	public OpenIAMCallable(final Callable<V> callable, final String requestorId) {
		this.callable = callable;
		this.requestorId = (StringUtils.isNotBlank(requestorId)) ? requestorId : SpringSecurityHelper.getRequestorUserId();
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
