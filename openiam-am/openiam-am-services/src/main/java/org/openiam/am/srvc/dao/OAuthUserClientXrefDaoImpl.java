package org.openiam.am.srvc.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.openiam.am.srvc.domain.OAuthUserClientXrefEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by alexander on 15/07/15.
 */
@Repository("oAuthUserClientXrefDao")
public class OAuthUserClientXrefDaoImpl extends BaseDaoImpl<OAuthUserClientXrefEntity, String> implements OAuthUserClientXrefDao  {
    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    public List<OAuthUserClientXrefEntity> getByClientAndUser(String clientId, String userId) {
        Criteria criteria = getCriteria();

        criteria.createAlias("client","cl", JoinType.INNER_JOIN);
        criteria.createAlias("cl.attributes","attr", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("attr.attribute.id", "OAuthClientID"));
        criteria.add(Restrictions.eq("attr.value", clientId));
        criteria.add(Restrictions.eq("user.id", userId));

        return criteria.list();
    }
}
