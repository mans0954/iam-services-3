package org.openiam.idm.srvc.auth.login.lucene;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
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
                SearchResponse searchResponse = esHelper.searchData(luceneQuery, getEntityClass());
                if(searchResponse!=null && searchResponse.getHits()!=null && searchResponse.getHits().getTotalHits()>0){
                    for (final SearchHit hit : searchResponse.getHits()) {
                        final String fieldValue = (String) hit.getSource().get("userId");
                        if(StringUtils.isNotBlank(fieldValue))
                            result.add(fieldValue);
                    }
                }
            }
    	}
        return result;
	}
}
