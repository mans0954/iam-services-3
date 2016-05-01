package org.openiam.imprt.jdbc.parser.impl;

import org.openiam.am.srvc.constants.SearchScopeType;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by alexander on 29/04/16.
 */
public class SyncConfigEntityParser extends BaseParser<SynchConfigEntity>  {
    @Override
    protected void init() throws Exception {

    }

    @Override
    void finish() {

    }

    @Override
    protected ImportPropertiesKey getPrimaryKeyName() {
        return ImportPropertiesKey.SYNCH_CONFIG_ID;
    }

    @Override
    protected ImportPropertiesKey getTableName() {
        return ImportPropertiesKey.SYNCH_CONFIG;
    }

    @Override
    protected Class<SynchConfigEntity> getClazz() {
        return SynchConfigEntity.class;
    }

    @Override
    protected void parseToEntry(SynchConfigEntity synchConfigEntity, ImportPropertiesKey key, String value) throws Exception {
        switch (key) {
            case SYNCH_CONFIG_ID: {
                synchConfigEntity.setSynchConfigId(value);
                break;
            }
            case SYNCH_CONFIG_NAME: {
                synchConfigEntity.setName(value);
                break;
            }
            case SYNCH_CONFIG_STATUS: {
                synchConfigEntity.setStatus(value);
                break;
            }
            case SYNCH_CONFIG_SYNCH_SRC: {
                synchConfigEntity.setSynchAdapter(value);
                break;
            }
            case SYNCH_CONFIG_FILE_NAME: {
                synchConfigEntity.setFileName(value);
                break;
            }
            case SYNCH_CONFIG_SRC_LOGIN_ID: {
                synchConfigEntity.setSrcLoginId(value);
                break;
            }
            case SYNCH_CONFIG_SRC_PASSWORD: {
                synchConfigEntity.setSrcPassword(value);
                break;
            }
            case SYNCH_CONFIG_SRC_HOST: {
                synchConfigEntity.setSrcHost(value);
                break;
            }
            case SYNCH_CONFIG_DRIVER: {
                synchConfigEntity.setDriver(value);
                break;
            }
            case SYNCH_CONFIG_CONNECTION_URL: {
                synchConfigEntity.setConnectionUrl(value);
                break;
            }
            case SYNCH_CONFIG_QUERY: {
                synchConfigEntity.setQuery(value);
                break;
            }
            case SYNCH_CONFIG_QUERY_TIME_FIELD: {
                synchConfigEntity.setQueryTimeField(value);
                break;
            }
            case SYNCH_CONFIG_BASE_DN: {
                synchConfigEntity.setBaseDn(value);
                break;
            }
            case SYNCH_CONFIG_LAST_EXEC_TIME: {
                synchConfigEntity.setLastExecTime(Utils.getDate(value));
                break;
            }
            case SYNCH_CONFIG_LAST_REC_PROCESSED: {
                synchConfigEntity.setLastRecProcessed(value);
                break;
            }
            case SYNCH_CONFIG_MANAGED_SYS_ID: {
                synchConfigEntity.setManagedSysId(value);
                break;
            }
            case SYNCH_CONFIG_LOAD_MATCH_ONLY: {
                synchConfigEntity.setLoadMatchOnly(Integer.valueOf(value));
                break;
            }
            case SYNCH_CONFIG_UPDATE_ATTRIBUTE: {
                synchConfigEntity.setUpdateAttribute(Integer.valueOf(value));
                break;
            }
            case SYNCH_CONFIG_SYNCH_FREQUENCY: {
                synchConfigEntity.setSynchFrequency(value);
                break;
            }
            case SYNCH_CONFIG_SYNCH_TYPE: {
                synchConfigEntity.setSynchType(value);
                break;
            }

            case SYNCH_CONFIG_PROCESS_RULE: {
                synchConfigEntity.setProcessRule(value);
                break;
            }
            case SYNCH_CONFIG_VALIDATION_RULE: {
                synchConfigEntity.setValidationRule(value);
                break;
            }
            case SYNCH_CONFIG_TRANSFORMATION_RULE: {
                synchConfigEntity.setTransformationRule(value);
                break;
            }
            case SYNCH_CONFIG_MATCH_FIELD_NAME: {
                synchConfigEntity.setMatchFieldName(value);
                break;
            }
            case SYNCH_CONFIG_MATCH_MANAGED_SYS_ID: {
                synchConfigEntity.setManagedSysId(value);
                break;
            }
            case SYNCH_CONFIG_MATCH_SRC_FIELD_NAME: {
                synchConfigEntity.setMatchSrcFieldName(value);
                break;
            }
            case SYNCH_CONFIG_CUSTOM_MATCH_RULE: {
                synchConfigEntity.setCustomMatchRule(value);
                break;
            }
            case SYNCH_CONFIG_CUSTOM_ADAPTER_SCRIPT: {
                synchConfigEntity.setCustomAdatperScript(value);
                break;
            }
            case SYNCH_CONFIG_CUSTOM_MATCH_ATTR: {
                synchConfigEntity.setCustomMatchAttr(value);
                break;
            }
            case SYNCH_CONFIG_WS_URL: {
                synchConfigEntity.setWsUrl(value);
                break;
            }
            case SYNCH_CONFIG_USE_POLICY_MAP: {
                synchConfigEntity.setUsePolicyMap((value.equals("Y")?true:false));
                break;
            }
            case SYNCH_CONFIG_USE_TRANSFORM_SCRIPT: {
                synchConfigEntity.setUseTransformationScript((value.equals("Y")?true:false));
                break;
            }
            case SYNCH_CONFIG_POLICY_MAP_BEFORE_TRANSFORM: {
                synchConfigEntity.setPolicyMapBeforeTransformation((value.equals("Y")?true:false));
                break;
            }
            case SYNCH_CONFIG_USE_SYSTEM_PATH: {
                synchConfigEntity.setUseSystemPath((value.equals("Y")?true:false));
                break;
            }
            case SYNCH_CONFIG_PRE_SYNC_SCRIPT: {
                synchConfigEntity.setPreSyncScript(value);
                break;
            }
            case SYNCH_CONFIG_POST_SYNC_SCRIPT: {
                synchConfigEntity.setPostSyncScript(value);
                break;
            }
            case SYNCH_CONFIG_COMPANY_ID: {
                synchConfigEntity.setCompanyId(value);
                break;
            }
            case SYNCH_CONFIG_ATTRIBUTE_NAMES_LOOKUP: {
                synchConfigEntity.setAttributeNamesLookup(value);
                break;
            }
            case SYNCH_CONFIG_SEARCH_SCOPE: {
                synchConfigEntity.setSearchScope(SearchScopeType.values()[Integer.valueOf(value)] );
                break;
            }
            case SYNCH_CONFIG_WS_URI: {
                synchConfigEntity.setWsUri(value);
                break;
            }
            case SYNCH_CONFIG_WS_NAME_SPACE: {
                synchConfigEntity.setWsNameSpace(value);
                break;
            }
            case SYNCH_CONFIG_WS_OPERATION: {
                synchConfigEntity.setWsOperation(value);
                break;
            }
            case SYNCH_CONFIG_WS_TARGET_ENTITY_PATH: {
                synchConfigEntity.setWsTargetEntityPath(value);
                break;
            }
            case SYNCH_CONFIG_WS_ATTRIBUTES_STRING: {
                synchConfigEntity.setWsAttributes(value);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void parseToList(List<Object> list, ImportPropertiesKey column, SynchConfigEntity entity) {
        switch (column) {
            case SYNCH_CONFIG_ID: {
                list.add(entity.getSynchConfigId());
                break;
            }
            case SYNCH_CONFIG_NAME: {
                list.add(entity.getName());
                break;
            }
            case SYNCH_CONFIG_STATUS: {
                list.add(entity.getStatus());
                break;
            }
            case SYNCH_CONFIG_SYNCH_SRC: {
                list.add(entity.getSynchAdapter());
                break;
            }
            case SYNCH_CONFIG_FILE_NAME: {
                list.add(entity.getFileName());
                break;
            }
            case SYNCH_CONFIG_SRC_LOGIN_ID: {
                list.add(entity.getSrcLoginId());
                break;
            }
            case SYNCH_CONFIG_SRC_PASSWORD: {
                list.add(entity.getSrcPassword());
                break;
            }
            case SYNCH_CONFIG_SRC_HOST: {
                list.add(entity.getSrcHost());
                break;
            }
            case SYNCH_CONFIG_DRIVER: {
                list.add(entity.getDriver());
                break;
            }
            case SYNCH_CONFIG_CONNECTION_URL: {
                list.add(entity.getConnectionUrl());
                break;
            }
            case SYNCH_CONFIG_QUERY: {
                list.add(entity.getQuery());
                break;
            }
            case SYNCH_CONFIG_QUERY_TIME_FIELD: {
                list.add(entity.getQueryTimeField());
                break;
            }
            case SYNCH_CONFIG_BASE_DN: {
                list.add(entity.getBaseDn());
                break;
            }
            case SYNCH_CONFIG_LAST_EXEC_TIME: {
                list.add(entity.getLastExecTime());
                break;
            }
            case SYNCH_CONFIG_LAST_REC_PROCESSED: {
                list.add(entity.getLastRecProcessed());
                break;
            }
            case SYNCH_CONFIG_MANAGED_SYS_ID: {
                list.add(entity.getManagedSysId());
                break;
            }
            case SYNCH_CONFIG_LOAD_MATCH_ONLY: {
                list.add(entity.getLoadMatchOnly());
                break;
            }
            case SYNCH_CONFIG_UPDATE_ATTRIBUTE: {
                list.add(entity.getUpdateAttribute());
                break;
            }
            case SYNCH_CONFIG_SYNCH_FREQUENCY: {
                list.add(entity.getSynchFrequency());
                break;
            }
            case SYNCH_CONFIG_SYNCH_TYPE: {
                list.add(entity.getSynchType());
                break;
            }
            case SYNCH_CONFIG_PROCESS_RULE: {
                list.add(entity.getProcessRule());
                break;
            }
            case SYNCH_CONFIG_VALIDATION_RULE: {
                list.add(entity.getValidationRule());
                break;
            }
            case SYNCH_CONFIG_TRANSFORMATION_RULE: {
                list.add(entity.getTransformationRule());
                break;
            }
            case SYNCH_CONFIG_MATCH_FIELD_NAME: {
                list.add(entity.getMatchFieldName());
                break;
            }
            case SYNCH_CONFIG_MATCH_MANAGED_SYS_ID: {
                list.add(entity.getMatchManagedSysId());
                break;
            }
            case SYNCH_CONFIG_MATCH_SRC_FIELD_NAME: {
                list.add(entity.getMatchSrcFieldName());
                break;
            }
            case SYNCH_CONFIG_CUSTOM_MATCH_RULE: {
                list.add(entity.getCustomMatchRule());
                break;
            }
            case SYNCH_CONFIG_CUSTOM_ADAPTER_SCRIPT: {
                list.add(entity.getCustomAdatperScript());
                break;
            }
            case SYNCH_CONFIG_CUSTOM_MATCH_ATTR: {
                list.add(entity.getCustomMatchAttr());
                break;
            }
            case SYNCH_CONFIG_WS_URL: {
                list.add(entity.getWsUrl());
                break;
            }
            case SYNCH_CONFIG_USE_POLICY_MAP: {
                list.add((entity.getUsePolicyMap())?"Y":"N");
                break;
            }
            case SYNCH_CONFIG_USE_TRANSFORM_SCRIPT: {
                list.add((entity.getUseTransformationScript())?"Y":"N");
                break;
            }
            case SYNCH_CONFIG_POLICY_MAP_BEFORE_TRANSFORM: {
                list.add((entity.getPolicyMapBeforeTransformation())?"Y":"N");
                break;
            }
            case SYNCH_CONFIG_USE_SYSTEM_PATH: {
                list.add((entity.getUseSystemPath())?"Y":"N");
                break;
            }
            case SYNCH_CONFIG_PRE_SYNC_SCRIPT: {
                list.add(entity.getPreSyncScript());
                break;
            }
            case SYNCH_CONFIG_POST_SYNC_SCRIPT: {
                list.add(entity.getPostSyncScript());
                break;
            }
            case SYNCH_CONFIG_COMPANY_ID: {
                list.add(entity.getCompanyId());
                break;
            }
            case SYNCH_CONFIG_ATTRIBUTE_NAMES_LOOKUP: {
                list.add(entity.getAttributeNamesLookup());
                break;
            }
            case SYNCH_CONFIG_SEARCH_SCOPE: {
                list.add(entity.getSearchScope().ordinal());
                break;
            }
            case SYNCH_CONFIG_WS_URI: {
                list.add(entity.getWsUri());
                break;
            }
            case SYNCH_CONFIG_WS_NAME_SPACE: {
                list.add(entity.getWsNameSpace());
                break;
            }
            case SYNCH_CONFIG_WS_OPERATION: {
                list.add(entity.getWsOperation());
                break;
            }
            case SYNCH_CONFIG_WS_TARGET_ENTITY_PATH: {
                list.add(entity.getWsTargetEntityPath());
                break;
            }
            case SYNCH_CONFIG_WS_ATTRIBUTES_STRING: {
                list.add(entity.getWsAttributes());
                break;
            }
            default:
                break;
        }
    }
}
