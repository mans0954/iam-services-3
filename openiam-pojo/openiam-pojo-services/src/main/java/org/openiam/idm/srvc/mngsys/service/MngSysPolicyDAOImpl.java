package org.openiam.idm.srvc.mngsys.service;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.MngSysPolicyEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("mngSysPolicyDAO")
public class MngSysPolicyDAOImpl extends BaseDaoImpl<MngSysPolicyEntity, String> implements MngSysPolicyDAO {

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    public List<MngSysPolicyEntity> findByMngSysId(String mngSysId) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("managedSystem.id", mngSysId))
                .addOrder(Order.asc(getPKfieldName()));
        return criteria.list();
    }

    @Override
    public List<MngSysPolicyEntity> findByMngSysIdAndType(String mngSysId, String metadataTypeId) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("managedSystem.id", mngSysId))
                .add(Restrictions.eq("type.id",metadataTypeId))
                .addOrder(Order.asc(getPKfieldName()));
        return criteria.list();
    }

    @Override
    public MngSysPolicyEntity findPrimaryByMngSysIdAndType(String mngSysId, String metadataTypeId) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("managedSystem.id", mngSysId))
                .add(Restrictions.eq("type.id",metadataTypeId))
                .add(Restrictions.eq("primary",true))
                .addOrder(Order.asc(getPKfieldName()));
        return (MngSysPolicyEntity)criteria.uniqueResult();
    }
}
