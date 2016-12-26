/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.auth.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.util.SpringSecurityHelper;
import org.openiam.base.request.AuthenticationRequest;

public class AuthenticationContext extends AuthenticationRequest {

	private IdmAuditLogEntity event;
	private String authProviderId;

	private static final Log log = LogFactory.getLog(AuthenticationContext.class);
	


	public AuthenticationContext(final AuthenticationRequest request) {
		super.setClientIP(request.getClientIP());
		super.setPatternId(request.getPatternId());
		super.setNodeIP(request.getNodeIP());
		super.setPassword(request.getPassword());
		super.setPrincipal(request.getPrincipal());
		super.setRequestSource(request.getRequestSource());
		super.setSocialUserProfile(request.getSocialUserProfile());
		super.setSkipPasswordCheck(request.isSkipPasswordCheck());
		super.setSkipUserStatusCheck(request.isSkipUserStatusCheck());
	}

	public String getAuthProviderId() {
		return authProviderId;
	}

	public void setAuthProviderId(String authProviderId) {
		this.authProviderId = authProviderId;
	}

	public IdmAuditLogEntity getEvent() {
		return event;
	}

	public void setEvent(IdmAuditLogEntity event) {
		this.event = event;
	}
}