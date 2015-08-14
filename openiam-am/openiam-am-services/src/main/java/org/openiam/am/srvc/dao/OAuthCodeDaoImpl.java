package org.openiam.am.srvc.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.OAuthCodeEntity;
import org.openiam.am.srvc.domain.pk.OAuthCodeIdEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created by alexander on 21/07/15.
 */
@Repository("oauthCodeDao")
public class OAuthCodeDaoImpl extends BaseDaoImpl<OAuthCodeEntity, OAuthCodeIdEntity> implements OAuthCodeDao {
    @Override
    protected String getPKfieldName() {
        return "primaryKey";
    }

    @Override
    public OAuthCodeEntity getByClientAndUser(String providerId, String userId) {
        Criteria criteria = this.getCriteria();
        criteria.add(Restrictions.eq("primaryKey.client.id", providerId));
        criteria.add(Restrictions.eq("primaryKey.user.id", userId));

        return (OAuthCodeEntity)criteria.uniqueResult();
    }

    @Override
    public OAuthCodeEntity getByCode(String code){
        Criteria criteria = this.getCriteria();
        criteria.add(Restrictions.eq("primaryKey.code", code));

        return (OAuthCodeEntity)criteria.uniqueResult();
    }
}
