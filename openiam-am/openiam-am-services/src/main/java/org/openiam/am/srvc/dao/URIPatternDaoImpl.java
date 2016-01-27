package org.openiam.am.srvc.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.searchbeans.URIPatternSearchBean;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class URIPatternDaoImpl extends BaseDaoImpl<URIPatternEntity, String> implements URIPatternDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}
	
	@Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
		Criteria criteria = this.getCriteria();
		if(searchBean != null && searchBean instanceof URIPatternSearchBean) {
			final URIPatternSearchBean sb = (URIPatternSearchBean)searchBean;
			
			if(StringUtils.isNotEmpty(sb.getContentProviderId())){
                criteria.createAlias("contentProvider", "p");
                criteria.add(Restrictions.eq("p.id", sb.getContentProviderId()));
            }
            if(StringUtils.isNotBlank(sb.getAuthProviderId())) {
                criteria.add(Restrictions.eq("authProvider.id", sb.getAuthProviderId()));
            }

            if(StringUtils.isNotEmpty(sb.getPattern())){
                MatchMode matchMode = MatchMode.ANYWHERE;
                criteria.add(Restrictions.ilike("pattern", sb.getPattern(), MatchMode.ANYWHERE));
//                criteria.add(Restrictions.eq("pattern", entity.getPattern()));
            }
            
            if(StringUtils.isNotEmpty(sb.getResourceId())) {
            	criteria.add(Restrictions.eq("resource.id", sb.getResourceId()));
            }

			if(sb.getShowOnApplicationPage() != null) {
            	criteria.add(Restrictions.eq("showOnApplicationPage", sb.getShowOnApplicationPage()));
            }
		}
		return criteria;
	}
	
    @Override
    @Transactional
    public void deleteByProvider(String providerId) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.contentProvider.id=:providerId ");
        qry.setString("providerId", providerId);
        qry.executeUpdate();
    }

    @Override
    @Transactional
    public void deleteById(String patternId) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.id=:patternId ");
        qry.setString("patternId", patternId);
        qry.executeUpdate();
    }
	@Override
	public List<URIPatternEntity> getByResourceId(String resourceId) {
		final URIPatternSearchBean sb = new URIPatternSearchBean();
		sb.setResourceId(resourceId);
		return getByExample(sb);
	}
	@Override
	public List<URIPatternEntity> getURIPatternsForContentProviderMatchingPattern(
			String contentProviderId, String pattern) {
		final Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("contentProvider.id", contentProviderId));
		criteria.add(Restrictions.eq("pattern", pattern));
		return criteria.list();
	}
}
