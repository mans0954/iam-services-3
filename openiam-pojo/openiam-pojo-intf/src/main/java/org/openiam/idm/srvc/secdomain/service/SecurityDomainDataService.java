package org.openiam.idm.srvc.secdomain.service;

import java.util.List;

import org.openiam.idm.srvc.secdomain.dto.SecurityDomain;

import javax.jws.WebService;

/**
 * Interface to manager the SecurityDomain that clients will access to gain information about SecurityDomain.
 *
 * @author Suneet Shah
 */
@WebService
public interface SecurityDomainDataService {

    /**
     * Returns an array of security domain objects in the system
     *
     * @return
     */
    public abstract List<SecurityDomain> getAllSecurityDomains();

    List<SecurityDomain> getAllDomainsWithExclude(String excludeDomain);

}