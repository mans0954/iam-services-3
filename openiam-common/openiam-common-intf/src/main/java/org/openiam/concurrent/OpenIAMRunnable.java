package org.openiam.concurrent;

import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.BaseObject;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.util.SpringSecurityHelper;

public class OpenIAMRunnable implements Runnable {

	private Runnable runnable;
	private String requestorId;
	
	private OpenIAMRunnable() {}
	
	public OpenIAMRunnable(final Runnable runnable) {
		this.requestorId = SpringSecurityHelper.getRequestorUserId();
		this.runnable = runnable;
	}
	
	public OpenIAMRunnable(final Runnable runnable, final String requestorId) {
		this.requestorId = (StringUtils.isNotBlank(requestorId)) ? requestorId : SpringSecurityHelper.getRequestorUserId();
		this.runnable = runnable;
	}
	
	public OpenIAMRunnable(final Runnable runnable, final BaseServiceRequest request) {
		this(runnable, (request != null) ? request.getRequesterId() : null);
	}
	
	public OpenIAMRunnable(final Runnable runnable, final BaseObject request) {
		this(runnable, (request != null) ? request.getRequestorUserId() : null);
	}

	@Override
	public void run() {
		try {
			SpringSecurityHelper.setRequesterUserId(requestorId);
			this.runnable.run();
		} finally {
			SpringSecurityHelper.clearContext();
		}
	}
}
