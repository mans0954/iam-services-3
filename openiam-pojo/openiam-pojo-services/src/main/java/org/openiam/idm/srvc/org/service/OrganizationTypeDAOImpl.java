package org.openiam.idm.srvc.org.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.OrderDaoImpl;
import org.openiam.dozer.converter.OrgType2OrgTypeXrefConverter;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.org.domain.OrgType2OrgTypeXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationTypeDAOImpl extends OrderDaoImpl<OrganizationTypeEntity, String> implements OrganizationTypeDAO {
    @Autowired
    private OrgType2OrgTypeXrefConverter orgType2OrgTypeXrefConverter;
	
	 @Override
	 protected Criteria getExampleCriteria(final SearchBean searchBean) {
		 Criteria criteria = getCriteria();
		 if(searchBean instanceof OrganizationTypeSearchBean) {
			 final OrganizationTypeSearchBean sb = (OrganizationTypeSearchBean)searchBean;
			 if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
				 criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
			 } else {
				 
				 final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
                if(nameCriterion != null) {
                	criteria.add(nameCriterion);
                }
			 
				 if(CollectionUtils.isNotEmpty(sb.getChildIds())) {
					 criteria.createAlias("childTypes", "child").add(Restrictions.in("child.id", sb.getChildIds()));
				 }
			 
				 if(CollectionUtils.isNotEmpty(sb.getParentIds())) {
					 criteria.createAlias("parentTypes", "parent").add(Restrictions.in("parent.id", sb.getParentIds()));
				 }
			 }

             if(CollectionUtils.isNotEmpty(sb.getExcludeIds())) {
                 criteria.add(Restrictions.not(Restrictions.in("id", sb.getExcludeIds())));
             }
		 }
		 return criteria;
	 }

	 @Override
	 protected String getPKfieldName() {
		 return "id";
	 }
    
	 @Override
	 public List<OrgType2OrgTypeXrefEntity> getOrgTypeToOrgTypeXrefList(){
		 List<OrgType2OrgTypeXrefEntity>  orgTypeXrefEntities = this.getSession().createCriteria(OrgType2OrgTypeXrefEntity.class).list();
		 return orgTypeXrefEntities;
	 }

	 @Override
	 public List<String> findAllIds(){
		 Criteria criteria = getCriteria();
		 criteria.setProjection(Projections.property(getPKfieldName()));
		 return criteria.list();
	 }

	protected String getReferenceType() {
		return "OrganizationTypeEntity.displayNameMap";
	}

}
