package org.openiam.idm.srvc.user.util;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.user.dto.UserAttribute;

import java.util.*;

public class DelegationFilterHelper {
    public static final String DLG_FLT_APP="DLG_FLT_APP";
    public static final String DLG_FLT_DEPT="DLG_FLT_DEPT";
    public static final String DLG_FLT_DIV="DLG_FLT_DIV";
    public static final String DLG_FLT_GRP="DLG_FLT_GRP";
    public static final String DLG_FLT_ORG="DLG_FLT_ORG";
    public static final String DLG_FLT_ROLE="DLG_FLT_ROLE";
    public static final String DLG_FLT_MNG_RPT="DLG_FLT_MNG_RPT";
    public static final String DLG_FLT_USE_ORG_INH="DLG_FLT_USE_ORG_INH";

    private static final String DLG_FLT_SEPARATOR=",";


    public static  boolean isAllowed(String pk,  Set<String> filterData){
        return filterData==null || filterData.isEmpty() || filterData.contains(pk);
    }

    public static List<String> getOrgIdFilterFromString(Map<String, UserAttribute> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_ORG);
    }

    public static boolean isOrgFilterSet(Map<String, UserAttribute> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_ORG);
    }
    public static List<String> getDeptFilterFromString(Map<String, UserAttribute> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_DEPT);
    }
    public static boolean isDeptFilterSet(Map<String, UserAttribute> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_DEPT);
    }

    public static List<String> getDivisionFilterFromString(Map<String, UserAttribute> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_DIV);
    }
    public static boolean isDivisionFilterSet(Map<String, UserAttribute> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_DIV);
    }

    public static List<String> getRoleFilterFromString(Map<String, UserAttribute> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_ROLE);
    }
    public static boolean isRoleFilterSet(Map<String, UserAttribute> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_ROLE);
    }

    public static List<String> getGroupFilterFromString(Map<String, UserAttribute> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_GRP);
    }
    public static boolean isGroupFilterSet(Map<String, UserAttribute> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_GRP);
    }

    public static List<String> getAPPFilterFromString(Map<String, UserAttribute> attrMap) {
        return getFilterListFromString(attrMap, DLG_FLT_APP);
    }
    public static boolean isAPPFilterSet(Map<String, UserAttribute> attrMap) {
        return isFilerSet(attrMap, DLG_FLT_APP);
    }


//    public static List<String> getMngRptFromString(Map<String, UserAttribute> attrMap) {
//        return getFilterListFromString(attrMap, DLG_FLT_MNG_RPT);
//    }

    public static boolean isMngRptFilterSet(Map<String, UserAttribute> attrMap) {
        String filterValue = getFilterValue(attrMap, DLG_FLT_MNG_RPT);
        boolean isFilterSet = false;
        if(StringUtils.isNotEmpty(filterValue)) {
            isFilterSet = new Boolean(filterValue);
        }
        return isFilterSet;
    }

    public static boolean isUseOrgInhFilterSet(Map<String, UserAttribute> attrMap) {
        String filterValue = getFilterValue(attrMap, DLG_FLT_USE_ORG_INH);
        boolean isFilterSet = false;
        if(StringUtils.isNotEmpty(filterValue)) {
            isFilterSet = new Boolean(filterValue);
        }
        return isFilterSet;
    }


    public static String getValueFromList(List<String> values){
        if(values==null)
            return null;
        StringBuilder  buf = new StringBuilder();
        int ctr = 0;
        for (String s : values) {
            if (ctr == 0) {
                buf.append(s);
            }else {
                buf.append(DLG_FLT_SEPARATOR).append(s);
            }
            ctr++;
        }
        return buf.toString();
    }

    public static String[] getFilterTypes(){
        return new String[]{DelegationFilterHelper.DLG_FLT_APP,
        					DelegationFilterHelper.DLG_FLT_DEPT,
                            DelegationFilterHelper.DLG_FLT_DIV,
                            DelegationFilterHelper.DLG_FLT_GRP,
                            DelegationFilterHelper.DLG_FLT_ROLE,
                            DelegationFilterHelper.DLG_FLT_ORG,
                            DelegationFilterHelper.DLG_FLT_MNG_RPT,
                            DelegationFilterHelper.DLG_FLT_USE_ORG_INH};
    }

    private static boolean isFilerSet(Map<String, UserAttribute> attrMap, String key){
        boolean result = false;
        if(attrMap!=null){
            if (attrMap.get(key) != null)  {
                String filter =  attrMap.get(key).getValue();
                result = (filter != null && !filter.trim().isEmpty());
            }
        }
        return result;
    }


    private static  List<String> getFilterListFromString(Map<String, UserAttribute> attrMap, String key){
        List<String> filterLst = new ArrayList<String>();
        String filter = getFilterValue(attrMap, key);
        if(StringUtils.isNotEmpty(filter)) {
            StringTokenizer tokenizer = new StringTokenizer(filter, DLG_FLT_SEPARATOR);
            while ( tokenizer.hasMoreTokens()) {
                filterLst.add(tokenizer.nextToken());
            }
        }
        return filterLst;
    }

    private static String getFilterValue(Map<String, UserAttribute> attrMap, String key){
        String value = null;
        if(attrMap!=null){
            if (attrMap.get(key) != null)  {
                value =  attrMap.get(key).getValue();
            }
        }
        return value;
    }
}
