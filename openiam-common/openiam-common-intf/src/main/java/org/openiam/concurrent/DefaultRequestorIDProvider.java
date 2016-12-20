package org.openiam.concurrent;

import org.openiam.util.SpringSecurityHelper;

public class DefaultRequestorIDProvider implements RequestorIDProvider {

	@Override
	public String getRequestorId() {
		return SpringSecurityHelper.getRequestorUserId();
	}

}
