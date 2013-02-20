package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.URIPatternMetaEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class URIPatternMetaDaoImpl extends BaseDaoImpl<URIPatternMetaEntity, String> implements URIPatternMetaDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

    @Override
    protected Criteria getExampleCriteria(final URIPatternMetaEntity entity) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(entity.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
        } else {
            if(entity.getPattern()!=null && StringUtils.isNotEmpty(entity.getPattern().getId())){
                criteria.createAlias("pattern", "p");
                criteria.add(Restrictions.eq("p.id", entity.getPattern().getId()));
            }
        }
        return criteria;
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.id=:id ");
        qry.setString("id", id);
        qry.executeUpdate();
    }
}
