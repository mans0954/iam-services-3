package org.openiam.idm.srvc.secdomain.service;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.secdomain.domain.SecurityDomainEntity;
import org.springframework.stereotype.Repository;

@Repository("secDomainDAO")
public class SecurityDomainDAOImpl extends BaseDaoImpl<SecurityDomainEntity, String> implements SecurityDomainDAO {

	@Override
	protected String getPKfieldName() {
		return "domainId";
	}

}
