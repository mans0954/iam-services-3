package org.openiam.idm.srvc.searchbean.converter;

import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.springframework.stereotype.Component;

/**
 * Created by: Alexander Duckardt
 * Date: 02.11.12
 */
@Component("organizationSearchBeanConverter")
public class OrganizationSearchBeanConverter implements SearchBeanConverter<Organization, OrganizationSearchBean>{
    @Override
    public Organization convert(OrganizationSearchBean searchBean) {
        final Organization organization = new Organization();
        organization.setOrgId(searchBean.getKey());
        organization.setOrganizationName(searchBean.getOrganizationName());

        return organization;
    }
}
