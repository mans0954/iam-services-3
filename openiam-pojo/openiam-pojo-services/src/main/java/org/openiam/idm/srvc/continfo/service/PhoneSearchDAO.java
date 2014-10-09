package org.openiam.idm.srvc.continfo.service;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchMode;
import org.openiam.core.dao.lucene.AbstractHibernateSearchDao;
import org.openiam.idm.searchbeans.PhoneSearchBean;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("phoneSearchDAO")
public class PhoneSearchDAO extends AbstractHibernateSearchDao<PhoneEntity, PhoneSearchBean, String> {

	@Override
	protected QueryBuilder parse(final PhoneSearchBean query) {
		final BoolQueryBuilder luceneQuery = QueryBuilders.boolQuery();

        QueryBuilder clause = buildTokenizedClause("areaCd", query.getPhoneAreaCd(), MatchType.STARTS_WITH);
		if(clause != null) {
            addClause(luceneQuery, clause, SearchMode.AND);
//			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildTokenizedClause("phoneNbr", query.getPhoneNbr(), MatchType.STARTS_WITH);
		if(clause != null) {
            addClause(luceneQuery, clause, SearchMode.AND);
//			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		return luceneQuery;
	}

	@Override
	protected Class<PhoneEntity> getEntityClass() {
		return PhoneEntity.class;
	}

	public List<String> findUserIds(final int from, final int size, final PhoneSearchBean query) {
		final List<String> result = new ArrayList<String>();
    	if ((query != null)) {
            final QueryBuilder luceneQuery = parse(query);
            if (luceneQuery != null) {
				/*final List idList = findIds(buildFullTextSessionQuery(getFullTextSession(null), luceneQuery, from, size, null).setProjection("parent"));
				for (final Object row : idList) {
					final Object[] columns = (Object[]) row;
					final UserEntity id = (UserEntity) columns[0];
					result.add(id.getId());
				}*/
            }
    	}
        return result;
	}
}
