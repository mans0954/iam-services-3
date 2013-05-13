package org.openiam.idm.srvc.secdomain.service;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.secdomain.domain.SecurityDomainEntity;
import org.openiam.idm.srvc.secdomain.dto.SecurityDomain;
import org.openiam.idm.srvc.secdomain.service.SecurityDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SecurityDomainServiceImpl implements SecurityDomainService {

	@Autowired
	protected SecurityDomainDAO secDomainDao;
	
	public List<SecurityDomainEntity> getAllSecurityDomains() {
		return secDomainDao.findAll();
	}
  
	public List<SecurityDomainEntity> getAllDomainsWithExclude(String excludeDomain) {
		final List<SecurityDomainEntity> domainList = getAllSecurityDomains();
		if(CollectionUtils.isNotEmpty(domainList)) {
			for(final Iterator<SecurityDomainEntity> it = domainList.iterator(); it.hasNext();) {
				final SecurityDomainEntity domain = it.next();
				if(StringUtils.equalsIgnoreCase(domain.getDomainId(), excludeDomain)) {
					it.remove();
				}
			}
		}
		return domainList;
	}
}
