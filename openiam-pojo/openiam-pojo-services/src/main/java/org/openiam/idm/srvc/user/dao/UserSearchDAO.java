package org.openiam.idm.srvc.user.dao;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchMode;
import org.openiam.base.ws.SearchParam;
import org.openiam.core.dao.lucene.AbstractHibernateSearchDao;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Repository;

@Repository("userSearchDAO")
public class UserSearchDAO extends AbstractHibernateSearchDao<UserEntity, UserSearchBean, String> {
	
	@Override
	protected QueryBuilder parse(UserSearchBean query) {
        BoolQueryBuilder luceneQuery = QueryBuilders.boolQuery();

		SearchParam param = null;
		
		param = query.getFirstNameMatchToken();
		if(param != null && param.isValid()) {
            QueryBuilder clause = null;
            if(MatchType.EXACT.equals(param.getMatchType())){
                clause = buildExactClause("firstName", param.getValue());
            } else {
                clause = buildTokenizedClause("firstName", param.getValue(), param.getMatchType());
            }

			if(clause != null) {
                addClause(luceneQuery, clause, query.getSearchMode());
			}
		}

		param = query.getLastNameMatchToken();
		if(param != null && param.isValid()) {
            QueryBuilder clause = null;
            if(MatchType.EXACT.equals(param.getMatchType())){
                clause = buildExactClause("lastName", param.getValue());
            } else {
                clause = buildTokenizedClause("lastName", param.getValue(), param.getMatchType());
            }

			if(clause != null) {
                addClause(luceneQuery, clause, query.getSearchMode());
//				luceneQuery.add(clause, BooleanClause.Occur.MUST);
			}
		}
		
		param = query.getMaidenNameMatchToken();
		if(param != null && param.isValid()) {
            QueryBuilder clause = null;
            if(MatchType.EXACT.equals(param.getMatchType())){
                clause = buildExactClause("maidenName", param.getValue());
            } else {
                clause = buildTokenizedClause("maidenName", param.getValue(), param.getMatchType());
            }

			if(clause != null) {
                addClause(luceneQuery, clause, query.getSearchMode());
//				luceneQuery.add(clause, BooleanClause.Occur.MUST);
			}
		}
		
		param = query.getEmployeeIdMatchToken();
		if(param != null && param.isValid()) {
            QueryBuilder clause = null;
            if(MatchType.EXACT.equals(param.getMatchType())){
                clause = buildExactClause("employeeId", param.getValue());
            } else {
                clause = buildTokenizedClause("employeeId", param.getValue(), param.getMatchType());
            }


			if(clause != null) {
                addClause(luceneQuery, clause, query.getSearchMode());
//				luceneQuery.add(clause, BooleanClause.Occur.MUST);
			}
		}

        QueryBuilder clause = buildExactClause("userStatus", query.getUserStatus());
		if(clause != null) {
            addClause(luceneQuery, clause, query.getSearchMode());
//			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}

		clause = buildExactClause("accountStatus", query.getAccountStatus());
		if(clause != null) {
            addClause(luceneQuery, clause, query.getSearchMode());
//			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
        
        clause = buildExactClause("jobCode.id", query.getJobCode());
        if(clause != null) {
            addClause(luceneQuery, clause, query.getSearchMode());
//			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}

        clause = buildExactClause("employeeType.id", query.getEmployeeType());
        if(clause != null) {
            addClause(luceneQuery, clause, query.getSearchMode());
//			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}

        clause = buildExactClause("type.id", query.getUserType());
        if(clause != null) {
            addClause(luceneQuery, clause, query.getSearchMode());
//            luceneQuery.add(clause, BooleanClause.Occur.MUST);
        }

		return luceneQuery;
	}



    @Override
	protected Class<UserEntity> getEntityClass() {
		return UserEntity.class;
	}
}