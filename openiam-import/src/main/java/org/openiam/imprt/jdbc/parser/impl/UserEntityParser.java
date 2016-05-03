package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.policy.dto.ResetPasswordTypeEnum;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexander on 26/04/16.
 */
public class UserEntityParser extends BaseParser<UserEntity> {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.USERS_USER_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.USERS;
    }

    @Override
    protected Class<UserEntity> getClazz() {
        return UserEntity.class;
    }

    @Override
    protected void parseToEntry(UserEntity userEntity, ImportPropertiesKey key, String value) throws Exception {
        if (key != null) {
            switch (key) {
                case USERS_USER_ID:
                    userEntity.setId(value);
                    break;
                case USERS_FIRST_NAME:
                    userEntity.setFirstName(value);
                    break;
                case USERS_LAST_NAME:
                    userEntity.setLastName(value);
                    break;
                case USERS_MIDDLE_INIT:
                    userEntity.setMiddleInit(value);
                    break;
                case USERS_TYPE_ID:
                    userEntity.setType(new MetadataTypeEntityParser().getById(value));
                    break;
                case USERS_CLASSIFICATION:
                    userEntity.setClassification(value);
                    break;
                case USERS_TITLE:
                    userEntity.setTitle(value);
                    break;
                case USERS_MAIL_CODE:
                    userEntity.setMailCode(value);
                    break;
                case USERS_COST_CENTER:
                    userEntity.setCostCenter(value);
                    break;
                case USERS_STATUS:
                    userEntity.setStatus(UserStatusEnum.getFromString(value));
                    break;
                case USERS_SECONDARY_STATUS:
                    userEntity.setSecondaryStatus(UserStatusEnum.getFromString(value));
                    break;
                case USERS_BIRTHDATE:
                    userEntity.setBirthdate(Utils.getDate(value));
                    break;
                case USERS_SEX:
                    userEntity.setSex(value);
                    break;
                case USERS_CREATE_DATE:
                    userEntity.setCreateDate(Utils.getDate(value));
                    break;
                case USERS_CREATED_BY:
                    userEntity.setCreatedBy(value);
                    break;
                case USERS_LAST_UPDATE:
                    userEntity.setLastUpdate(Utils.getDate(value));
                    break;
                case USERS_LAST_UPDATED_BY:
                    userEntity.setLastUpdatedBy(value);
                    break;
                case USERS_PREFIX:
                    userEntity.setPrefix(value);
                    break;
                case USERS_SUFFIX:
                    userEntity.setSuffix(value);
                    break;
                case USERS_USER_TYPE_IND:
                    userEntity.setUserTypeInd(value);
                    break;
                case USERS_EMPLOYEE_ID:
                    userEntity.setEmployeeId(value);
                    break;
                case USERS_EMPLOYEE_TYPE:
                    userEntity.setEmployeeType(new MetadataTypeEntityParser().getById(value));
                    break;
                case USERS_LOCATION_CD:
                    userEntity.setLocationCd(value);
                    break;
                case USERS_LOCATION_NAME:
                    userEntity.setLocationName(value);
                    break;
                case USERS_COMPANY_OWNER_ID:
                    userEntity.setCompanyOwnerId(value);
                    break;
                case USERS_JOB_CODE:
                    userEntity.setJobCode(new MetadataTypeEntityParser().getById(value));
                    break;
                case USERS_ALTERNATE_ID:
                    userEntity.setAlternateContactId(value);
                    break;
                case USERS_START_DATE:
                    userEntity.setStartDate(Utils.getDate(value));
                    break;
                case USERS_LAST_DATE:
                    userEntity.setLastDate(Utils.getDate(value));
                    break;
                case USERS_MAIDEN_NAME:
                    userEntity.setMailCode(value);
                    break;
                case USERS_NICKNAME:
                    userEntity.setNickname(value);
                    break;
                case USERS_PASSWORD_THEME:
                    userEntity.setPasswordTheme(value);
                    break;
                case USERS_SHOW_IN_SEARCH:
                    userEntity.setShowInSearch(Integer.valueOf(value));
                    break;
                case USERS_USER_OWNER_ID:
                    userEntity.setUserOwnerId(value);
                    break;
                case USERS_DATE_PASSWORD_CHANGED:
                    userEntity.setDatePasswordChanged(Utils.getDate(value));
                    break;
                case USERS_DATE_CHALLENGE_RESP_CHANGED:
                    userEntity.setDateChallengeRespChanged(Utils.getDate(value));
                    break;
                case USERS_SYSTEM_FLAG:
                    userEntity.setSystemFlag(value);
                    break;
                case USERS_DATE_IT_POLICY_APPROVED:
                    userEntity.setDateITPolicyApproved(Utils.getDate(value));
                    break;
                case USERS_CLAIM_DATE:
                    userEntity.setClaimDate(Utils.getDate(value));
                    break;
                case USERS_RESET_PASSWORD_TYPE:
                    userEntity.setResetPasswordType(ResetPasswordTypeEnum.valueOf(value));
                    break;
                case USERS_LASTNAME_PREFIX:
                    userEntity.setPrefixLastName(value);
                    break;
                case USERS_SUB_TYPE_ID:
                    userEntity.setSubType(new MetadataTypeEntityParser().getById(value));
                    break;
                case USERS_PARTNER_NAME:
                    userEntity.setPartnerName(value);
                    break;
                case USERS_PREFIX_PARTNER_NAME:
                    userEntity.setPrefixPartnerName(value);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, UserEntity entity) {
        switch (column) {
            case USERS_USER_ID: {
                list.add(entity.getId());
                break;
            }
            case USERS_FIRST_NAME: {
                list.add(entity.getFirstName());
                break;
            }
            case USERS_LAST_NAME: {
                list.add(entity.getLastName());
                break;
            }
            case USERS_MIDDLE_INIT: {
                list.add(entity.getMiddleInit());
                break;
            }
            case USERS_TYPE_ID: {
                list.add(entity.getType());
                break;
            }
            case USERS_CLASSIFICATION: {
                list.add(entity.getClassification());
                break;
            }
            case USERS_TITLE: {
                list.add(entity.getTitle());
                break;
            }
            case USERS_MAIL_CODE: {
                list.add(entity.getMailCode());
                break;
            }
            case USERS_COST_CENTER: {
                list.add(entity.getCostCenter());
                break;
            }
            case USERS_STATUS: {
                list.add(entity.getStatus());
                break;
            }
            case USERS_SECONDARY_STATUS: {
                list.add(entity.getSecondaryStatus());
                break;
            }
            case USERS_BIRTHDATE: {
                list.add(entity.getBirthdate());
                break;
            }
            case USERS_SEX: {
                list.add(entity.getSex());
                break;
            }
            case USERS_CREATE_DATE: {
                list.add(entity.getCreateDate());
                break;
            }
            case USERS_CREATED_BY: {
                list.add(entity.getCreatedBy());
                break;
            }
            case USERS_LAST_UPDATE: {
                list.add(entity.getLastUpdate());
                break;
            }
            case USERS_LAST_UPDATED_BY: {
                list.add(entity.getLastUpdatedBy());
                break;
            }
            case USERS_PREFIX: {
                list.add(entity.getPrefix());
                break;
            }
            case USERS_SUFFIX: {
                list.add(entity.getSuffix());
                break;
            }
            case USERS_USER_TYPE_IND: {
                list.add(entity.getUserTypeInd());
                break;
            }
            case USERS_EMPLOYEE_ID: {
                list.add(entity.getEmployeeId());
                break;
            }
            case USERS_EMPLOYEE_TYPE: {
                list.add(entity.getEmployeeType());
                break;
            }
            case USERS_LOCATION_CD: {
                list.add(entity.getLocationCd());
                break;
            }
            case USERS_LOCATION_NAME: {
                list.add(entity.getLocationName());
                break;
            }
            case USERS_COMPANY_OWNER_ID: {
                list.add(entity.getCompanyOwnerId());
                break;
            }
            case USERS_JOB_CODE: {
                list.add(entity.getJobCode());
                break;
            }
            case USERS_ALTERNATE_ID: {
                list.add(entity.getAlternateContactId());
                break;
            }
            case USERS_START_DATE: {
                list.add(entity.getStartDate());
                break;
            }
            case USERS_LAST_DATE: {
                list.add(entity.getLastDate());
                break;
            }
            case USERS_MAIDEN_NAME: {
                list.add(entity.getMaidenName());
                break;
            }
            case USERS_NICKNAME: {
                list.add(entity.getNickname());
                break;
            }
            case USERS_PASSWORD_THEME: {
                list.add(entity.getPasswordTheme());
                break;
            }
            case USERS_SHOW_IN_SEARCH: {
                list.add(entity.getShowInSearch());
                break;
            }
            case USERS_USER_OWNER_ID: {
                list.add(entity.getUserOwnerId());
                break;
            }
            case USERS_DATE_PASSWORD_CHANGED: {
                list.add(entity.getDatePasswordChanged());
                break;
            }
            case USERS_DATE_CHALLENGE_RESP_CHANGED: {
                list.add(entity.getDateChallengeRespChanged());
                break;
            }
            case USERS_SYSTEM_FLAG: {
                list.add(entity.getSystemFlag());
                break;
            }
            case USERS_DATE_IT_POLICY_APPROVED: {
                list.add(entity.getDateITPolicyApproved());
                break;
            }
            case USERS_CLAIM_DATE: {
                list.add(entity.getClaimDate());
                break;
            }
            case USERS_RESET_PASSWORD_TYPE: {
                list.add(entity.getResetPasswordType());
                break;
            }
            case USERS_LASTNAME_PREFIX: {
                list.add(entity.getPrefixLastName());
                break;
            }
            case USERS_SUB_TYPE_ID: {
                list.add(entity.getSubType().getId());
                break;
            }
            case USERS_PARTNER_NAME: {
                list.add(entity.getPartnerName());
                break;
            }
            case USERS_PREFIX_PARTNER_NAME: {
                list.add(entity.getPrefixPartnerName());
                break;
            }

            default:
                break;
        }

    }
}
