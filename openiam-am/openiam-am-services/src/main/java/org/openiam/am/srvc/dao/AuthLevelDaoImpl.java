package org.openiam.am.srvc.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.openiam.am.srvc.domain.AuthLevelEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class AuthLevelDaoImpl extends BaseDaoImpl<AuthLevelEntity, String> implements AuthLevelDao {

    protected boolean cachable() {
        return true;
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }
}
