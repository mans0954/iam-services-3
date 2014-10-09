package org.openiam.idm.srvc.continfo.service;

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
import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("emailSearchDAO")
public class EmailSearchDAO extends AbstractHibernateSearchDao<EmailAddressEntity, EmailSearchBean, String> {

	@Override
	protected QueryBuilder parse(final EmailSearchBean query) {
		final BoolQueryBuilder luceneQuery = QueryBuilders.boolQuery();
		final SearchParam param = query.getEmailMatchToken();
		if(param != null && param.isValid()) {
            QueryBuilder clause = null;
            if(MatchType.EXACT.equals(param.getMatchType())){
                clause = buildExactClause("emailAddress", param.getValue());
            } else {
                clause = buildTokenizedClause("emailAddress", param.getValue(), param.getMatchType());
            }

			if(clause != null) {
                addClause(luceneQuery, clause, SearchMode.AND);
//				luceneQuery.add(clause, BooleanClause.Occur.MUST);
			}
		}


		return luceneQuery;
	}
	
	@Override
	protected Class<EmailAddressEntity> getEntityClass() {
		return EmailAddressEntity.class;
	}
	
	public List<String> findUserIds(final int from, final int size, final EmailSearchBean query) {
		final List<String> result = new ArrayList<String>();
    	if ((query != null)) {
            final QueryBuilder luceneQuery = parse(query);
            if (luceneQuery != null) {
//				final List idList = findIds(buildFullTextSessionQuery(getFullTextSession(null), luceneQuery, from, size, null).setProjection("parent"));
//				for (final Object row : idList) {
//					final Object[] columns = (Object[]) row;
//					final UserEntity id = (UserEntity) columns[0];
//					result.add(id.getId());
//				}
            }
    	}
        return result;
	}
}
