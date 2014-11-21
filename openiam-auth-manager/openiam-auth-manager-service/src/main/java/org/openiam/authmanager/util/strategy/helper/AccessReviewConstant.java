package org.openiam.authmanager.util.strategy.helper;

/**
 * Created by: Alexander Duckardt
 * Date: 5/27/14.
 */
public class AccessReviewConstant {
    public static final int INITIAL_LEVEL = 0;

    public static final int SHOW_ALL = 0;
    public static final int SHOW_ROLE_ONLY = 1;
    public static final int SHOW_GROUP_ONLY = 2;
    public static final int SHOW_MNGSYS_ONLY = 4;


    public static final int NAME_FILTER_SET = 1;
    public static final int DESCRIPTION_FILTER_SET = 2;
    public static final int RISK_FILTER_SET = 4;
    public static final int SHOW_EXCEPTIONS_FILTER_SET = 8;

    public static final String ROLE_TYPE="role";
    public static final String GROUP_TYPE="group";
    public static final String RESOURCE_TYPE="resource";

    public static final String ROLE_ICON_DESCR="Role";
    public static final String GROUP_ICON_DESCR="Group";


}
