package org.openiam.idm.srvc.secdomain.service;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.jws.WebService;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.dozer.converter.SecurityDomainDozerConverter;
import org.openiam.idm.srvc.secdomain.domain.SecurityDomainEntity;
import org.openiam.idm.srvc.secdomain.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Interface to manager the SecurityDomain that clients will access to gain information about SecurityDomain.
 * @author Suneet Shah
 *
 */
@Service("secDomainService")
@WebService(endpointInterface = "org.openiam.idm.srvc.secdomain.service.SecurityDomainDataService", 
		targetNamespace = "urn:idm.openiam.org/srvc/secdomain/service", 
		serviceName = "SecurityDomainWebService",
		portName = "SecurityDomainWebServicePort")
public class SecurityDomainDataServiceImpl implements SecurityDomainDataService {

	@Autowired
	protected SecurityDomainDAO secDomainDao;
	
	@Autowired
	private SecurityDomainDozerConverter securityDomainDozerConverter;
  
	public List<SecurityDomain> getAllSecurityDomains() {
		final List<SecurityDomainEntity> entityList =  secDomainDao.findAll();
		return securityDomainDozerConverter.convertToDTOList(entityList, true);
	}
  
	  public List<SecurityDomain> getAllDomainsWithExclude(String excludeDomain) {
		  final List<SecurityDomain> domainList = getAllSecurityDomains();
		  if(CollectionUtils.isNotEmpty(domainList)) {
			  for(final Iterator<SecurityDomain> it = domainList.iterator(); it.hasNext();) {
				  final SecurityDomain domain = it.next();
				  if(StringUtils.equalsIgnoreCase(domain.getDomainId(), excludeDomain)) {
					  it.remove();
				  }
			  }
		  }
		  return domainList;
	  }
}
