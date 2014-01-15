package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.springframework.stereotype.Component;

/**
 * Created by: Alexander Duckardt
 * Date: 02.11.12
 */
@Component("organizationSearchBeanConverter")
public class OrganizationSearchBeanConverter implements SearchBeanConverter<OrganizationEntity, OrganizationSearchBean>{
    @Override
    public OrganizationEntity convert(OrganizationSearchBean searchBean) {
        final OrganizationEntity organization = new OrganizationEntity();
        organization.setId(StringUtils.trimToNull(searchBean.getKey()));
        organization.setName(StringUtils.trimToNull(searchBean.getName()));
        if(StringUtils.isNotBlank(searchBean.getOrganizationTypeId())) {
        	final OrganizationTypeEntity typeEntity = new OrganizationTypeEntity();
        	typeEntity.setId(StringUtils.trimToNull(searchBean.getOrganizationTypeId()));
        	organization.setOrganizationType(typeEntity);
        }
        return organization;
    }
}
