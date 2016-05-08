package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.List;


public class LoginEntityParser extends BaseParser<LoginEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.LOGIN_LOGIN_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.LOGIN;
    }

    @Override
    protected Class<LoginEntity> getClazz() {
        return LoginEntity.class;
    }

    @Override
    protected void parseToEntry(LoginEntity entity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case LOGIN_LOGIN:
                    entity.setLogin(value);
                    break;
                case LOGIN_MANAGED_SYS_ID:
                    entity.setManagedSysId(value);
                    break;
                case LOGIN_CANONICAL_NAME:
                    entity.setCanonicalName(value);
                    break;
                case LOGIN_USER_ID:
                    entity.setUserId(value);
                    break;
                case LOGIN_PASSWORD:
                    entity.setPassword(value);
                    break;
                case LOGIN_PWD_EQUIVALENT_TOKEN:
                    entity.setPwdEquivalentToken(value);
                    break;
                case LOGIN_PWD_CHANGED:
                    entity.setPwdChanged(Utils.getDate(value));
                    break;
                case LOGIN_PWD_EXP:
                    entity.setPwdExp(Utils.getDate(value));
                    break;
                case LOGIN_RESET_PWD:
                    entity.setResetPassword(Integer.valueOf(value));
                    break;
                case LOGIN_FIRST_TIME_LOGIN:
                    entity.setFirstTimeLogin(Integer.valueOf(value));
                    break;
                case LOGIN_IS_LOCKED:
                    entity.setIsLocked(Integer.valueOf(value));
                    break;
                case LOGIN_STATUS:
                    entity.setStatus(LoginStatusEnum.getFromString(value));
                    break;
                case LOGIN_GRACE_PERIOD:
                    entity.setGracePeriod(Utils.getDate(value));
                    break;
                case LOGIN_CREATE_DATE:
                    entity.setCreateDate(Utils.getDate(value));
                    break;
                case LOGIN_CREATED_BY:
                    entity.setCreatedBy(value);
                    break;
                case LOGIN_CURRENT_LOGIN_HOST:
                    entity.setCurrentLoginHost(value);
                    break;
                case LOGIN_AUTH_FAIL_COUNT:
                    entity.setAuthFailCount(Integer.valueOf(value));
                    break;
                case LOGIN_LAST_AUTH_ATTEMPT:
                    entity.setLastAuthAttempt(Utils.getDate(value));
                    break;
                case LOGIN_LAST_LOGIN:
                    entity.setLastLogin(Utils.getDate(value));
                    break;
                case LOGIN_LAST_LOGIN_IP:
                    entity.setLastLoginIP(value);
                    break;
                case LOGIN_PREV_LOGIN:
                    entity.setPrevLogin(Utils.getDate(value));
                    break;
                case LOGIN_PREV_LOGIN_IP:
                    entity.setPrevLoginIP(value);
                    break;
                case LOGIN_IS_DEFAULT:
                    entity.setIsDefault(Integer.valueOf(value));
                    break;
                case LOGIN_PWD_CHANGE_COUNT:
                    entity.setPasswordChangeCount(Integer.valueOf(value));
                    break;
                case LOGIN_PSWD_RESET_TOKEN:
                    entity.setPswdResetToken(value);
                    break;
                case LOGIN_PSWD_RESET_TOKEN_EXP:
                    entity.setPswdResetTokenExp(Utils.getDate(value));
                    break;
                case LOGIN_LOGIN_ID:
                    entity.setLoginId(value);
                    break;
                case LOGIN_LAST_UPDATE:
                    entity.setLastUpdate(Utils.getDate(value));
                    break;
                case LOGIN_LOWERCASE_LOGIN:
                    entity.setLowerCaseLogin(value);
                    break;
                case LOGIN_PROV_STATUS:
                    entity.setProvStatus(ProvLoginStatusEnum.valueOf(value));
                    break;
                case LOGIN_CHALLENGE_RESPONSE_FAIL_COUNT:
                    entity.setChallengeResponseFailCount(Integer.valueOf(value));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, LoginEntity entity) {
        switch (column) {
            case LOGIN_LOGIN:
                list.add(entity.getLogin());
                break;
            case LOGIN_MANAGED_SYS_ID:
                list.add(entity.getManagedSysId());
                break;
            case LOGIN_CANONICAL_NAME:
                list.add(entity.getCanonicalName());
                break;
            case LOGIN_USER_ID:
                list.add(entity.getUserId());
                break;
            case LOGIN_PASSWORD:
                list.add(entity.getPassword());
                break;
            case LOGIN_PWD_EQUIVALENT_TOKEN:
                list.add(entity.getPwdEquivalentToken());
                break;
            case LOGIN_PWD_CHANGED:
                list.add(entity.getPwdChanged());
                break;
            case LOGIN_PWD_EXP:
                list.add(entity.getPwdExp());
                break;
            case LOGIN_RESET_PWD:
                list.add(entity.getResetPassword());
                break;
            case LOGIN_FIRST_TIME_LOGIN:
                list.add(entity.getFirstTimeLogin());
                break;
            case LOGIN_IS_LOCKED:
                list.add(entity.getIsLocked());
                break;
            case LOGIN_STATUS:
                if (entity.getStatus() != null)
                    list.add(entity.getStatus().getValue());
                else {
                    list.add(null);
                }
                break;
            case LOGIN_GRACE_PERIOD:
                list.add(entity.getGracePeriod());
                break;
            case LOGIN_CREATE_DATE:
                list.add(entity.getCreateDate());
                break;
            case LOGIN_CREATED_BY:
                list.add(entity.getCreatedBy());
                break;
            case LOGIN_CURRENT_LOGIN_HOST:
                list.add(entity.getCurrentLoginHost());
                break;
            case LOGIN_AUTH_FAIL_COUNT:
                list.add(entity.getAuthFailCount());
                break;
            case LOGIN_LAST_AUTH_ATTEMPT:
                list.add(entity.getLastAuthAttempt());
                break;
            case LOGIN_LAST_LOGIN:
                list.add(entity.getLastLogin());
                break;
            case LOGIN_LAST_LOGIN_IP:
                list.add(entity.getLastLoginIP());
                break;
            case LOGIN_PREV_LOGIN:
                list.add(entity.getPrevLogin());
                break;
            case LOGIN_PREV_LOGIN_IP:
                list.add(entity.getPrevLoginIP());
                break;
            case LOGIN_IS_DEFAULT:
                list.add(entity.getIsDefault());
                break;
            case LOGIN_PWD_CHANGE_COUNT:
                list.add(entity.getPasswordChangeCount());
                break;
            case LOGIN_PSWD_RESET_TOKEN:
                list.add(entity.getPswdResetToken());
                break;
            case LOGIN_PSWD_RESET_TOKEN_EXP:
                list.add(entity.getPswdResetTokenExp());
                break;
            case LOGIN_LOGIN_ID:
                list.add(entity.getLoginId());
                break;
            case LOGIN_LAST_UPDATE:
                list.add(entity.getLastUpdate());
                break;
            case LOGIN_LOWERCASE_LOGIN:
                list.add(entity.getLowerCaseLogin());
                break;
            case LOGIN_PROV_STATUS:
                if (entity.getProvStatus() != null)
                    list.add(entity.getProvStatus().getValue());
                else list.add(null);
                break;
            case LOGIN_CHALLENGE_RESPONSE_FAIL_COUNT:
                list.add(entity.getChallengeResponseFailCount());
                break;
            default:
                break;
        }

    }
}
