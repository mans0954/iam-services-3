package org.openiam.idm.srvc.secdomain.service;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.jws.WebService;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.secdomain.dto.*;
/**
 * Interface to manager the SecurityDomain that clients will access to gain information about SecurityDomain.
 * @author Suneet Shah
 *
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.secdomain.service.SecurityDomainDataService", 
		targetNamespace = "urn:idm.openiam.org/srvc/secdomain/service", 
		serviceName = "SecurityDomainWebService",
		portName = "SecurityDomainWebServicePort")
public class SecurityDomainDataServiceImpl implements SecurityDomainDataService {

	protected SecurityDomainDAO secDomainDao;
	
	public SecurityDomainDataServiceImpl() {
		
	}
	
	public SecurityDomainDAO getSecDomainDao() {
		return secDomainDao;
	}

	public void setSecDomainDao(SecurityDomainDAO secDomainDao) {
		this.secDomainDao = secDomainDao;
	}

	public SecurityDomainDataServiceImpl(SecurityDomainDAO secDomainDao) {
		super();
		this.secDomainDao = secDomainDao;
	}
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.secdomain.service.SecurityDomainDataService#getSecurityDomain(java.lang.String)
	 */
	public SecurityDomain getSecurityDomain(String domainId) {
		return secDomainDao.findById(domainId);
	}

/* (non-Javadoc)
 * @see org.openiam.idm.srvc.secdomain.service.SecurityDomainDataService#addSecurityDomain(org.openiam.idm.srvc.secdomain.dto.SecurityDomain)
 */

  public void addSecurityDomain(SecurityDomain secDom)  {
	  if (secDom == null)
		   throw new NullPointerException("SecurityDomain object is null");
	  
	  secDomainDao.add(secDom);
  }
  
  /* (non-Javadoc)
 * @see org.openiam.idm.srvc.secdomain.service.SecurityDomainDataService#updateSecurityDomain(org.openiam.idm.srvc.secdomain.dto.SecurityDomain)
 */
  public void updateSecurityDomain(SecurityDomain secDom) {
	  if (secDom == null)
		   throw new NullPointerException("SecurityDomain object is null");
	  if (secDom.getDomainId() == null)
		  throw new NullPointerException("DomainId is null");
	
	  secDomainDao.update(secDom);

  }

  /* (non-Javadoc)
 * @see org.openiam.idm.srvc.secdomain.service.SecurityDomainDataService#removeSecurityDomain(java.lang.String)
 */
  public void removeSecurityDomainById(String id) {
	  if (id == null)
		  throw new NullPointerException("Service id is null");

	  final SecurityDomain secDom = new SecurityDomain(id);
	  secDomainDao.remove(secDom);
  }

  /* (non-Javadoc)
 * @see org.openiam.idm.srvc.secdomain.service.SecurityDomainDataService#removeSecurityDomain(org.openiam.idm.srvc.secdomain.dto.SecurityDomain)
 */
  public void removeSecurityDomain(SecurityDomain secDom) {
	  if (secDom == null)
		   throw new NullPointerException("SecurityDomain object is null");
	  if (secDom.getDomainId() == null)
		  throw new NullPointerException("DomainId is null");
	
	  secDomainDao.remove(secDom);
  }
  
  public List<SecurityDomain> getAllSecurityDomains() {
	  return secDomainDao.findAll();
  }
  
  public List<SecurityDomain> getAllDomainsWithExclude(String excludeDomain) {
	  final List<SecurityDomain> domainList = secDomainDao.findAll();
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
