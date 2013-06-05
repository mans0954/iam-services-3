package org.openiam.idm.srvc.secdomain.service;

import java.util.List;

import org.openiam.idm.srvc.secdomain.domain.SecurityDomainEntity;

public interface SecurityDomainService {

	public List<SecurityDomainEntity> getAllSecurityDomains();
	public List<SecurityDomainEntity> getAllDomainsWithExclude(String excludeDomain);
}
