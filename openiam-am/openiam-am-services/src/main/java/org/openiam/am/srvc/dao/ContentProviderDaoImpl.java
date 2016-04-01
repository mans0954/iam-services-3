package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
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
	
    protected boolean cachable() {
        return true;
    }
	

    @Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof ContentProviderSearchBean) {
			final ContentProviderSearchBean sb = (ContentProviderSearchBean)searchBean;
			if (StringUtils.isNotBlank(sb.getKey())) {
	            criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
	        } else {

	            if (StringUtils.isNotEmpty(sb.getName())) {
	                String name = sb.getName();
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
	            if (StringUtils.isNotEmpty(sb.getDomainPattern())) {
	                criteria.add(Restrictions.eq("domainPattern", sb.getDomainPattern()));
	            }

	            if(StringUtils.isNotEmpty(sb.getResourceId())) {
	            	criteria.add(Restrictions.eq("resource.id", sb.getResourceId()));
	            }
	            
	            if(StringUtils.isNotBlank(sb.getAuthProviderId())) {
	            	criteria.add(Restrictions.eq("authProvider.id", sb.getAuthProviderId()));
	            }
	        }
		}
        criteria.setCacheable(this.cachable());
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
        criteria.setCacheable(this.cachable());
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
		final ContentProviderSearchBean sb = new ContentProviderSearchBean();
		sb.setResourceId(resourceId);
		return getByExample(sb);
	}
}
