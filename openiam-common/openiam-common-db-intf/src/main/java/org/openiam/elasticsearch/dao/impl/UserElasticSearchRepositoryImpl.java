package org.openiam.elasticsearch.dao.impl;

import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.UserElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class UserElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<UserEntity, String, UserSearchBean> implements UserElasticSearchRepositoryCustom {

	@Override
	protected CriteriaQuery getCriteria(final UserSearchBean searchBean) {
		CriteriaQuery query = null;
		if(searchBean != null) {
			SearchParam param = null;
			
			param = searchBean.getFirstNameMatchToken();
			if(param != null && param.isValid()) {
				final Criteria criteria = getWhereCriteria("firstName", param.getValue(), param.getMatchType());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			param = searchBean.getLastNameMatchToken();
			if(param != null && param.isValid()) {
				final Criteria criteria = getWhereCriteria("lastName", param.getValue(), param.getMatchType());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			param = searchBean.getMaidenNameMatchToken();
			if(param != null && param.isValid()) {
				final Criteria criteria = getWhereCriteria("maidenName", param.getValue(), param.getMatchType());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			param = searchBean.getEmployeeIdMatchToken();
			if(param != null && param.isValid()) {
				final Criteria criteria = getWhereCriteria("employeeId", param.getValue(), param.getMatchType());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			Criteria criteria = exactCriteria("status", searchBean.getUserStatus());
			if(criteria != null) {
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
			
			criteria = exactCriteria("secondaryStatus", searchBean.getAccountStatus());
			if(criteria != null) {
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
			
			criteria = exactCriteria("jobCode", searchBean.getJobCode());
			if(criteria != null) {
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
			
			criteria = exactCriteria("employeeType", searchBean.getEmployeeType());
			if(criteria != null) {
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
			
			criteria = exactCriteria("type", searchBean.getUserType());
			if(criteria != null) {
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
		}
		return query;
	}

	@Override
	protected Class<UserEntity> getEntityClass() {
		return UserEntity.class;
	}

}
