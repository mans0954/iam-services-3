package org.openiam.idm.srvc.auth.login.lucene;

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
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("loginSearchDAO")
public class LoginSearchDAO extends AbstractHibernateSearchDao<LoginEntity, LoginSearchBean, String> {

	@Override
	protected QueryBuilder parse(LoginSearchBean query) {
        BoolQueryBuilder luceneQuery = QueryBuilders.boolQuery();
		final SearchParam param = query.getLoginMatchToken();
		if(param != null && param.isValid()) {
            QueryBuilder clause = null;
            if(MatchType.EXACT.equals(param.getMatchType())){
                clause = buildExactClause("login", param.getValue());
            } else {
                clause = buildTokenizedClause("login", param.getValue(), param.getMatchType());
            }

			if(clause != null) {
                addClause(luceneQuery, clause, SearchMode.AND);
			}
		}

        QueryBuilder clause = buildExactClause("managedSysId", query.getManagedSysId());
		if(clause != null) {
            addClause(luceneQuery, clause, SearchMode.AND);
//			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("userId", query.getUserId());
		if(clause != null) {
            addClause(luceneQuery, clause, SearchMode.AND);
//			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		return luceneQuery;
	}

	@Override
	protected Class<LoginEntity> getEntityClass() {
		return LoginEntity.class;
	}

	public List<String> findUserIds(final int from, final int size, final LoginSearchBean query) {
		final List<String> result = new ArrayList<String>();
    	if ((query != null)) {
            final QueryBuilder luceneQuery = parse(query);
            if (luceneQuery != null) {
//				final List idList = findIds(buildFullTextSessionQuery(getFullTextSession(null), luceneQuery, from, size, null).setProjection("userId"));
//				for (final Object row : idList) {
//					final Object[] columns = (Object[]) row;
//					final String id = (String) columns[0];
//					result.add(id);
//				}
            }
    	}
        return result;
	}
}
