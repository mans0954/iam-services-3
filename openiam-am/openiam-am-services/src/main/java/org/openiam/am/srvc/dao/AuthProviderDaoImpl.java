package org.openiam.am.srvc.dao;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.am.srvc.searchbeans.converter.AuthProviderSearchBeanConverter;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("authProviderDao")
public class AuthProviderDaoImpl extends BaseDaoImpl<AuthProviderEntity, String> implements AuthProviderDao {
	
    @Autowired
    private AuthProviderSearchBeanConverter authProviderSearchBeanConverter;
	
    @Override
    protected String getPKfieldName() {
        return "id";
    }
    
    @Override
    protected Criteria getExampleCriteria(final SearchBean sb) {
    	Criteria criteria = this.getCriteria();
    	if(sb != null && (sb instanceof AuthProviderSearchBean)) {
    		final AuthProviderSearchBean searchBean = (AuthProviderSearchBean)sb;
    		final AuthProviderEntity example = authProviderSearchBeanConverter.convert(searchBean);
    		criteria = getExampleCriteria(example);
    		if(searchBean.getDefaultAuthProvider() != null) {
    			criteria.add(Restrictions.eq("defaultProvider", searchBean.getDefaultAuthProvider()));
    		}
            if(CollectionUtils.isNotEmpty(searchBean.getContentProviderIds())){
                criteria.createAlias("contentProviders", "cp");
                criteria.add(Restrictions.in("cp.id", searchBean.getContentProviderIds()));
            }
            if(CollectionUtils.isNotEmpty(searchBean.getUriPatternIds())){
                criteria.createAlias("uriPatterns", "up");
                criteria.add(Restrictions.in("up.id", searchBean.getUriPatternIds()));
            }
    	}
    	return criteria;
    }

    @Override
    protected Criteria getExampleCriteria(final AuthProviderEntity entity) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(entity.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
        } else {
            if (entity.getType() != null && StringUtils.isNotEmpty(entity.getType().getId())) {
                criteria.add(Restrictions.eq("type.id", entity.getType().getId()));
            }
            if (entity.getManagedSystem() != null && StringUtils.isNotEmpty(entity.getManagedSystem().getId())) {
                criteria.add(Restrictions.eq("managedSystem.id", entity.getManagedSystem().getId()));
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
        entity.setManagedSystem(managedSysEntity);
        return getByExample(entity);
    }

}
