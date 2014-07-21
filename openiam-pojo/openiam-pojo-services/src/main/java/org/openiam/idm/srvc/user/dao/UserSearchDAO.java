package org.openiam.idm.srvc.user.dao;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.openiam.base.ws.SearchParam;
import org.openiam.core.dao.lucene.AbstractHibernateSearchDao;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Repository;

@Repository("userSearchDAO")
public class UserSearchDAO extends AbstractHibernateSearchDao<UserEntity, UserSearchBean, String> {
	
	@Override
	protected Query parse(UserSearchBean query) {
		final BooleanQuery luceneQuery = new BooleanQuery();
		SearchParam param = null;
		
		param = query.getFirstNameMatchToken();
		if(param != null && param.isValid()) {
			Query clause = null;
			switch(param.getMatchType()) {
				case EXACT:
					clause = buildExactClause("firstNameUntokenized", param.getValue());
					break;
				case STARTS_WITH:
					clause = buildTokenizedClause("firstName", param.getValue());
					break;
				default:
					break;
			}
			
			if(clause != null) {
				luceneQuery.add(clause, BooleanClause.Occur.MUST);
			}
		}

		param = query.getLastNameMatchToken();
		if(param != null && param.isValid()) {
			Query clause = null;
			switch(param.getMatchType()) {
				case EXACT:
					clause = buildExactClause("lastNameUntokenized", param.getValue());
					break;
				case STARTS_WITH:
					clause = buildTokenizedClause("lastName", param.getValue());
					break;
				default:
					break;
			}
			
			if(clause != null) {
				luceneQuery.add(clause, BooleanClause.Occur.MUST);
			}
		}
		
		param = query.getMaidenNameMatchToken();
		if(param != null && param.isValid()) {
			Query clause = null;
			switch(param.getMatchType()) {
				case EXACT:
					clause = buildExactClause("maidenNameUntokenized", param.getValue());
					break;
				case STARTS_WITH:
					clause = buildTokenizedClause("maidenName", param.getValue());
					break;
				default:
					break;
			}
			
			if(clause != null) {
				luceneQuery.add(clause, BooleanClause.Occur.MUST);
			}
		}
		
		param = query.getEmployeeIdMatchToken();
		if(param != null && param.isValid()) {
			Query clause = null;
			switch(param.getMatchType()) {
				case EXACT:
					clause = buildExactClause("employeeIdUntokenized", param.getValue());
					break;
				case STARTS_WITH:
					clause = buildTokenizedClause("employeeId", param.getValue());
					break;
				default:
					break;
			}
			
			if(clause != null) {
				luceneQuery.add(clause, BooleanClause.Occur.MUST);
			}
		}

		Query clause = buildExactClause("userStatus", query.getUserStatus());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}

		clause = buildExactClause("accountStatus", query.getAccountStatus());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
        
        clause = buildExactClause("jobCode.id", query.getJobCode());
        if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}

        clause = buildExactClause("employeeType.id", query.getEmployeeType());
        if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		return luceneQuery;
	}

	@Override
	protected Class<UserEntity> getEntityClass() {
		return UserEntity.class;
	}
}