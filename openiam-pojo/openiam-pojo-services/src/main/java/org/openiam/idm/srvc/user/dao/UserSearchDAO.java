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
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.idm.searchbeans.LoginSearchBean;
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
        
        clause = buildExactClause("jobCode", query.getJobCode());
        if(clause != null) {
            addClause(luceneQuery, clause, query.getSearchMode());
//			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}

        clause = buildExactClause("employeeType", query.getEmployeeType());
        if(clause != null) {
            addClause(luceneQuery, clause, query.getSearchMode());
//			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}

        clause = buildExactClause("type", query.getUserType());
        if(clause != null) {
            addClause(luceneQuery, clause, query.getSearchMode());
//            luceneQuery.add(clause, BooleanClause.Occur.MUST);
        }

//        param = query.getEmailAddressMatchToken();
//        if(param != null && param.isValid()) {
//
//            // TODO: hasChildren
//            if(MatchType.EXACT.equals(param.getMatchType())){
//                clause = QueryBuilders.hasChildQuery(ESIndexType.EMAIL, buildExactClause("emailAddress", param.getValue()));
////                clause = buildExactClause("emailAddress", param.getValue());
//            } else {
//                clause = QueryBuilders.hasChildQuery(ESIndexType.EMAIL, buildTokenizedClause("emailAddress", param.getValue(), param.getMatchType()));
////                clause = buildTokenizedClause("emailAddress", param.getValue(), param.getMatchType());
//            }
//
//            if(clause != null) {
//                addClause(luceneQuery, clause, query.getSearchMode());
//            }
//        }


//        LoginSearchBean loginQuery = query.getPrincipal();
//        if(loginQuery!=null){
//            BoolQueryBuilder loginQueryClause = QueryBuilders.boolQuery();
//
//            param = loginQuery.getLoginMatchToken();
//            if(param != null && param.isValid()) {
//                clause = null;
//                if(MatchType.EXACT.equals(param.getMatchType())){
////                    clause = QueryBuilders.hasChildQuery(ESIndexType.LOGIN, buildExactClause("login", param.getValue()));
//                    clause = buildExactClause("login", param.getValue());
//                } else {
//                    clause = QueryBuilders.hasChildQuery(ESIndexType.LOGIN, buildTokenizedClause("login", param.getValue(), param.getMatchType()));
//                    clause = buildTokenizedClause("login", param.getValue(), param.getMatchType());
//                }
//
//                if(clause != null) {
//                    addClause(loginQueryClause, clause, SearchMode.AND);
//                }
//            }
//
//            clause = buildExactClause("managedSysId", loginQuery.getManagedSysId());
//            if(clause != null) {
//                addClause(loginQueryClause, clause, SearchMode.AND);
//            }
//
//            clause = buildExactClause("userId", query.getUserId());
//            if(clause != null) {
//                addClause(loginQueryClause, clause, SearchMode.AND);
//            }
//
//            clause = QueryBuilders.hasChildQuery(ESIndexType.LOGIN, loginQueryClause);
//
//            if(clause != null) {
//                addClause(luceneQuery, clause, query.getSearchMode());
//            }
//        }

//        query.getPhoneAreaCd();
//        query.getPhoneNbr();
//
//
//        QueryBuilder clause = buildTokenizedClause("areaCd", query.getPhoneAreaCd(), MatchType.STARTS_WITH);
//        if(clause != null) {
//            addClause(luceneQuery, clause, SearchMode.AND);
////			luceneQuery.add(clause, BooleanClause.Occur.MUST);
//        }
//
//        clause = buildTokenizedClause("phoneNbr", query.getPhoneNbr(), MatchType.STARTS_WITH);
//        if(clause != null) {
//            addClause(luceneQuery, clause, SearchMode.AND);
////			luceneQuery.add(clause, BooleanClause.Occur.MUST);
//        }

		return luceneQuery;
	}



    @Override
	protected Class<UserEntity> getEntityClass() {
		return UserEntity.class;
	}
}