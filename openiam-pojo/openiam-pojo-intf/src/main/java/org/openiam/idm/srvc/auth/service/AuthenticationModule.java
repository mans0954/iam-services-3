package org.openiam.idm.srvc.auth.service;

import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.LogoutRequest;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.user.domain.UserEntity;

public interface AuthenticationModule {

	public void logout(final LogoutRequest request, final IdmAuditLog auditLog) throws Exception;
	public Subject login(final AuthenticationContext context) throws Exception;
}
