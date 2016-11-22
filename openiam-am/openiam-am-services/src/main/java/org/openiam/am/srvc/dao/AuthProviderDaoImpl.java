package org.openiam.am.srvc.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.searchbean.AuthProviderSearchBean;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.springframework.stereotype.Repository;

@Repository("authProviderDao")
public class AuthProviderDaoImpl extends BaseDaoImpl<AuthProviderEntity, String> implements AuthProviderDao {
	
    @Override
    protected String getPKfieldName() {
        return "id";
    }
    
    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
    	Criteria criteria = this.getCriteria();
    	if(searchBean != null && (searchBean instanceof AuthProviderSearchBean)) {
    		final AuthProviderSearchBean sb = (AuthProviderSearchBean)searchBean;
    		if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
            } else {
                if (StringUtils.isNotEmpty(sb.getProviderType())) {
                    criteria.add(Restrictions.eq("type.id", sb.getProviderType()));
                }
                if (StringUtils.isNotEmpty(sb.getManagedSysId())) {
                    criteria.add(Restrictions.eq("managedSystem.id", sb.getManagedSysId()));
                }

                final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
                if(nameCriterion != null) {
                	criteria.add(nameCriterion);
                }
                
                if(StringUtils.isNotEmpty(sb.getResourceId())) {
                	criteria.add(Restrictions.eq("resource.id", sb.getResourceId()));
                }
	    		if(sb.getDefaultAuthProvider() != null) {
	    			criteria.add(Restrictions.eq("defaultProvider", sb.getDefaultAuthProvider().booleanValue()));
	    		}
	            if(CollectionUtils.isNotEmpty(sb.getContentProviderIds())){
	                criteria.createAlias("contentProviders", "cp");
	                criteria.add(Restrictions.in("cp.id", sb.getContentProviderIds()));
	            }
	            if(CollectionUtils.isNotEmpty(sb.getUriPatternIds())){
	                criteria.createAlias("uriPatterns", "up");
	                criteria.add(Restrictions.in("up.id", sb.getUriPatternIds()));
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
	public List<AuthProviderEntity> getByResourceId(String resourceId) {
		final AuthProviderSearchBean sb = new AuthProviderSearchBean();
		sb.setResourceId(resourceId);
		return getByExample(sb);
	}

    public List<AuthProviderEntity> getByManagedSysId(final String managedSysId) {
    	final AuthProviderSearchBean sb = new AuthProviderSearchBean();
    	sb.setManagedSysId(managedSysId);
        return getByExample(sb);
    }

    @Override
    public AuthProviderEntity getOAuthClient(String clientId) {
        Criteria criteria = this.getCriteria();

        criteria.createAlias("attributes","attr", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("attr.attribute.id", "OAuthClientID"));
        criteria.add(Restrictions.eq("attr.value", clientId));

        return (AuthProviderEntity)criteria.uniqueResult();
    }

	@Override
	public List<AuthProviderEntity> getOAuthClients() {
		final Criteria criteria = this.getCriteria();
		criteria.createAlias("attributes","attr", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("attr.attribute.id", "OAuthClientID"));
		return criteria.list();
	}

}
