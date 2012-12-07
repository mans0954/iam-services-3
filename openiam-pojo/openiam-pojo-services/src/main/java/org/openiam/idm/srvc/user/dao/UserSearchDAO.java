package org.openiam.idm.srvc.user.dao;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.openiam.core.dao.lucene.AbstractHibernateSearchDao;
import org.openiam.core.dao.lucene.SortType;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Repository;

@Repository("userSearchDAO")
public class UserSearchDAO extends AbstractHibernateSearchDao<UserEntity, UserSearchBean, String> {
	
	@Override
	protected Query parse(UserSearchBean query) {
		final BooleanQuery luceneQuery = new BooleanQuery();
		Query clause = buildClause("firstName", query.getFirstName());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildClause("lastName", query.getLastName());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildClause("userStatus", query.getStatus());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildClause("accountStatus", query.getSecondaryStatus());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildClause("email", query.getEmailAddress());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildClause("areaCd", query.getPhoneAreaCd());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildClause("phoneNbr", query.getPhoneNbr());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		/*
		clause = buildOrganizationClause(query.getOrgIdList());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		*/
		
		return luceneQuery;
	}
	
	private Query buildOrganizationClause(final List<String> organizationIdList) {
        if (CollectionUtils.isNotEmpty(organizationIdList)) {
        	/*
        	return ActiveType.getByCode(service).equals(ActiveType.ALL)
        		? new RegexQuery(new Term("service", "(.*?)"))
        		: QueryBuilder.buildQuery("service", BooleanClause.Occur.MUST, service);
        	*/
        }
        return null;
    }

	@Override
	protected Class<UserEntity> getEntityClass() {
		return UserEntity.class;
	}
}