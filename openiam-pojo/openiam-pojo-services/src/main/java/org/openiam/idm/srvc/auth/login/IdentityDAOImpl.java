package org.openiam.idm.srvc.auth.login;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.IdentitySearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.springframework.stereotype.Repository;

@Repository("identityDAO")
public class IdentityDAOImpl extends BaseDaoImpl<IdentityEntity, String> implements IdentityDAO {

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    public List<IdentityEntity> findByReferredId(String referredId) {
        Criteria criteria = getCriteria().add(Restrictions.eq("referredObjectId",referredId));
        return criteria.list();
    }

    @Override
    public List<IdentityEntity> findByType(IdentityTypeEnum type) {
        Criteria criteria = getCriteria().add(Restrictions.eq("type",type));
        return criteria.list();
    }

    @Override
    public IdentityEntity findByManagedSysId(String referredId, String managedSysId) {
        Criteria criteria = getCriteria().add(Restrictions.and(Restrictions.eq("managedSysId",managedSysId),Restrictions.eq("referredObjectId",referredId)));
        return (IdentityEntity)criteria.uniqueResult();
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if(searchBean != null && searchBean instanceof IdentitySearchBean) {
            final IdentitySearchBean sb = (IdentitySearchBean)searchBean;
            
            if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
            }
            
            if(StringUtils.isNotBlank(sb.getIdentity())) {
            	criteria.add(Restrictions.eq("identity", sb.getIdentity()));
            }
            
            if(StringUtils.isNotBlank(sb.getManagedSysId())) {
            	criteria.add(Restrictions.eq("managedSysId", sb.getManagedSysId()));
            }
            
            if(StringUtils.isNotBlank(sb.getReferredObjectId())) {
            	criteria.add(Restrictions.eq("referredObjectId", sb.getReferredObjectId()));
            }
            
            if(sb.getStatus() != null) {
            	criteria.add(Restrictions.eq("status", sb.getStatus()));
            }
            
            if(sb.getType() != null) {
            	criteria.add(Restrictions.eq("type", sb.getType()));
            }
            
            if(StringUtils.isNotBlank(sb.getCreatedBy())) {
            	criteria.add(Restrictions.eq("createdBy", sb.getCreatedBy()));
            }
            
            if(sb.getCreateFromDate() != null && sb.getCreateToDate() != null) {
            	criteria.add(Restrictions.between("createDate", sb.getCreateFromDate(), sb.getCreateToDate()));
            } else if(sb.getCreateFromDate() != null) {
            	criteria.add(Restrictions.gt("createDate", sb.getCreateFromDate()));
            } else if(sb.getCreateToDate() != null) {
            	criteria.add(Restrictions.lt("createDate", sb.getCreateToDate()));
            }
        }

        return criteria;
    }

    @Override
    public IdentityEntity getByIdentityManagedSys(String principal, String managedSysId) {
        return (IdentityEntity) getCriteria()
                .add(Restrictions.eq("identity", principal))
                .add(Restrictions.eq("managedSysId", managedSysId)).uniqueResult();
    }
}
