package org.openiam.am.srvc.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class URIPatternDaoImpl extends BaseDaoImpl<URIPatternEntity, String> implements URIPatternDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}
    @Override
    protected Criteria getExampleCriteria(final URIPatternEntity entity) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(entity.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
        } else {
            if(entity.getContentProvider()!=null && StringUtils.isNotEmpty(entity.getContentProvider().getId())){
                criteria.createAlias("contentProvider", "p");
                criteria.add(Restrictions.eq("p.id", entity.getContentProvider().getId()));
            }

            if(entity.getPattern()!=null && StringUtils.isNotEmpty(entity.getPattern())){
                MatchMode matchMode = MatchMode.ANYWHERE;
                criteria.add(Restrictions.ilike("pattern", entity.getPattern(), MatchMode.ANYWHERE));
//                criteria.add(Restrictions.eq("pattern", entity.getPattern()));
            }
            
            if(entity.getResource() != null && StringUtils.isNotEmpty(entity.getResource().getId())) {
            	criteria.add(Restrictions.eq("resource.id", entity.getResource().getId()));
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
		final URIPatternEntity entity = new URIPatternEntity();
		final ResourceEntity resource = new ResourceEntity();
		resource.setId(resourceId);
		entity.setResource(resource);
		return getByExample(entity);
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
