package org.openiam.am.srvc.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.OAuthCodeDao;
import org.openiam.am.srvc.dao.OAuthTokenDao;
import org.openiam.am.srvc.domain.OAuthCodeEntity;
import org.openiam.am.srvc.domain.OAuthTokenEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by alexander on 24.04.15.
 */
@Service("oAuthService")
@Transactional
public class OAuthServiceImpl implements OAuthService {
    @Autowired
    private OAuthCodeDao oAuthCodeDao;
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
    public OAuthCodeEntity getOAuthCode(String clientId, String userId) {
        return oAuthCodeDao.getByClientAndUser(clientId,userId);
    }

    @Override
    @Transactional(readOnly = true)
    public OAuthCodeEntity getOAuthCodeByCode(String clientId, String code) {
        return oAuthCodeDao.getByClientAndCode(clientId, code);
    }

    @Override
    public OAuthCodeEntity saveOAuthCode(OAuthCodeEntity oAuthCodeEntity) {
        if(oAuthCodeEntity!=null){
            OAuthCodeEntity dbCodeEntity = oAuthCodeDao.findById(oAuthCodeEntity.getId());
            if(dbCodeEntity!=null){
                oAuthCodeEntity.setClient(dbCodeEntity.getClient());
                oAuthCodeEntity.setUser(dbCodeEntity.getUser());
            } else {
                oAuthCodeEntity.setClient(authProviderDao.findById(oAuthCodeEntity.getClient().getId()));
                if(oAuthCodeEntity.getUser()!=null)
                    oAuthCodeEntity.setUser(userDAO.findById(oAuthCodeEntity.getUser().getId()));
                else
                    oAuthCodeEntity.setUser(null);
            }

            if(CollectionUtils.isNotEmpty(oAuthCodeEntity.getScopeSet())){
                Set<RoleEntity> newScopeSet = new HashSet<>();
                for (RoleEntity scope: oAuthCodeEntity.getScopeSet()){
                    newScopeSet.add(roleDAO.findById(scope.getId()));
                }
                oAuthCodeEntity.setScopeSet(newScopeSet);
            }


            if(StringUtils.isBlank(oAuthCodeEntity.getId())){
                oAuthCodeDao.save(oAuthCodeEntity);
            }else {
                oAuthCodeDao.merge(oAuthCodeEntity);
            }
        }
        return oAuthCodeEntity;
    }

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

            if(CollectionUtils.isNotEmpty(oAuthTokenEntity.getScopeSet())){
                Set<RoleEntity> newScopeSet = new HashSet<>();
                for (RoleEntity scope: oAuthTokenEntity.getScopeSet()){
                    newScopeSet.add(roleDAO.findById(scope.getId()));
                }
                oAuthTokenEntity.setScopeSet(newScopeSet);
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
