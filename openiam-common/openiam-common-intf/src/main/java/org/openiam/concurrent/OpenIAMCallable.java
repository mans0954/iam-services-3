package org.openiam.concurrent;

import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.openiam.util.SpringSecurityHelper;

public class OpenIAMCallable<V> implements Callable<V> {
	
	private Callable<V> callable;
	private String requestorId;
	private String languageId;

	private OpenIAMCallable() {}
	
	public OpenIAMCallable(final Callable<V> callable, final String requestorId, final String languageId) {
		this.callable = callable;
		this.requestorId = (StringUtils.isNotBlank(requestorId)) ? requestorId : SpringSecurityHelper.getRequestorUserId();
		this.languageId = StringUtils.trimToNull(languageId);
	}

	@Override
	public V call() throws Exception {
		try {
			SpringSecurityHelper.setAuthenticationInformation(requestorId, languageId);
			return this.callable.call();
		} finally {
			SpringSecurityHelper.clearContext();
		}
	}
}
