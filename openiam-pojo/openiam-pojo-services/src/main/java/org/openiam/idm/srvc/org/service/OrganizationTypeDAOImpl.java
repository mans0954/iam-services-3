package org.openiam.idm.srvc.org.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.org.dto.OrgType2OrgTypeXref;
import org.openiam.idm.srvc.searchbean.converter.OrganizationTypeSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrganizationTypeDAOImpl extends BaseDaoImpl<OrganizationTypeEntity, String> implements OrganizationTypeDAO {

	@Autowired
	private OrganizationTypeSearchBeanConverter converter;
	
	 @Override
	 protected Criteria getExampleCriteria(final SearchBean searchBean) {
		 Criteria criteria = getCriteria();
		 if(searchBean instanceof OrganizationTypeSearchBean) {
			 final OrganizationTypeSearchBean typeSearchBean = (OrganizationTypeSearchBean)searchBean;
			 if(CollectionUtils.isNotEmpty(typeSearchBean.getKeySet())) {
				 criteria.add(Restrictions.in(getPKfieldName(), typeSearchBean.getKeySet()));
			 } else {
				 final OrganizationTypeEntity entity = converter.convert(typeSearchBean);
				 criteria = getExampleCriteria(entity);
			 
				 if(CollectionUtils.isNotEmpty(typeSearchBean.getChildIds())) {
					 criteria.createAlias("childTypes", "child").add(Restrictions.in("child.id", typeSearchBean.getChildIds()));
				 }
			 
				 if(CollectionUtils.isNotEmpty(typeSearchBean.getParentIds())) {
					 criteria.createAlias("parentTypes", "parent").add(Restrictions.in("parent.id", typeSearchBean.getParentIds()));
				 }
			 }

             if(CollectionUtils.isNotEmpty(typeSearchBean.getExcludeIds())) {
                 criteria.add(Restrictions.not(Restrictions.in("id", typeSearchBean.getExcludeIds())));
             }
		 }
		 return criteria;
	 }
	        
	
	@Override
	protected Criteria getExampleCriteria(OrganizationTypeEntity entity) {
		final Criteria criteria = getCriteria();
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getId())) {
				criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
			} else {
				if(StringUtils.isNotBlank(entity.getName())) {
					criteria.add(Restrictions.eq("name", entity.getName()));
				}
			}
		}
		return criteria;
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}
    @Override
    public List<OrgType2OrgTypeXref> getOrgTypeToOrgTypeXrefList(){
        return this.getSession().createSQLQuery("SELECT ORG_TYPE_ID as organizationTypeId, MEMBER_ORG_TYPE_ID as memberOrganizationTypeId FROM ORG_TYPE_VALID_MEMBERSHIP")
                .addScalar("organizationTypeId").addScalar("memberOrganizationTypeId")
                .setResultTransformer(Transformers.aliasToBean(OrgType2OrgTypeXref.class)).list();
    }

    @Override
    public List<String> findAllIds(){
        Criteria criteria = getCriteria();
        criteria.setProjection(Projections.property(getPKfieldName()));
        return criteria.list();
    }

}
