package org.openiam.idm.srvc.continfo.service;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.openiam.base.ws.SearchParam;
import org.openiam.core.dao.lucene.AbstractHibernateSearchDao;
import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("emailSearchDAO")
public class EmailSearchDAO extends AbstractHibernateSearchDao<EmailAddressEntity, EmailSearchBean, String> {

	@Override
	protected Query parse(final EmailSearchBean query) {
		final BooleanQuery luceneQuery = new BooleanQuery();
		final SearchParam param = query.getEmailMatchToken();
		if(param != null && param.isValid()) {
			Query clause = null;
			switch(param.getMatchType()) {
				case EXACT:
					clause = buildExactClause("emailAddressUntokenized", param.getValue());
					break;
				case STARTS_WITH:
					clause = buildTokenizedClause("emailAddress", param.getValue());
					break;
				default:
					break;
			}
			
			if(clause != null) {
				luceneQuery.add(clause, BooleanClause.Occur.MUST);
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
            final Query luceneQuery = parse(query);
            if (luceneQuery != null) {
				final List idList = findIds(buildFullTextSessionQuery(getFullTextSession(), luceneQuery, from, size, null).setProjection("parent"));
				for (final Object row : idList) {
					final Object[] columns = (Object[]) row;
					final UserEntity id = (UserEntity) columns[0];
					result.add(id.getId());
				}
            }
    	}
        return result;
	}
}
