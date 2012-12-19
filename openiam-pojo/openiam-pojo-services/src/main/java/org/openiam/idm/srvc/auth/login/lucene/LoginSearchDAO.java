package org.openiam.idm.srvc.auth.login.lucene;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.openiam.core.dao.lucene.AbstractHibernateSearchDao;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.springframework.stereotype.Repository;

@Repository("loginSearchDAO")
public class LoginSearchDAO extends AbstractHibernateSearchDao<LoginEntity, LoginSearchBean, String> {

	@Override
	protected Query parse(LoginSearchBean query) {
		final BooleanQuery luceneQuery = new BooleanQuery();
		Query clause = buildTokenizedClause("login", query.getLogin());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("managedSysId", query.getManagedSysId());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("domainId", query.getDomainId());
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

	public List<String> findUserIds(final LoginSearchBean query) {
		final List<String> result = new ArrayList<String>();
    	if ((query != null)) {
            final Query luceneQuery = parse(query);
            if (luceneQuery != null) {
				final List idList = findIds(buildFullTextSessionQuery(getFullTextSession(), luceneQuery, null).setProjection("userId"));
				for (final Object row : idList) {
					final Object[] columns = (Object[]) row;
					final String id = (String) columns[0];
					result.add(id);
				}
            }
    	}
        return result;
	}
}
