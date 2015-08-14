package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.OAuthTokenEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created by alexander on 24.04.15.
 */
@Repository("oAuthTokenDao")
public class OAuthTokenDaoImpl extends BaseDaoImpl<OAuthTokenEntity, String> implements OAuthTokenDao {
    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    public OAuthTokenEntity getByRefreshToken(String refreshToken) {
        Criteria criteria = this.getCriteria();
        if(StringUtils.isNotBlank(refreshToken)){
            criteria.add(Restrictions.eq("refreshToken", refreshToken));
        }

        return (OAuthTokenEntity)criteria.uniqueResult();
    }

    @Override
    public OAuthTokenEntity getByAccessToken(String accessToken) {
        Criteria criteria = this.getCriteria();
        if(StringUtils.isNotBlank(accessToken)){
            criteria.add(Restrictions.eq("token", accessToken));
        }

        return (OAuthTokenEntity)criteria.uniqueResult();
    }

}
