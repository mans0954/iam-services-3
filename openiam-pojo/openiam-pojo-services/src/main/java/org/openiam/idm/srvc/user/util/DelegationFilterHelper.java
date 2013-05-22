package org.openiam.idm.srvc.user.util;

import org.openiam.idm.srvc.user.domain.UserAttributeEntity;

import java.util.*;

public class DelegationFilterHelper {
    private static final String DLG_FLT_APP="DLG_FLT_APP";
    private static final String DLG_FLT_DEPT="DLG_FLT_DEPT";
    private static final String DLG_FLT_DIV="DLG_FLT_DIV";
    private static final String DLG_FLT_GRP="DLG_FLT_GRP";
    private static final String DLG_FLT_ORG="DLG_FLT_ORG";
    private static final String DLG_FLT_ROLE="DLG_FLT_ROLE";

    private static final String DLG_FLT_SEPARATOR=",";


    public static  boolean isAllowed(String pk,  Set<String> filterData){
        return filterData==null || filterData.isEmpty() || filterData.contains(pk);
    }

    public static List<String> getOrgIdFilterFromString(Map<String, UserAttributeEntity> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_ORG);
    }

    public static boolean isOrgFilterSet(Map<String, UserAttributeEntity> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_ORG);
    }

    public static List<String> getDeptFilterFromString(Map<String, UserAttributeEntity> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_DEPT);
    }
    public static boolean isDeptFilterSet(Map<String, UserAttributeEntity> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_DEPT);
    }

    public static List<String> getDivisionFilterFromString(Map<String, UserAttributeEntity> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_DIV);
    }
    public static boolean isDivisionFilterSet(Map<String, UserAttributeEntity> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_DIV);
    }

    public static List<String> getRoleFilterFromString(Map<String, UserAttributeEntity> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_ROLE);
    }
    public static boolean isRoleFilterSet(Map<String, UserAttributeEntity> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_ROLE);
    }

    public static List<String> getGroupFilterFromString(Map<String, UserAttributeEntity> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_GRP);
    }
    public static boolean isGroupFilterSet(Map<String, UserAttributeEntity> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_GRP);
    }

    public static List<String> getAPPFilterFromString(Map<String, UserAttributeEntity> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_APP);
    }
    public static boolean isAPPFilterSet(Map<String, UserAttributeEntity> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_APP);
    }

    private static boolean isFilerSet(Map<String, UserAttributeEntity> attrMap, String key){
        boolean result = false;
        if(attrMap!=null){
            if (attrMap.get(key) != null)  {
                String filter =  attrMap.get(key).getValue();
                result = (filter != null && !filter.trim().isEmpty());
            }
        }
        return result;
    }


    private static  List<String> getFilterListFromString(Map<String, UserAttributeEntity> attrMap, String key){
        List<String> filterLst = new ArrayList<String>();
        if(attrMap!=null){
            if (attrMap.get(key) != null)  {
                String filter =  attrMap.get(key).getValue();
                if (filter != null && !filter.trim().isEmpty()) {
                    StringTokenizer tokenizer = new StringTokenizer(filter, DLG_FLT_SEPARATOR);
                    while ( tokenizer.hasMoreTokens()) {
                        filterLst.add(tokenizer.nextToken());
                    }
                }
            }
        }
        return filterLst;
    }
}
