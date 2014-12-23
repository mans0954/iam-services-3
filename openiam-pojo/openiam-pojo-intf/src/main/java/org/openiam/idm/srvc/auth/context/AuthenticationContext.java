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
import org.openiam.base.ws.ObjectMapAdapter;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationContext extends AuthenticationRequest {

	private IdmAuditLog event;
	private String authProviderId;

	private static final Log log = LogFactory.getLog(AuthenticationContext.class);
	


	public AuthenticationContext(final AuthenticationRequest request) {
		super.setClientIP(request.getClientIP());
		super.setPatternId(request.getPatternId());
		super.setLanguageId(request.getLanguageId());
		super.setNodeIP(request.getNodeIP());
		super.setPassword(request.getPassword());
		super.setPrincipal(request.getPrincipal());
		super.setRequestSource(request.getRequestSource());
		super.setSocialUserProfile(request.getSocialUserProfile());
	}

	public String getAuthProviderId() {
		return authProviderId;
	}

	public void setAuthProviderId(String authProviderId) {
		this.authProviderId = authProviderId;
	}

	public IdmAuditLog getEvent() {
		return event;
	}

	public void setEvent(IdmAuditLog event) {
		this.event = event;
	}
	
	
}