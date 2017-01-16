package org.openiam.constants;
/**
 * 
 * @author Zdenko Imrek
 *
 */
public class ProvisionConnectorConstant {

	public static final String DRIVER_URL="driverUrl";
	public static final String CONNECTION_STRING="connectionString";
	public static final String SEARCH_SCOPE="searchScope";
	public static final String PRIMARY_REPOSITORY="primaryRepository";
	public static final String SECONDARY_REPOSITORY="secondaryRepository";
	public static final String UPDATE_SECONDARY="updateSecondary";
	public static final String HANDLER5="handler5";
	public static final String ADD_HANDLER="addHandler";
	public static final String MODIFY_HANDLER="modifyHandler";
	public static final String DELETE_HANDLER="deleteHandler";
	public static final String PASSWORD_HANDLER="passwordHandler";
	public static final String SUSPEND_HANDLER="suspendHandler";
	public static final String RESUME_HANDLER="resumeHandler";
	public static final String SEARCH_HANDLER="searchHandler";
	public static final String TEST_CONNECTION_HANDLER="testConnectionHandler";
	public static final String RECONCILE_HANDLER="reconcileResourceHandler";
	public static final String ATTRIBUTE_NAMES_HANDLER="attributeNamesHandler";
	public static final String USER_FIELDS="userFields";
	public static final String GROUP_FIELDS="groupFields";

	public static final String[] PROVISION_CONNECTOR_METADATA_CONSTANTS = {
			DRIVER_URL, 
			CONNECTION_STRING, 
			SEARCH_SCOPE, 
			PRIMARY_REPOSITORY, 
			SECONDARY_REPOSITORY, 
			UPDATE_SECONDARY,
			HANDLER5,
			ADD_HANDLER,
			MODIFY_HANDLER,
			DELETE_HANDLER,
			PASSWORD_HANDLER,
			SUSPEND_HANDLER,
			RESUME_HANDLER,
			SEARCH_HANDLER,
			TEST_CONNECTION_HANDLER,
			RECONCILE_HANDLER,
			ATTRIBUTE_NAMES_HANDLER,
			USER_FIELDS,
			GROUP_FIELDS
	};
}
