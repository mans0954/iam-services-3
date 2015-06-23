package org.openiam.idm.srvc.mngsys.service;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.MngSysPolicyEntity;
import org.springframework.stereotype.Repository;

@Repository("mngSysPolicyDAO")
public class MngSysPolicyDAOImpl extends BaseDaoImpl<MngSysPolicyEntity, String> implements MngSysPolicyDAO {

    @Override
    protected String getPKfieldName() {
        return "id";
    }
}
