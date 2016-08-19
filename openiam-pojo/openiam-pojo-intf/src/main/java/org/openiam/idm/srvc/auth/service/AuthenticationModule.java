package org.openiam.idm.srvc.auth.service;

import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.base.request.LogoutRequest;
import org.openiam.idm.srvc.auth.dto.Subject;

public interface AuthenticationModule {

	void logout(final LogoutRequest request, final IdmAuditLogEntity auditLog) throws Exception;
	Subject login(final AuthenticationContext context) throws Exception;
}
