package org.openiam.am.srvc.dao;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("authProviderDao")
public class AuthProviderDaoImpl extends BaseDaoImpl<AuthProviderEntity, String> implements AuthProviderDao {
    @Override
    protected String getPKfieldName() {
        return "providerId";
    }

    @Override
    protected Criteria getExampleCriteria(final AuthProviderEntity entity) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(entity.getProviderId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), entity.getProviderId()));
        } else {
            if (StringUtils.isNotEmpty(entity.getProviderType())) {
                criteria.add(Restrictions.eq("providerType", entity.getProviderType()));
            }
            if (StringUtils.isNotEmpty(entity.getManagedSysId())) {
                criteria.add(Restrictions.eq("managedSysId", entity.getManagedSysId()));
            }
            
            if(entity.getNextAuthProvider() != null && StringUtils.isNotBlank(entity.getNextAuthProvider().getProviderId())) {
            	criteria.add(Restrictions.eq(String.format("nextAuthProvider.%s", getPKfieldName()), entity.getNextAuthProvider().getProviderId()));
            }

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
            
            if(entity.getResource() != null && StringUtils.isNotEmpty(entity.getResource().getId())) {
            	criteria.add(Restrictions.eq("resource.id", entity.getResource().getId()));
            }
        }
        return criteria;
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = this.getCriteria();
        if(searchBean != null && (searchBean instanceof AuthProviderSearchBean)) {
            final AuthProviderSearchBean sb = (AuthProviderSearchBean)searchBean;
            if (StringUtils.isNotBlank(sb.getKey())) {
                criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
            } else {
                if (StringUtils.isNotEmpty(sb.getProviderType())) {
                    criteria.add(Restrictions.eq("type.id", sb.getProviderType()));
                }
                if (StringUtils.isNotEmpty(sb.getManagedSysId())) {
                    criteria.add(Restrictions.eq("managedSystem.id", sb.getManagedSysId()));
                }
                if(sb.getLinkableToContentProvider() != null) {
                    criteria.createAlias("type", "providerType");
                    criteria.add(Restrictions.eq("providerType.linkableToContentProvider", sb.getLinkableToContentProvider()));
                }
            }
        }
        return criteria;
    }

    @Override
    public List<String> getPkListByType(String providerType) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("providerType",providerType)).setProjection(Projections.property(getPKfieldName()));
        return criteria.list();
    }

    @Override
    @Transactional
    public void deleteByPkList(List<String> pkList) {
        if(pkList!=null && !pkList.isEmpty()) {
            Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.providerId in (:pkList) ");
            qry.setParameterList("pkList", pkList);
            qry.executeUpdate();
        }
    }

	@Override
	public List<AuthProviderEntity> getByResourceId(String resourceId) {
		final AuthProviderEntity entity = new AuthProviderEntity();
		final ResourceEntity resource = new ResourceEntity();
		resource.setId(resourceId);
		entity.setResource(resource);
		return getByExample(entity);
	}

    public List<AuthProviderEntity> getByManagedSysId(final String managedSysId) {
        final AuthProviderEntity entity = new AuthProviderEntity();
        final ManagedSysEntity managedSysEntity = new ManagedSysEntity();
        managedSysEntity.setId(managedSysId);
        entity.setManagedSys(managedSysEntity);
        entity.setManagedSysId(managedSysId);
        return getByExample(entity);
    }

}
