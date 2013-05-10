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
import org.springframework.transaction.annotation.Transactional;
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
	private SecurityDomainService domainService;
	
	@Autowired
	private SecurityDomainDozerConverter securityDomainDozerConverter;
  
	public List<SecurityDomain> getAllSecurityDomains() {
		final List<SecurityDomainEntity> entityList =  domainService.getAllSecurityDomains();
		return securityDomainDozerConverter.convertToDTOList(entityList, true);
	}
  
	  public List<SecurityDomain> getAllDomainsWithExclude(String excludeDomain) {
		  final List<SecurityDomainEntity> entityList =  domainService.getAllDomainsWithExclude(excludeDomain);
		  return securityDomainDozerConverter.convertToDTOList(entityList, true);
	  }
}
