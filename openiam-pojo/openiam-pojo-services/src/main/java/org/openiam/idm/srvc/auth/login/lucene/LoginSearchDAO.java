package org.openiam.idm.srvc.auth.login.lucene;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
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
	protected Query parse(LoginSearchBean query) {
		final BooleanQuery luceneQuery = new BooleanQuery();
		final SearchParam param = query.getLoginMatchToken();
		if(param != null && param.isValid()) {
			Query clause = null;
			switch(param.getMatchType()) {
				case EXACT:
					clause = buildExactClause("loginUntokenized", param.getValue());
					break;
				case STARTS_WITH:
					clause = buildTokenizedClause("login", param.getValue());
					break;
				default:
					break;
			}
			if(clause != null) {
				luceneQuery.add(clause, BooleanClause.Occur.MUST);
			}
		}
		
		Query clause = buildExactClause("managedSysId", query.getManagedSysId());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("userId", query.getUserId());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
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
            final Query luceneQuery = parse(query);
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
