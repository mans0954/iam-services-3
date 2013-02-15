package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.core.dao.BaseDaoImpl;
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
}
