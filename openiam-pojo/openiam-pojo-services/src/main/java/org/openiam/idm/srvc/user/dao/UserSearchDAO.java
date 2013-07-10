package org.openiam.idm.srvc.user.dao;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.openiam.core.dao.lucene.AbstractHibernateSearchDao;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Repository;

@Repository("userSearchDAO")
public class UserSearchDAO extends AbstractHibernateSearchDao<UserEntity, UserSearchBean, String> {
	
	@Override
	protected Query parse(UserSearchBean query) {
		final BooleanQuery luceneQuery = new BooleanQuery();
		Query clause = buildTokenizedClause("firstName", query.getFirstName());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildTokenizedClause("lastName", query.getLastName());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("userStatus", query.getUserStatus());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("accountStatus", query.getAccountStatus());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		/*
		clause = buildTokenizedClause("email", query.getEmailAddress());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("areaCd", query.getPhoneAreaCd());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("phoneNbr", query.getPhoneNbr());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}

		clause = buildInClause("organization", query.getOrganizationIdList());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}

        clause = buildInClause("divisionEntity", query.getDivisionIdList());
        if(clause != null) {
            luceneQuery.add(clause, BooleanClause.Occur.MUST);
        }

        clause = buildInClause("department", query.getDeptIdList());
        if(clause != null) {
            luceneQuery.add(clause, BooleanClause.Occur.MUST);
        }
        */
		
		return luceneQuery;
	}

	@Override
	protected Class<UserEntity> getEntityClass() {
		return UserEntity.class;
	}
}