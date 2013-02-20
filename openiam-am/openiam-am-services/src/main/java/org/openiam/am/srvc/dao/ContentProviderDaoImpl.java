package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ContentProviderDaoImpl extends BaseDaoImpl<ContentProviderEntity, String> implements ContentProviderDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

    @Override
    protected Criteria getExampleCriteria(final ContentProviderEntity providerEntity) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(providerEntity.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), providerEntity.getId()));
        } else {

            if (StringUtils.isNotEmpty(providerEntity.getName())) {
                String name = providerEntity.getName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(name, "*") == 0) {
                    matchMode = MatchMode.END;
                    name = name.substring(1);
                }
                if (StringUtils.isNotEmpty(name) && StringUtils.indexOf(name, "*") == name.length() - 1) {
                    name = name.substring(0, name.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(name)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("name", name, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("name", name));
                    }
                }
            }
            if (StringUtils.isNotEmpty(providerEntity.getDomainPattern())) {
                criteria.add(Restrictions.eq("domainPattern", providerEntity.getDomainPattern()));
            }
            if (StringUtils.isNotEmpty(providerEntity.getContextPath())) {
                criteria.add(Restrictions.eq("contextPath", providerEntity.getContextPath()));
            }
        }
        return criteria;
    }

    @Override
    public List<ContentProviderEntity> getProviderByDomainPattern(String domainPattern, String contextPath, Boolean isSSL){
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotEmpty(domainPattern)) {
            criteria.add(Restrictions.eq("domainPattern", domainPattern));
        }
        if (StringUtils.isNotEmpty(contextPath)) {
            criteria.add(Restrictions.eq("contextPath", contextPath));
        }

        if(isSSL==null)
            criteria.add(Restrictions.isNull("isSSL"));
        else
            criteria.add(Restrictions.eq("isSSL", isSSL));
        return criteria.list();
    }

    @Override
    @Transactional
    public void deleteById(String providerId) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.id=:providerId ");
        qry.setString("providerId", providerId);
        qry.executeUpdate();
    }
}
