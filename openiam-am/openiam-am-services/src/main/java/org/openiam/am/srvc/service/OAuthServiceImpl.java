package org.openiam.am.srvc.service;

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.OAuthTokenDao;
import org.openiam.am.srvc.domain.OAuthTokenEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by alexander on 24.04.15.
 */
@Service("oAuthService")
@Transactional
public class OAuthServiceImpl implements OAuthService {
    @Autowired
    private OAuthTokenDao oAuthTokenDao;
    @Autowired
    private AuthProviderDao authProviderDao;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private RoleDAO roleDAO;

    @Override
    @Transactional(readOnly = true)
    public OAuthTokenEntity getOAuthTokenByToken(String accessToken) {
        return oAuthTokenDao.getByAccessToken(accessToken);
    }

    @Override
    @Transactional(readOnly = true)
    public OAuthTokenEntity getOAuthTokenByRefreshToken(String refreshToken) {
        return oAuthTokenDao.getByRefreshToken(refreshToken);
    }

    @Override
    public OAuthTokenEntity saveOAuthToken(OAuthTokenEntity oAuthTokenEntity) {
        if(oAuthTokenEntity!=null){
            OAuthTokenEntity dbEntity = oAuthTokenDao.findById(oAuthTokenEntity.getId());
            if(dbEntity!=null){
                oAuthTokenEntity.setClient(dbEntity.getClient());
                oAuthTokenEntity.setUser(dbEntity.getUser());
            } else {
                oAuthTokenEntity.setClient(authProviderDao.findById(oAuthTokenEntity.getClient().getId()));
                if(oAuthTokenEntity.getUser()!=null)
                    oAuthTokenEntity.setUser(userDAO.findById(oAuthTokenEntity.getUser().getId()));
                else
                    oAuthTokenEntity.setUser(null);
            }

            if(StringUtils.isBlank(oAuthTokenEntity.getId())){
                oAuthTokenDao.save(oAuthTokenEntity);
            }else {
                oAuthTokenDao.merge(oAuthTokenEntity);
            }
        }
        return oAuthTokenEntity;
    }
}
