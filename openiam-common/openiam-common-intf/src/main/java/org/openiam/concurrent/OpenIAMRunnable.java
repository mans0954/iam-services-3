package org.openiam.concurrent;

import org.openiam.util.SpringSecurityHelper;

public class OpenIAMRunnable implements Runnable {

	private Runnable runnable;
	private String requestorId;
	
	private OpenIAMRunnable() {}
	
	public OpenIAMRunnable(final Runnable runnable) {
		this.requestorId = SpringSecurityHelper.getRequestorUserId();
		this.runnable = runnable;
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
