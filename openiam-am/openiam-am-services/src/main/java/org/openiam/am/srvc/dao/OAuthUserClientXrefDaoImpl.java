package org.openiam.am.srvc.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.openiam.am.srvc.domain.OAuthUserClientXrefEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hibernate.criterion.Projections.rowCount;

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
    public List<OAuthUserClientXrefEntity> getByClientAndUser(String clientId, String userId, Boolean isAuthorized) {
        Criteria criteria = getCriteria();

        criteria.createAlias("client","cl", JoinType.INNER_JOIN);
        criteria.createAlias("cl.attributes", "attr", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("attr.attribute.id", "OAuthClientID"));
        criteria.add(Restrictions.eq("attr.value", clientId));
        criteria.add(Restrictions.eq("user.id", userId));

        if(isAuthorized!=null){
            criteria.add(Restrictions.eq("isAllowed", isAuthorized));
        }



        return criteria.list();
    }

    @Override
    public void deleteByScopeId(String scopeId) {
        getSession().createQuery("delete from " + this.domainClass.getName() + " obj where obj.scope.id=?")
                .setParameter(0, scopeId).executeUpdate();
    }
    @Override
    public void deleteByUserIdScopeId(String userId, String scopeId){
        getSession().createQuery("delete from " + this.domainClass.getName() + " obj where obj.user.id=? and obj.scope.id=?")
                .setParameter(0, userId).setParameter(1, scopeId).executeUpdate();
    }

    @Override
    public void deleteByClientIdUserId(String providerId, String userId){
        getSession().createQuery("delete from " + this.domainClass.getName() + " obj where obj.user.id=? and obj.client.id=?")
                .setParameter(0, userId).setParameter(1, providerId).executeUpdate();
    }
}
