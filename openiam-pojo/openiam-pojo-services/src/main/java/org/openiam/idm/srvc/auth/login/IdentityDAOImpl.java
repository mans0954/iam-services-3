package org.openiam.idm.srvc.auth.login;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
