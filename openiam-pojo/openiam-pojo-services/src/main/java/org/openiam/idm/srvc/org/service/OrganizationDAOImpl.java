package org.openiam.idm.srvc.org.service;


import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.searchbean.converter.OrganizationSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static org.hibernate.criterion.Projections.rowCount;

/**
 * Data access object implementation for OrganizationEntity.
 */
@Repository("organizationDAO")
public class OrganizationDAOImpl extends BaseDaoImpl<OrganizationEntity, String> implements OrganizationDAO {
    @Autowired
    private OrganizationSearchBeanConverter organizationSearchBeanConverter;

    public List<OrganizationEntity> findRootOrganizations() {
        final Criteria criteria = getCriteria()
                .add(Restrictions.isNull("parentId"))
                .addOrder(Order.asc("organizationName"));
        	  //.setFetchMode("attributes", FetchMode.JOIN);
        return criteria.list();
    }

    public List<OrganizationEntity> findAllOrganization() {
        Criteria criteria = getCriteria()
                .addOrder(Order.asc("organizationName"));
                //.setFetchMode("attributes", FetchMode.JOIN);
        return criteria.list();
    }


    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if(searchBean != null && searchBean instanceof OrganizationSearchBean) {
            final OrganizationSearchBean organizationSearchBean = (OrganizationSearchBean)searchBean;

            final OrganizationEntity exampleEnity = organizationSearchBeanConverter.convert(organizationSearchBean);
            exampleEnity.setId(null);
            criteria = this.getExampleCriteria(exampleEnity);

            if(organizationSearchBean.hasMultipleKeys()) {
                criteria.add(Restrictions.in(getPKfieldName(), organizationSearchBean.getKeys()));
            } else if(StringUtils.isNotBlank(organizationSearchBean.getKey())) {
                criteria.add(Restrictions.eq(getPKfieldName(), organizationSearchBean.getKey()));
            }
        }
        return criteria;
    }

    @Override
    protected Criteria getExampleCriteria(final OrganizationEntity organization) {
        final Criteria criteria = getCriteria();
        if(StringUtils.isNotBlank(organization.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), organization.getId()));
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
        criteria.addOrder(Order.asc("organizationName"));
        return criteria;
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }
    

	@Override
	public List<OrganizationEntity> getChildOrganizations(String orgId, Set<String> filter, final int from, final int size) {
		return getList(getChildOrganizationsCriteria(orgId, filter), from, size);
	}
    @Override
    public List<OrganizationEntity> getParentOrganizations(String orgId, Set<String> filter, final int from, final int size) {
        return getList(getParentOrganizationsCriteria(orgId, filter), from, size);
    }

	
	@Override
	public int getNumOfParentOrganizations(String orgId, Set<String> filter) {
		final Criteria criteria = getParentOrganizationsCriteria(orgId, filter).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}
    @Override
    public int getNumOfChildOrganizations(String orgId, Set<String> filter) {
        final Criteria criteria = getChildOrganizationsCriteria(orgId, filter).setProjection(rowCount());
        return ((Number)criteria.uniqueResult()).intValue();
    }



    private List<OrganizationEntity> getList(Criteria criteria, final int from, final int size){
        if(from > -1) {
            criteria.setFirstResult(from);
        }

        if(size > -1) {
            criteria.setMaxResults(size);
        }
        criteria.addOrder(Order.asc("organizationName"));
        return criteria.list();
    }

    private Criteria getParentOrganizationsCriteria(String orgId, Set<String> filter) {
        Criteria criteria =  getCriteria().createAlias("childOrganizations", "organization").add( Restrictions.eq("organization.id", orgId));
        if(filter!=null && !filter.isEmpty()){
            criteria.add(Restrictions.in(getPKfieldName(), filter));
        }
        return  criteria;
    }

    private Criteria getChildOrganizationsCriteria(String orgId, Set<String> filter) {
        Criteria criteria =  getCriteria().createAlias("parentOrganizations", "organization").add( Restrictions.eq("organization.id", orgId));
        if(filter!=null && !filter.isEmpty()){
            criteria.add(Restrictions.in(getPKfieldName(), filter));
        }
        return  criteria;
    }
}
