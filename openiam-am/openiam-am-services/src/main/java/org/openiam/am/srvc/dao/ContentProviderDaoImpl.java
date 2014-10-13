package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
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
    protected Criteria getExampleCriteria(final ContentProviderEntity entity) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(entity.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
        } else {

            if (StringUtils.isNotEmpty(entity.getName())) {
                String name = entity.getName();
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
            if (StringUtils.isNotEmpty(entity.getDomainPattern())) {
                criteria.add(Restrictions.eq("domainPattern", entity.getDomainPattern()));
            }

            if(entity.getResource() != null && StringUtils.isNotEmpty(entity.getResource().getId())) {
            	criteria.add(Restrictions.eq("resource.id", entity.getResource().getId()));
            }
            
            if(entity.getAuthProvider() != null && StringUtils.isNotBlank(entity.getAuthProvider().getId())) {
            	criteria.add(Restrictions.eq("authProvider.id", entity.getAuthProvider().getId()));
            }
            
            if(entity.getManagedSystem() != null && StringUtils.isNotBlank(entity.getManagedSystem().getId())) {
            	criteria.add(Restrictions.eq("managedSystem.id", entity.getManagedSystem().getId()));
            }
        }
        return criteria;
    }

    @Override
    public List<ContentProviderEntity> getProviderByDomainPattern(String domainPattern, Boolean isSSL){
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotEmpty(domainPattern)) {
            criteria.add(Restrictions.eq("domainPattern", domainPattern));
        }
        /*
        if (StringUtils.isNotEmpty(contextPath)) {
            criteria.add(Restrictions.eq("contextPath", contextPath));
        }
        */

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

	@Override
	public List<ContentProviderEntity> getByResourceId(String resourceId) {
		final ContentProviderEntity entity = new ContentProviderEntity();
		final ResourceEntity resource = new ResourceEntity();
		resource.setId(resourceId);
		entity.setResource(resource);
		return getByExample(entity);
	}
}
