package org.openiam.idm.srvc.org.service;


import java.util.List;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;

import org.hibernate.criterion.*;

import org.openiam.idm.srvc.org.dto.*;
import org.springframework.stereotype.Repository;

/**
 * Data access object implementation for OrganizationEntity.
 */
@Repository("organizationDAO")
public class OrganizationDAOImpl extends BaseDaoImpl<OrganizationEntity, String> implements OrganizationDAO {

    public List<OrganizationEntity> findChildOrganization(String orgId) {
        final Criteria criteria = getCriteria()
                .add(Restrictions.eq("parentId", orgId))
                .addOrder(Order.asc("organizationName"))
                .setFetchMode("attributes", FetchMode.JOIN);
        return criteria.list();
    }

    public OrganizationEntity findParent(String orgId) {
        OrganizationEntity curOrg = findById(orgId);
        if (curOrg != null && curOrg.getParentId() != null) {
            return findById(curOrg.getParentId());
        }

        return null;
    }

    public List<OrganizationEntity> findRootOrganizations() {
        final Criteria criteria = getCriteria()
                .add(Restrictions.isNull("parentId"))
                .addOrder(Order.asc("organizationName"))
                .setFetchMode("attributes", FetchMode.JOIN);
        return criteria.list();
    }

    public List<OrganizationEntity> findOrganizationByType(String type, String parentId) {
        final Criteria criteria = getCriteria().add(Restrictions.eq("metadataTypeId",type));
        if (parentId != null) {
            criteria.add(Restrictions.eq("parentId",parentId));
        }
        criteria.addOrder(Order.asc("organizationName")) .setFetchMode("attributes", FetchMode.JOIN);
        return criteria.list();
    }

    public List<OrganizationEntity> findAllOrganization() {
        Criteria criteria = getCriteria()
                .addOrder(Order.asc("organizationName"))
                .setFetchMode("attributes", FetchMode.JOIN);
        return criteria.list();
    }

    public List<OrganizationEntity> findOrganizationByClassification(final String parentId, final OrgClassificationEnum classification) {
        final Criteria criteria = getCriteria();
        if (parentId == null) {
            criteria.add(Restrictions.eq("classification",classification));
        } else {
            criteria.add(Restrictions.eq("parentId",parentId));
        }
        criteria.addOrder(Order.asc("parentId")).addOrder(Order.asc("organizationName"));
        return criteria.list();
    }

    public List<OrganizationEntity> findOrganizationByStatus(final String parentId, final String status) {
        final Criteria criteria = getCriteria();

        if (parentId != null) {
            criteria.add(Restrictions.eq("parentId", parentId));
        }
        if (status != null) {
            criteria.add(Restrictions.eq("status", status));
        }
        criteria.add(Restrictions.eq("classification", OrgClassificationEnum.ORGANIZATION));
        return criteria.list();

    }

    @Override
    protected Criteria getExampleCriteria(final OrganizationEntity organization) {
        final Criteria criteria = getCriteria();
        if(StringUtils.isNotBlank(organization.getOrgId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), organization.getOrgId()));
        } else {
            if(StringUtils.isNotEmpty(organization.getOrganizationName())) {
                String organizationName = organization.getOrganizationName();
                MatchMode matchMode = null;
                if(StringUtils.indexOf(organizationName, "*") == 0) {
                    matchMode = MatchMode.START;
                    organizationName = organizationName.substring(1);
                }
                if(StringUtils.isNotEmpty(organizationName) && StringUtils.indexOf(organizationName, "*") == organizationName.length() - 1) {
                    organizationName = organizationName.substring(0, organizationName.length() - 1);
                    matchMode = (matchMode == MatchMode.START) ? MatchMode.ANYWHERE : MatchMode.END;
                }

                if(StringUtils.isNotEmpty(organizationName)) {
                    if(matchMode != null) {
                        criteria.add(Restrictions.ilike("organizationName", organizationName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("organizationName", organizationName));
                    }
                }
            }
            
            if (StringUtils.isNotBlank(organization.getMetadataTypeId())) {
                criteria.add(Restrictions.eq("metadataTypeId", organization.getMetadataTypeId()));
            }
            
            if (organization.getClassification() != null) {
                criteria.add(Restrictions.eq("classification", organization.getClassification()));
            }
            
            if (StringUtils.isNotBlank(organization.getInternalOrgId())) {
                criteria.add(Restrictions.eq("internalOrgId", organization.getInternalOrgId()));
            }
        }
        return criteria;
    }

    @Override
    protected String getPKfieldName() {
        return "orgId";
    }

}
