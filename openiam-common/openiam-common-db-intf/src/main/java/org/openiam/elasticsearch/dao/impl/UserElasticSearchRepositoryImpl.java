package org.openiam.elasticsearch.dao.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.base.OrderConstants;
import org.openiam.base.ws.SearchMode;
import org.openiam.base.ws.SearchParam;
import org.openiam.base.ws.SortParam;
import org.openiam.elasticsearch.dao.UserElasticSearchRepository;
import org.openiam.elasticsearch.dao.UserElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class UserElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<UserEntity, String, UserSearchBean> implements UserElasticSearchRepositoryCustom {
	
	private Sort getSorting(final UserSearchBean searchBean) {
		Sort finalSort = null;
		if(searchBean != null && CollectionUtils.isNotEmpty(searchBean.getSortBy())) {
	        for (SortParam sort: searchBean.getSortBy()){
	            final OrderConstants orderDir = (sort.getOrderBy() == null)?OrderConstants.ASC:sort.getOrderBy();
	            final Direction direction = OrderConstants.ASC.equals(orderDir) ? Direction.ASC : Direction.DESC;
	
	            if("name".equals(sort.getSortBy())){
	            	final Sort firstNameSort = new Sort(direction, "firstName");
	            	final Sort lastNameSort = new Sort(direction, "lastName");
	            	finalSort = (finalSort != null) ? finalSort.and(firstNameSort) : firstNameSort;
	            	finalSort = (finalSort != null) ? finalSort.and(lastNameSort) : lastNameSort;
	            }/* else if("phone".equals(sort.getSortBy())){
	                criteria.createAlias("phones", "p", Criteria.LEFT_JOIN, Restrictions.eq("p.isDefault", true));
	                criteria.addOrder(createOrder("p.countryCd", orderDir));
	                criteria.addOrder(createOrder("p.areaCd", orderDir));
	                criteria.addOrder(createOrder("p.phoneNbr", orderDir));
	                criteria.addOrder(createOrder("p.phoneExt", orderDir));
	            } else if("email".equals(sort.getSortBy())){
	                criteria.createAlias("emailAddresses", "ea", Criteria.LEFT_JOIN, Restrictions.eq("ea.isDefault", true));
	                criteria.addOrder(createOrder("ea.emailAddress", orderDir));
	            }else if("userStatus".equals(sort.getSortBy())){
	                criteria.addOrder(createOrder("status",orderDir));
	            }else if("accountStatus".equals(sort.getSortBy())){
	                criteria.addOrder(createOrder("secondaryStatus",orderDir));
	            }else if("principal".equals(sort.getSortBy())){
	                criteria.createAlias("principalList", "l", Criteria.LEFT_JOIN, Restrictions.eq("l.managedSysId", sysConfiguration.getDefaultManagedSysId()));
	                criteria.addOrder(createOrder("l.login", orderDir));
	            }else if("organization".equals(sort.getSortBy())){
	                criteria.createAlias("affiliations", "org", Criteria.LEFT_JOIN).add(
	                        Restrictions.or(Restrictions.isNull("org.organizationType.id"), Restrictions.eq("org.organizationType.id", propertyValueSweeper.getString("org.openiam.organization.type.id"))));
	                criteria.addOrder(createOrder("org.name", orderDir));
	            }else if("department".equals(sort.getSortBy())) {
	                criteria.createAlias("affiliations", "dep", Criteria.LEFT_JOIN).add(
	                        Restrictions.or(Restrictions.isNull("dep.organizationType.id"), Restrictions.eq("dep.organizationType.id", propertyValueSweeper.getString("org.openiam.department.type.id"))));
	                criteria.addOrder(createOrder("dep.name", orderDir));
	            } else {
	                criteria.addOrder(createOrder(sort.getSortBy(),orderDir));
	            }*/
	        }
		}
		return finalSort;
    }
	
	@Override
	protected CriteriaQuery getCriteria(final UserSearchBean searchBean) {
		final SearchMode mode = (searchBean.getSearchMode() != null) ? searchBean.getSearchMode() : SearchMode.AND;
		
		CriteriaQuery query = null;
		if(searchBean != null) {
			SearchParam param = null;
			
			param = searchBean.getFirstNameMatchToken();
			if(param != null && param.isValid()) {
				final Criteria criteria = getWhereCriteria("firstName", param.getValue(), param.getMatchType());
				if(criteria != null) {
					query = new CriteriaQuery(criteria);
				}
			}
			
			param = searchBean.getLastNameMatchToken();
			if(param != null && param.isValid()) {
				final Criteria criteria = getWhereCriteria("lastName", param.getValue(), param.getMatchType());
				if(criteria != null) {
					//query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
					if(query == null) {
						query = new CriteriaQuery(criteria);
					} else {
						if(SearchMode.AND.equals(mode)) {
							query.addCriteria(criteria);
						} else {
							query = new CriteriaQuery(query.getCriteria().or(criteria));
						}
					}
				}
			}
			
			param = searchBean.getMaidenNameMatchToken();
			if(param != null && param.isValid()) {
				final Criteria criteria = getWhereCriteria("maidenName", param.getValue(), param.getMatchType());
				if(criteria != null) {
					//query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
					if(query == null) {
						query = new CriteriaQuery(criteria);
					} else {
						if(SearchMode.AND.equals(mode)) {
							query.addCriteria(criteria);
						} else {
							query = new CriteriaQuery(query.getCriteria().or(criteria));
						}
					}
				}
			}
			
			param = searchBean.getEmployeeIdMatchToken();
			if(param != null && param.isValid()) {
				final Criteria criteria = getWhereCriteria("employeeId", param.getValue(), param.getMatchType());
				if(criteria != null) {
					//query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
					if(query == null) {
						query = new CriteriaQuery(criteria);
					} else {
						if(SearchMode.AND.equals(mode)) {
							query.addCriteria(criteria);
						} else {
							query = new CriteriaQuery(query.getCriteria().or(criteria));
						}
					}
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
