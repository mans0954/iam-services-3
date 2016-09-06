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
                    userEntity.setType(this.getMetadataType(value));
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
                    userEntity.setEmployeeType(this.getMetadataType(value));
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
                    userEntity.setJobCode(this.getMetadataType(value));
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
                case USERS_DATE_IT_POLICY_APPROVED:
                    userEntity.setDateITPolicyApproved(Utils.getDate(value));
                    break;
                case USERS_CLAIM_DATE:
                    userEntity.setClaimDate(Utils.getDate(value));
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
        } else {
            System.out.println("Empty KEy");
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
                this.putStringToList(list, column, entity.getFirstName(), 50);
                break;
            }
            case USERS_LAST_NAME: {
                this.putStringToList(list, column, entity.getLastName(), 50);
                break;
            }
            case USERS_MIDDLE_INIT: {
                this.putStringToList(list, column, entity.getMiddleInit(), 50);
                break;
            }
            case USERS_TYPE_ID: {
                if (entity.getType() != null)
                    this.putStringToList(list, column, entity.getType().getId(), 32);
                else
                    list.add(null);
                break;
            }
            case USERS_CLASSIFICATION: {
                this.putStringToList(list, column, entity.getClassification(), 20);
                break;
            }
            case USERS_TITLE: {
                this.putStringToList(list, column, entity.getTitle(), 100);
                break;
            }
            case USERS_MAIL_CODE: {
                this.putStringToList(list, column, entity.getMailCode(), 100);
                break;
            }
            case USERS_COST_CENTER: {
                this.putStringToList(list, column, entity.getCostCenter(), 100);
                break;
            }
            case USERS_STATUS: {
                if (entity.getStatus() != null)
                    list.add(entity.getStatus().getValue());
                else
                    list.add(null);
                break;
            }
            case USERS_SECONDARY_STATUS: {
                if (entity.getSecondaryStatus() != null)
                    list.add(entity.getSecondaryStatus().getValue());
                else
                    list.add(null);
                break;
            }
            case USERS_BIRTHDATE: {
                if (entity.getBirthdate() != null) {
                    list.add(entity.getBirthdate());
                } else {
                    list.add(null);
                }
                break;
            }
            case USERS_SEX: {
                this.putStringToList(list, column, entity.getSex(), 1);
                break;
            }
            case USERS_CREATE_DATE: {
                list.add(entity.getCreateDate());
                break;
            }
            case USERS_CREATED_BY: {
                this.putStringToList(list, column, entity.getCreatedBy(), 40);
                break;
            }
            case USERS_LAST_UPDATE: {
                list.add(entity.getLastUpdate());
                break;
            }
            case USERS_LAST_UPDATED_BY: {
                this.putStringToList(list, column, entity.getLastUpdatedBy(), 40);
                break;
            }
            case USERS_PREFIX: {
                this.putStringToList(list, column, entity.getPrefix(), 4);
                break;
            }
            case USERS_SUFFIX: {
                this.putStringToList(list, column, entity.getSuffix(), 20);
                break;
            }
            case USERS_USER_TYPE_IND: {
                this.putStringToList(list, column, entity.getUserTypeInd(), 20);
                break;
            }
            case USERS_EMPLOYEE_ID: {
                this.putStringToList(list, column, entity.getEmployeeId(), 100);
                break;
            }
            case USERS_EMPLOYEE_TYPE: {
                if (entity.getEmployeeType() != null)
                    list.add(entity.getEmployeeType().getId());
                else list.add(null);
                break;
            }
            case USERS_LOCATION_CD: {
                this.putStringToList(list, column, entity.getLocationCd(), 50);
                break;
            }
            case USERS_LOCATION_NAME: {
                this.putStringToList(list, column, entity.getLocationName(), 100);
                break;
            }
            case USERS_COMPANY_OWNER_ID: {
                this.putStringToList(list, column, entity.getCompanyOwnerId(), 32);
                break;
            }
            case USERS_JOB_CODE: {
                if (entity.getJobCode() != null) {
                    list.add(entity.getJobCode().getId());
                } else {
                    list.add(null);
                }
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
                this.putStringToList(list, column, entity.getMaidenName(), 40);
                break;
            }
            case USERS_NICKNAME: {
                this.putStringToList(list, column, entity.getNickname(), 100);
                break;
            }
            case USERS_PASSWORD_THEME: {
                this.putStringToList(list, column, entity.getPasswordTheme(), 20);
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
            case USERS_DATE_IT_POLICY_APPROVED: {
                list.add(entity.getDateITPolicyApproved());
                break;
            }
            case USERS_CLAIM_DATE: {
                list.add(entity.getClaimDate());
                break;
            }
            case USERS_LASTNAME_PREFIX: {
                this.putStringToList(list, column, entity.getPrefixLastName(), 10);
                break;
            }
            case USERS_SUB_TYPE_ID: {
                if (entity.getSubType() != null)
                    list.add(entity.getSubType().getId());
                else
                    list.add(null);
                break;
            }
            case USERS_PARTNER_NAME: {
                this.putStringToList(list, column, entity.getPartnerName(), 60);
                break;
            }
            case USERS_PREFIX_PARTNER_NAME: {
                this.putStringToList(list, column, entity.getPrefixPartnerName(), 10);
                break;
            }
            default:
                break;
        }

    }
}
