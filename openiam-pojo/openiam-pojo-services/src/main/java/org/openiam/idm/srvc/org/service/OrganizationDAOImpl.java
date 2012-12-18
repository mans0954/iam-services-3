package org.openiam.idm.srvc.org.service;


import static org.hibernate.criterion.Projections.rowCount;

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

    public List<OrganizationEntity> findRootOrganizations() {
        final Criteria criteria = getCriteria()
                .add(Restrictions.isNull("parentId"))
                .addOrder(Order.asc("organizationName"))
                .setFetchMode("attributes", FetchMode.JOIN);
        return criteria.list();
    }

    public List<OrganizationEntity> findAllOrganization() {
        Criteria criteria = getCriteria()
                .addOrder(Order.asc("organizationName"))
                .setFetchMode("attributes", FetchMode.JOIN);
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
    
    private Criteria getChildOrganizationsCriteria(final String organizationId) {
		return getCriteria().createAlias("parentOrganizations", "organization").add( Restrictions.eq("organization.orgId", organizationId));
	}
    
	@Override
	public int getNumOfChildOrganizations(String organizationId) {
		final Criteria criteria = getChildOrganizationsCriteria(organizationId).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public List<OrganizationEntity> getChildOrganizations(final String organizationId, final int from, final int size) {
		final Criteria criteria = getChildOrganizationsCriteria(organizationId);
		
		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}
	
    private Criteria getParentOrganizationsCriteria(final String organizationId) {
    	return getCriteria().createAlias("childOrganizations", "organization").add( Restrictions.eq("organization.orgId", organizationId));
	}
	
	@Override
	public int getNumOfParentOrganizations(String organizationId) {
		final Criteria criteria = getParentOrganizationsCriteria(organizationId).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public List<OrganizationEntity> getParentOrganizations(final String organizationId, final int from, final int size) {
		final Criteria criteria = getParentOrganizationsCriteria(organizationId);
		
		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}
}
