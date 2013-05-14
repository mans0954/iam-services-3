package org.openiam.idm.srvc.org.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.searchbeans.MembershipOrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;

import java.util.List;


/**
 * Data access object interface for Organization.
 *
 * @author Suneet Shah
 */

public interface OrganizationDAO extends BaseDao<OrganizationEntity, String> {

    /**
     * Returns a list of Organization objects that are root level entities; ie. they
     * don't have a parent.
     *
     * @return
     */
    List<OrganizationEntity> findRootOrganizations();

    List<OrganizationEntity> findAllOrganization();


    int getNumOfChildOrganizations(final MembershipOrganizationSearchBean searchBean);
    List<OrganizationEntity> getChildOrganizations(final MembershipOrganizationSearchBean searchBean, final int from, final int size);
    
    int getNumOfParentOrganizations(final MembershipOrganizationSearchBean searchBean);
    List<OrganizationEntity> getParentOrganizations(final MembershipOrganizationSearchBean searchBean, final int from, final int size);
}
