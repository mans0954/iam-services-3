package org.openiam.idm.srvc.recon.service;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.ReconConfigSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.springframework.stereotype.Repository;

/**
 * Home object for domain model class ReconiliationConfig.
 */
@Repository
public class ReconciliationConfigDAOImpl extends
        BaseDaoImpl<ReconciliationConfigEntity, String> implements
        ReconciliationConfigDAO {

    public ReconciliationConfigEntity get(String id) {
        return (ReconciliationConfigEntity)getSession().get(ReconciliationConfigEntity.class,id);
    }

    @Override
    protected Criteria getExampleCriteria(SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if(searchBean != null && searchBean instanceof ReconConfigSearchBean) {
            final ReconConfigSearchBean reconSearchBean = (ReconConfigSearchBean)searchBean;
            if(StringUtils.isNotBlank(reconSearchBean.getKey())) {
            	criteria.add(Restrictions.eq(getPKfieldName(), reconSearchBean.getKey()));
            }
            if(StringUtils.isNotBlank(reconSearchBean.getManagedSysId())) {
            	criteria.add(Restrictions.eq("managedSysId", reconSearchBean.getManagedSysId()));
            }
            if(StringUtils.isNotBlank(reconSearchBean.getName())) {
            	criteria.add(Restrictions.eq("name", reconSearchBean.getName()));
            }
            if(StringUtils.isNotBlank(reconSearchBean.getReconType())) {
            	criteria.add(Restrictions.eq("reconType", reconSearchBean.getReconType()));
            }
            if(StringUtils.isNotBlank(reconSearchBean.getResourceId())) {
            	criteria.add(Restrictions.eq("resourceId", reconSearchBean.getResourceId()));
            }
        }
        return criteria;
    }

    public ReconciliationConfigEntity findByResourceIdByType(final String resourceId, final String type) {
        final ReconConfigSearchBean sb = new ReconConfigSearchBean();
        sb.setResourceId(resourceId);
        sb.setReconType(type);
        final List<ReconciliationConfigEntity> results = getByExample(sb);
        return (CollectionUtils.isNotEmpty(results)) ? results.get(0) : null;
    }

    public List<ReconciliationConfigEntity> findByResourceId(final String resourceId) {
    	final ReconConfigSearchBean sb = new ReconConfigSearchBean();
    	sb.setResourceId(resourceId);
    	return getByExample(sb);
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

}
