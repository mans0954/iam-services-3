package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.OAuthCodeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created by alexander on 24.04.15.
 */
@Repository("oAuthCodeDao")
public class OAuthCodeDaoImpl extends BaseDaoImpl<OAuthCodeEntity, String> implements OAuthCodeDao  {
    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    public OAuthCodeEntity getByClientAndUser(String clientId, String userId) {
        Criteria criteria = this.getCriteria();
        if(StringUtils.isNotBlank(clientId)){
            criteria.add(Restrictions.eq("client.id", clientId));
        }
        if(StringUtils.isNotBlank(userId)){
            criteria.add(Restrictions.eq("user.id", userId));
        }

        return (OAuthCodeEntity)criteria.uniqueResult();
    }

    @Override
    public OAuthCodeEntity getByClientAndCode(String clientId, String code) {
        Criteria criteria = this.getCriteria();
        if(StringUtils.isNotBlank(clientId)){
            criteria.add(Restrictions.eq("client.id", clientId));
        }
        if(StringUtils.isNotBlank(code)){
            criteria.add(Restrictions.eq("code", code));
        }

        return (OAuthCodeEntity)criteria.uniqueResult();
    }
}
