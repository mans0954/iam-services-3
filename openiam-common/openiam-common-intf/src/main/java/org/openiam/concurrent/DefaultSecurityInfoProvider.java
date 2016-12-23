package org.openiam.concurrent;

import org.openiam.util.SpringSecurityHelper;

public class DefaultSecurityInfoProvider implements SecurityInfoProvider {

	@Override
	public String getRequestorId() {
		return SpringSecurityHelper.getRequestorUserId();
	}

	@Override
	public String getLanguageId() {
		return SpringSecurityHelper.getLanguageId();
	}

}
