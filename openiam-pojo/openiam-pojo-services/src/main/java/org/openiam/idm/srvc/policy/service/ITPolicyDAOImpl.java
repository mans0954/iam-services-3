package org.openiam.idm.srvc.policy.service;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.policy.domain.ITPolicyEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("itPolicyDAO")
public class ITPolicyDAOImpl extends BaseDaoImpl<ITPolicyEntity, String> implements ITPolicyDAO {

    @Override
    public ITPolicyEntity findITPolicy() {
        List<ITPolicyEntity> l = findAll();
        if (l != null && l.size() > 0) {
            return l.get(0);
        }
        return null;
    }

    @Override
    protected String getPKfieldName() {
        return "policyId";
    }
}
