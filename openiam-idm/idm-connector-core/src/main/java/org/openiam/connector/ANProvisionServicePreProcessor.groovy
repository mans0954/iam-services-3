package org.openiam.connector

import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang.StringUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.openiam.base.AttributeOperationEnum
import org.openiam.base.TreeObjectId
import org.openiam.base.id.UUIDGen
import org.openiam.connector.type.ObjectValue
import org.openiam.connector.type.constant.StatusCodeType
import org.openiam.connector.type.request.LookupRequest
import org.openiam.connector.type.request.SearchRequest
import org.openiam.connector.type.response.SearchResponse
import org.openiam.idm.searchbeans.OrganizationSearchBean
import org.openiam.idm.srvc.auth.dto.Login
import org.openiam.idm.srvc.auth.login.LoginDataService
import org.openiam.idm.srvc.auth.ws.LoginDataWebService
import org.openiam.idm.srvc.continfo.dto.Address
import org.openiam.idm.srvc.continfo.dto.EmailAddress
import org.openiam.idm.srvc.grp.domain.GroupEntity
import org.openiam.idm.srvc.grp.dto.Group
import org.openiam.idm.srvc.grp.service.GroupDataService
import org.openiam.idm.srvc.grp.ws.GroupDataWebService
import org.openiam.idm.srvc.loc.dto.Location
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService
import org.openiam.idm.srvc.msg.service.MailService
import org.openiam.idm.srvc.org.dto.Organization
import org.openiam.idm.srvc.org.dto.OrganizationAttribute
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO
import org.openiam.idm.srvc.org.service.OrganizationDataService
import org.openiam.idm.srvc.res.dto.Resource
import org.openiam.idm.srvc.res.dto.ResourceProp
import org.openiam.idm.srvc.res.service.ResourceDataService
import org.openiam.idm.srvc.role.dto.Role
import org.openiam.idm.srvc.role.ws.RoleDataWebService
import org.openiam.idm.srvc.user.domain.UserAttributeEntity
import org.openiam.idm.srvc.user.domain.UserEntity
import org.openiam.idm.srvc.user.dto.User
import org.openiam.idm.srvc.user.dto.UserAttribute
import org.openiam.idm.srvc.user.dto.UserStatusEnum
import org.openiam.idm.srvc.user.service.UserDAO
import org.openiam.idm.srvc.user.service.UserDataService
import org.openiam.idm.srvc.user.util.DelegationFilterHelper
import org.openiam.idm.srvc.user.ws.UserDataWebService
import org.openiam.provision.dto.PasswordSync
import org.openiam.provision.dto.ProvisionUser
import org.openiam.provision.resp.LookupUserResponse
import org.openiam.provision.service.AbstractProvisionPreProcessor
import org.openiam.provision.service.ConnectorAdapter
import org.openiam.provision.service.ProvisionService
import org.openiam.provision.service.ProvisioningConstants
import org.openiam.provision.type.ExtensibleAttribute
import org.openiam.provision.type.ExtensibleUser
import org.openiam.util.MuleContextProvider
import org.openiam.util.constants.CountryCode
import org.springframework.context.ApplicationContext

import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

public class ANProvisionServicePreProcessor extends AbstractProvisionPreProcessor<ProvisionUser> {
    private final String EXCHANGE_MANSYS_ID = "2c94b25748eaf9ef01492d5312d3026d"
    private final String EXCHANGE_RES_ID = "2c94b25748eaf9ef01492d5312c3026a"
    private final String AD_MANSYS_ID = "DD6CA4CC8BBC4D78A5879D93CEBC8A29"
    private final String OIAM_MANSYS_ID = "0"
    private final String ORG_ROLE_TYPE_ID = "2c94b2574a7c3454014a9608add231df"
    private final String BU_ROLE_TYPE_ID = "2c94b2574bc5f9d0014bdf24d9dc0a61"
    private final String ST_ROLE_TYPE_ID = "2c94b2574bc5f9d0014bdf2976ac0a70"
    private final String GRP_INTUNE_ENABLED = "8a8da035549233be0154a59ca2132037"
    private final String DELEGATION_ROLE_TYPE_ID = "DELEGATION_ROLE_TYPE_ID"
    private final String DELEGATION_GROUP_TYPE_ID = "DELEGATION_GROUP_TYPE_ID"
    private final String MDM_ROLE_ID = "MDM_ROLE_ID"
    private final String HP_ADMIN_ROLE_ID = "HP_ADMIN_ROLE_ID"
    private final String[] formatValues = ["00", "0", "50", "51", "52", "53"];

    private final String g_GSS_MDMEmailWMS_ID = "8a8da03553e864920153f15fab8f145b"
    private final String g_GSS_MDMUsers_ID = "8a8da03553e864920153f1601e0b1486"
    private final String m_Unity_Disabled_Users_exists_ID = "8a8da03553e864920153f15d74001430"

    private final boolean debugMode = true;
    private final String DEFAULT_HOLDING_ROLE_ID = "8a8da02e51a28fd20151a291ce360002"
    private final String GRP_VAULT_CACHE_ENABLED = "8a8da03553a37f850153a38f40680007"
    private final String defaulADPath = 'OU=IAMHolding,OU=Unity,DC=d30,DC=intra'
    private final String DOMAIN_CONTEXT = 'DC=d30,DC=intra'
//   private final String DOMAIN_CONTEXT = 'DC=dev,DC=local'
    List<Group> _userGroups = null
    List<Role> _userRoles = null
    Map<String, UserAttribute> attributesMap;
    Map<String, Set<String>> existedAttributes;
    Organization currectOrganization = null;
    Location currentLocation = null;
    Boolean changeDisplayName = false;

    public void resetLocalFields() {
        currectOrganization = null;
        currentLocation = null;
        _userGroups = null;
        _userRoles = null;
        attributesMap = null;
        existedAttributes = null;
    }

    RoleDataWebService roleWS;

    public int add(ProvisionUser user, Map<String, Object> bindingMap) {

        //we are not creating users from Source adapter now.
/*        if (user.getAttribute("USER_CREATION_SOURCE")?.value?.equalsIgnoreCase("SOURCE_ADAPTER") && "A5".equalsIgnoreCase(user.getAttribute("actionCode")?.value)) {
      return ProvisioningConstants.FAIL;
        }
*/
        roleWS = context.getBean('roleWS') as RoleDataWebService;
        long time2 = System.currentTimeMillis();
        changeDisplayName = true;
        long time1 = System.currentTimeMillis();
        resetLocalFields();
        if (debugMode) {
            println("1.ProvisionServicePreProcessor: AddUser called.");
            println("ProvisionServicePreProcessor: User=" + user.toString());
        }
        //Check should we process this user (users with employeeGroups=E or T should be skipped) - requirement
        String emplGrp = user.getAttribute("employeeGroup")?.value
        if (debugMode) {
            println("Start modify. Check Employee Group=" + emplGrp)
        }
        if ("E".equalsIgnoreCase(emplGrp) || "T".equalsIgnoreCase(emplGrp)) {
            if (debugMode) {
                println("Break provisioning. Employee Group is " + emplGrp)
            }
            sendNotificationBreakProvisioning(user, emplGrp)

            return 0;
        }

        //Start date should be filled always. If not than do current date
        if (!user.startDate) {
            user.startDate = new Date();
        }

        if (isDisable(user.lastDate)) {
            user.secondaryStatus = UserStatusEnum.DISABLED;

        }

        //clean up some fields
        if (user.prefix) {
            user.prefixLastName = user.prefix;
            user.prefix = "";
        }
        if ("null".equalsIgnoreCase(user.prefixLastName)) {
            user.prefixLastName = ""
        }

        if ((user.middleInit == null) || ("null".equalsIgnoreCase(user.middleInit))) {
            user.middleInit = ""
        }

        //employeeType comes as user.userTypeInd, so needs to put to it to user attribute
        if (user.userTypeInd) {
            addAttribute(user, "employeeType", user.userTypeInd, bindingMap);
            user.userTypeInd = null;
        } else {
            //if employeeType doesn't come with new user fill it as 'Employee' - requerements
            addAttribute(user, "employeeType", "Employee", bindingMap);
        }

        //Process maibox based on metadata type
        if (processMetadataType(user, bindingMap)) {
            processMailBox(user, bindingMap);
        }

        if (user.userSubTypeId?.equalsIgnoreCase("PDD") || user.classification?.equalsIgnoreCase("PDD")) {
            addAttribute(user, "PDDAccount", "On", bindingMap);
        }
/** **************************************************/

        def groupWS = context.getBean('groupWS') as GroupDataWebService

        //start check on Login exists by ManadedSys. Situation appears when reconcile
        //depregated  - recon now used now
        def principal = user.getPrimaryPrincipal("0")?.login
        if (principal == null) {
            long t = System.currentTimeMillis();
            principal = createPrimaryPrincipal(user, bindingMap);
            if (debugMode) {
                println("Finish. createPrimaryPrincipal time " + (System.currentTimeMillis() - t));
                println "====== ProvisionServicePreProcessor.createPrimaryPrincipal: " + (principal ?: "FAILED")
            }
            if (!principal) {
                sendError(user,"5. Principal is NULL")
                return ProvisioningConstants.FAIL
            }

            def primaryLogin = new Login(
                    managedSysId: OIAM_MANSYS_ID,
                    login: principal,
                    operation: AttributeOperationEnum.ADD
            )

            if ("off".equalsIgnoreCase(user.getAttribute("changePasswordOnLogon")?.value)) {
                def classification = user.getAttribute("classification")?.value
                def dateTime = new DateTime(new Date())
                def pwdExpDays = "ADM".equalsIgnoreCase(classification) ? 90 : 180
                primaryLogin.pwdExp = dateTime.plusDays(pwdExpDays).toDate()
                primaryLogin.gracePeriod = dateTime.plusDays(pwdExpDays + 1).toDate()
            }

            user.principalList.add(primaryLogin)
        }

        //end check on primary Login exist and new primary Login generation code
        if (principal) {
            def homeDirAttr = user.userAttributes?.get('homeDirectory')
            processUserID(homeDirAttr, principal)
            def terminalServicesProfilePathAttr = user.userAttributes?.get('terminalServicesProfilePath')
            processUserID(terminalServicesProfilePathAttr, principal)
            def terminalServicesHomeDirectoryAttr = user.userAttributes?.get('terminalServicesHomeDirectory')
            processUserID(terminalServicesHomeDirectoryAttr, principal)
        }



        this.provisionToLocation(user, bindingMap, true);
        if (debugMode) {
            println("2.after roles check point. Time=" + (System.currentTimeMillis() - time2) + "ms");
        }

        Role role = getUserRoleById(user, MDM_ROLE_ID)

        // Check MDM_ROLE_ID and remove MDM groups
        if (role != null) {
            Group g_GSS_MDMEmailWMS = groupWS.getGroup(g_GSS_MDMEmailWMS_ID, "3000");
            if (g_GSS_MDMEmailWMS != null) {
                g_GSS_MDMEmailWMS.setOperation(AttributeOperationEnum.ADD)
                user.getGroups().add(g_GSS_MDMEmailWMS)
            }
            if (debugMode) {
                println("===================================== ProvisionPreProcessor modify g_GSS_MDMEmailWMS = " + g_GSS_MDMEmailWMS)
            }
            Group g_GSS_MDMUsers = groupWS.getGroup(g_GSS_MDMUsers_ID, "3000");
            if (g_GSS_MDMUsers != null) {
                g_GSS_MDMUsers.setOperation(AttributeOperationEnum.ADD)
                user.getGroups().add(g_GSS_MDMUsers)
            }
            if (debugMode) {
                println("===================================== ProvisionPreProcessor modify g_GSS_MDMUsers = " + g_GSS_MDMUsers)
            }

        }

        //populate employeeNumber for contractors
        if (user.employeeId == null && user.getPrimaryEmailAddress() && "Contractor".equals(user.getAttribute("employeeType")?.value)) {
            def String email = user.getPrimaryEmailAddress()?.emailAddress
            user.employeeId = user.employeeId ? user.employeeId : email;
        }

        def upn = createUserPrincipalName(user, bindingMap)
        if (!upn) {
            if (debugMode) {
                println "ProvisionServicePreProcessor Error: UserPrincipalName can not be generated for User ${user.firstName} ${user.lastName}"
            }
            sendError(user,"6. UPN is NULL")
            return ProvisioningConstants.FAIL
        }
        def upnAttr = new UserAttribute("UserPrincipalName", upn)
        upnAttr.operation = AttributeOperationEnum.ADD
        user.saveAttribute(upnAttr)

        if (user.alternateContactId) {
            setAssistantDNAttribute(user)
        }

        if (user.getAttribute("USER_CREATION_SOURCE")?.value?.equals("SOURCE_ADAPTER"))
            addAttribute(user, "activeSync", "Off", bindingMap);

        //let it be to check timing

        addRole(user, "LYNC_ROLE_ID");
        println("2. Pre proc add Time=" + (System.currentTimeMillis() - time1) + "ms");
        return ProvisioningConstants.SUCCESS
    }

    public int modify(ProvisionUser user, Map<String, Object> bindingMap) {
        resetLocalFields();
        if (user.status == null) {
            user.status = UserStatusEnum.ACTIVE;
            user.secondaryStatus = null;
        }
        roleWS = context.getBean('roleWS') as RoleDataWebService;
        def groupWS = context.getBean('groupWS') as GroupDataWebService
        def userDataService = context.getBean('userManager') as UserDataService
        long time1 = System.currentTimeMillis();
        ManagedSystemWebService managedSysService = context.getBean('managedSysService') as ManagedSystemWebService
        ResourceDataService dataService = context.getBean('resourceDataService') as ResourceDataService
        //ManagedSysDto mSys = managedSysService.getManagedSys(AD_MANSYS_ID);
        //Resource res = dataService.getResource(mSys.getResourceId(), null);
        def orgManager = context.getBean('orgManager') as OrganizationDataService
        def actionCode = getUserAttributeByName(user, bindingMap, "actionCode")

        addAttribute(user, "IN_ONEHUB", "N", bindingMap)

        /**************processing leavers*****************/

        if (isDisable(user.lastDate)) {
//        if (actionCode && actionCode?.value == "AA") {
            def boolean isContained = false;
            def emailAddress = "AN" + user.getEmployeeId() + ".iamterm@akzonobel.com"
            def userEmailList = userDataService.getEmailAddressDtoList(user.id,true)
            if(userEmailList)
                for (EmailAddress oldEmails : userEmailList) {
                    if (oldEmails.emailAddress?.equalsIgnoreCase(emailAddress))
                        isContained = true;
                    break;
                }
            if (!isContained) {
                EmailAddress address = new EmailAddress();
                address.setEmailAddress(emailAddress)
                Set nonProvRes = new HashSet()
                nonProvRes.add(new String(EXCHANGE_RES_ID))
                user.setNotProvisioninResourcesIds(nonProvRes)
                address.setIsActive(true);
                address.setIsDefault(true);
                if (!userEmailList?.empty){
                    for (EmailAddress oldEmails : userEmailList) {
                        oldEmails.setIsDefault(false)
                        oldEmails.setMetadataTypeId("SECONDARY_EMAIL")
//                        oldEmails.setIsActive(false)
                        oldEmails.operation = AttributeOperationEnum.REPLACE;
                        user.emailAddresses.add(oldEmails);
                    }
                }
                address.setMetadataTypeId("PRIMARY_EMAIL");
                address.operation = AttributeOperationEnum.ADD;
                user.emailAddresses.add(address);
            }
            user.setStatus(UserStatusEnum.LEAVE)
            user.setSecondaryStatus(UserStatusEnum.DISABLED)


            addDisabledAttributes(user, bindingMap);


            String adPath = this.getUserAttributeByName(user, bindingMap, "AD_PATH")?.value;
            if (!adPath){
                adPath =  attributesMap.get("AD_PATH");
            }
            if (!adPath) {
                sendError(user,"11. AD path ${adPath} from User Attributes.")
            }
            if (!updateDn(user, bindingMap, "DISABLED", adPath, orgManager)) {
                sendError(user,"7. update DN fail during DISABLED")
                return ProvisioningConstants.FAIL
            }
            // If De-Provision set m_Unity_Disabled_Users  group GroupId=2c94b2574b451b5f014b45d54d366567
            Group group = groupWS.getGroup(m_Unity_Disabled_Users_exists_ID, "3000");
            boolean groupExists = false;
            List<Group> userGroups = groupWS.getGroupsForUser(user.id, "3000", false, 0, 100);
            for (Group gr : userGroups) {
                if (gr.id.equalsIgnoreCase(m_Unity_Disabled_Users_exists_ID)) {
                    groupExists = true
                    break;
                }
            }
            if (!groupExists) {
                user.addGroup(group)
                if (debugMode) {
                    println("===================================== ProvisionPreProcessor add Group = " + group.getName())
                }
            }
            return ProvisioningConstants.SUCCESS;
        } else if (isProlonged(user)) {
            //make a rehire process
            println "USER SHOULD BE PROLONGED"
            rehire(user)
            addDisabledAttributes(user, bindingMap);
            def adPath = this.getUserAttributeByName(user, bindingMap, "AD_PATH")?.value;
            if (!updateDn(user, bindingMap, "ACTIVE", adPath, orgManager)) {
                sendError(user,"8. update DN fail during ACTIVE")
                return ProvisioningConstants.FAIL
            }
        } else if (user.lastDate) {
            addDisabledAttributes(user, bindingMap)
        }

        /** **********************************************/

        //def dcdn = getResourceProperty(res, "CHANGE_DISPLAY_NAME");
        //changeDisplayName = (!dcdn || "TRUE".equalsIgnoreCase(dcdn)) ? true : false
        if (!changeDisplayName) {
            UserDAO userDAO = context.getBean("userDAO")
            UserEntity curUser = userDAO.findById(user.id)
            if (curUser) {
                //   user.firstName = curUser.firstName
                //   user.lastName = curUser.lastName
                //    user.middleInit = curUser.middleInit
                //   user.prefixLastName = curUser.prefixLastName
                user.nickname = curUser.nickname
                //   user.prefix = curUser.prefix
            }
        }

        def loginID = user.getAttribute("serviceAccountName")?.value?.replaceAll(/\s+/, '')
        def lastName = convertNonAscii(user.lastName?.toLowerCase())?.replaceAll("[^a-z0-9]+", "")
        def firstName = convertNonAscii(user.firstName?.toLowerCase())?.replaceAll("[^a-z0-9]+", "")
        this.fillExistedPropMap(user, loginID, firstName, lastName, bindingMap);
        String emplGrp = user.getAttribute("employeeGroup")?.value
        if (debugMode) {
            println("Start modify. Check Employee Group=[" + emplGrp + "]")
        }
        if ("E".equalsIgnoreCase(emplGrp) || "T".equalsIgnoreCase(emplGrp)) {
            //skip User
            if (debugMode) {
                println("Break provisioning. Employee Group is " + emplGrp)
            }
            sendNotificationBreakProvisioning(user,emplGrp)
            return 0;
        }
        //only one org is allowed!
        if (debugMode) {
            println("Organizations that comes from external=" + user.organizationUserDTOs?.size());
        }
        if (user.organizationUserDTOs) {
            if (user.organizationUserDTOs.find({ it -> it.operation.equals(AttributeOperationEnum.ADD) || it.operation.equals(AttributeOperationEnum.REPLACE) })) {
                for (def oo : user.organizationUserDTOs) {
                    if (debugMode) {
                        println("Org operation=" + oo.operation);
                    }
                    if (AttributeOperationEnum.NO_CHANGE.equals(oo.operation)) {
                        if (debugMode) {
                            println("NO CHANGE DETECTED");
                        }
                        oo.operation = AttributeOperationEnum.DELETE;
                    }
                }
            }
        }

        //only one manager is allowed!
        if (debugMode) {
            println("Managers that comes from external=" + user.superiors?.size());
            for (def oo : user.superiors) {
                println("MANAGER:${oo.id} ${oo.operation}")
            }
        }
        if (user.superiors) {
            if (user.superiors.find({ it -> it.operation.equals(AttributeOperationEnum.ADD) || it.operation.equals(AttributeOperationEnum.REPLACE) })) {
                for (def oo : user.superiors) {
                    if (debugMode) {
                        println("Manager's operation=" + oo.operation);
                    }
                    if (AttributeOperationEnum.NO_CHANGE.equals(oo.operation)) {
                        if (debugMode) {
                            println("NO CHANGE DETECTED"); oo.operation = AttributeOperationEnum.DELETE;
                        }
                    }
                }
            }
        }

        if (user.prefix) {
            user.prefixLastName = user.prefix;
            user.prefix = null;
        }

        if ("null".equalsIgnoreCase(user.prefixLastName)) {
            user.prefixLastName = ""
        }
        if ("null".equalsIgnoreCase(user.middleInit)) {
            user.middleInit = ""
        }

        if (user.userTypeInd) {
            addAttribute(user, "employeeType", user.userTypeInd, bindingMap);
            user.userTypeInd = null;
        }

        if (processMetadataType(user, bindingMap)) {
            processMailBox(user, bindingMap);
        }

/********************Dummy Emails ****************/
        if (debugMode) {
            println("MY EMPLOYEE TYPE=" + this.getUserAttributeByName(user, bindingMap, "employeeType")?.value)
        }
        boolean isEmployeeFlag = "employee".equalsIgnoreCase(this.getUserAttributeByName(user, bindingMap, "employeeType")?.value);

        if (!isEmployeeFlag) {
            addAttribute(user, "ORG_SBU_SHORT_NAME", "null", bindingMap)
        }

        boolean isPDDorPOS = "PDD".equalsIgnoreCase(user.userSubTypeId) || "POS".equalsIgnoreCase(user.userSubTypeId) || "PDD".equalsIgnoreCase(user.classification) || "POS".equalsIgnoreCase(user.classification);
        boolean isEmployeeNumber = user.employeeId != null;
        String mailboxValue = this.getUserAttributeByName(user, bindingMap, "mailbox")?.value;
        boolean isMailBoxNotPresented = mailboxValue == null || "".equalsIgnoreCase(mailboxValue) || "NONE".equalsIgnoreCase(mailboxValue);
        def userEmailList = userDataService.getEmailAddressDtoList(user.id,false)
        boolean isEmptyEmailsList = CollectionUtils.isEmpty(userEmailList);
        if (isEmptyEmailsList && isEmployeeNumber && (isPDDorPOS || (isEmployeeFlag && !isMailBoxNotPresented))) {
            EmailAddress address = new EmailAddress();
            address.setEmailAddress("AN" + user.getEmployeeId() + ".nomail@akzonobel.com")
            Set nonProvRes = new HashSet()
            nonProvRes.add(new String(EXCHANGE_RES_ID))
            user.setNotProvisioninResourcesIds(nonProvRes)
            address.setIsActive(true);
            address.setIsDefault(true);
            address.setMetadataTypeId("PRIMARY_EMAIL");
            address.operation = AttributeOperationEnum.ADD;
            user.emailAddresses = new HashSet<EmailAddress>();
            user.emailAddresses.add(address);
        }

/******************************** set P&DD account checkbox **************************/
        if ("PDD".equalsIgnoreCase(user.userSubTypeId) || "PDD".equalsIgnoreCase(user.classification)) {
            addAttribute(user, "PDDAccount", "On", bindingMap);
        }
/** **************************************************/

        // context to look up spring beans

        // find user emails (consider deleted and new emails)
        def emailList = userDataService.getEmailAddressDtoList(user.id, false)
        if (debugMode) {
            println("===================================== ProvisionPreProcessor modify emailList before delete = " + emailList)
        }
        def emailIds = []
        if (emailList) {
            emailIds = emailList.collect { e -> e.emailId }
        }


        def emailsToDelete = []
        user.emailAddresses?.each { em ->
            if (!em.emailId && em.operation == AttributeOperationEnum.ADD) {
                emailList << em
            } else if (em.emailId in emailIds && em.operation == AttributeOperationEnum.DELETE) {
                emailsToDelete << emailList.find { e -> e.emailId == em.emailId }
            }
        }
        emailList.removeAll(emailsToDelete)
        def emailAddresses = emailList?.collect { it -> it.emailAddress } as String[]



        if (debugMode) {
            println("ProvisionServicePreProcessor: ModifyUser called.");
            println("ProvisionServicePreProcessor: User=" + user.toString());
        }

        showBindingMap(bindingMap);


        if ("A9".equalsIgnoreCase(actionCode?.value) && actionCode) {

            if (user.lastDate)
                user.lastDate = null
            rehire(user)
            removeAttribute(user, bindingMap, "DEACTIVATION_DATE")
            removeAttribute(user, bindingMap, "userEditActionInfo")
        }


        if (user.status != UserStatusEnum.DELETED) {
            if (debugMode) {
                println("ProvisionServicePreProcessor: No DeleteUser called.");
            }
            def principal = user.getPrimaryPrincipal('0')?.login
            if (!principal) {
                def loginManager = context.getBean("loginManager") as LoginDataService
                principal = loginManager.getByUserIdManagedSys(user.id, '0')?.login
            }
            this.provisionToLocation(user, bindingMap, false);

            boolean disableGrpExist = false;
            for (Group gr : user.getGroups()) {
                if (m_Unity_Disabled_Users_exists_ID.equals(gr.getId())) {

                    disableGrpExist = true;
                    break;
                }
            }
            if (disableGrpExist) {
                Group grp = groupWS.getGroup(m_Unity_Disabled_Users_exists_ID, "3000")
//user.markGroupAsDeleted(m_Unity_Disabled_Users_exists_ID)
                grp.setOperation(AttributeOperationEnum.DELETE)
            } else {
                Group grp = groupWS.getGroup(m_Unity_Disabled_Users_exists_ID, "3000")
                if (grp) {
                    grp.operation = AttributeOperationEnum.DELETE
                    user.groups << grp
                }
            }

            // Check MDM_ROLE_ID and remove MDM groups
            for (Role r : getUserRoles(user.id)) {
                if (MDM_ROLE_ID.equalsIgnoreCase(r.id)) {
                    Group g_GSS_MDMEmailWMS = groupWS.getGroup(g_GSS_MDMEmailWMS_ID, "3000");
                    if (g_GSS_MDMEmailWMS != null) {
                        g_GSS_MDMEmailWMS.setOperation(AttributeOperationEnum.ADD)
                        user.getGroups().add(g_GSS_MDMEmailWMS)
                    }
                    if (debugMode) {
                        println("===================================== ProvisionPreProcessor modify g_GSS_MDMEmailWMS = " + g_GSS_MDMEmailWMS)
                    }
                    Group g_GSS_MDMUsers = groupWS.getGroup(g_GSS_MDMUsers_ID, "3000");
                    if (g_GSS_MDMUsers != null) {
                        g_GSS_MDMUsers.setOperation(AttributeOperationEnum.ADD)
                        user.getGroups().add(g_GSS_MDMUsers)
                    }
                    if (debugMode) {
                        println("===================================== ProvisionPreProcessor modify g_GSS_MDMUsers = " + g_GSS_MDMUsers)
                    }
                    break;
                }
            }

            if (debugMode) {
                println("===================================== ProvisionPreProcessor modify emailAddresses = " + emailAddresses)
            }
            if (emailAddresses) {
                def mdmRole = user.roles?.find { r -> MDM_ROLE_ID.equals(r.id) }
                if (mdmRole) {
                    if (mdmRole.operation == AttributeOperationEnum.ADD) {
                        sendMDMActivated(user, emailAddresses)
                    } else if (mdmRole.operation == AttributeOperationEnum.DELETE) {
                        sendMDMDEActivated(user, emailAddresses)
                    }
                }
            }
            if (debugMode) {
                println("===================================== ProvisionPreProcessor modify emailAddresses finished ")
            }

            def homeDirAttr = user.userAttributes?.get('homeDirectory')
            processUserID(homeDirAttr, principal)

            def terminalServicesProfilePathAttr = user.userAttributes?.get('terminalServicesProfilePath')
            processUserID(terminalServicesProfilePathAttr, principal)

            def terminalServicesHomeDirectoryAttr = user.userAttributes?.get('terminalServicesHomeDirectory')
            processUserID(terminalServicesHomeDirectoryAttr, principal)

            if (user.alternateContactId) {
                setAssistantDNAttribute(user)
            }


        } else {
            // context to look up spring beans

            if (debugMode) {
                println("ProvisionServicePreProcessor: DeleteUser called.");
                println("ProvisionServicePreProcessor: User=" + user.toString());
            }

            showBindingMap(bindingMap);
            // If De-Provision set m_Unity_Disabled_Users  group GroupId=2c94b2574b451b5f014b45d54d366567
            Group group = groupWS.getGroup(m_Unity_Disabled_Users_exists_ID, "3000");
            boolean groupExists = false;
            List<Group> userGroups = groupWS.getGroupsForUser(user.id, "3000", false, 0, 100);
            for (Group gr : userGroups) {
                if (gr.id.equalsIgnoreCase(m_Unity_Disabled_Users_exists_ID)) {
                    groupExists = true
                    break;
                }
            }
            if (!groupExists) {
                user.addGroup(group)
                if (debugMode) {
                    println("===================================== ProvisionPreProcessor add Group = " + group.getName())
                }
            }

            // Check MDM_ROLE_ID and remove MDM groups
            for (Role r : getUserRoles(user.id)) {
                if (MDM_ROLE_ID.equalsIgnoreCase(r.id)) {

                    Group g_GSS_MDMEmailWMS = groupWS.getGroup(g_GSS_MDMEmailWMS_ID, "3000");
                    if (g_GSS_MDMEmailWMS != null) {
                        g_GSS_MDMEmailWMS.setOperation(AttributeOperationEnum.DELETE)
                        user.getGroups().add(g_GSS_MDMEmailWMS)
                        user.markGroupAsDeleted(g_GSS_MDMEmailWMS_ID)
                    }
                    if (debugMode) {
                        println("===================================== ProvisionPreProcessor modify g_GSS_MDMEmailWMS = " + g_GSS_MDMEmailWMS)
                    }

                    Group g_GSS_MDMUsers = groupWS.getGroup(g_GSS_MDMUsers_ID, "3000");
                    if (g_GSS_MDMUsers != null) {
                        g_GSS_MDMUsers.setOperation(AttributeOperationEnum.DELETE)
                        user.getGroups().add(g_GSS_MDMUsers)
                        user.markGroupAsDeleted(g_GSS_MDMUsers_ID)
                    }
                    if (debugMode) {
                        println("===================================== ProvisionPreProcessor modify g_GSS_MDMUsers = " + g_GSS_MDMUsers)
                    }
                    break;
                }
            }

            addDisabledAttributes(user, bindingMap)
        }
        //let it be to check timing
        println("Modify Time=" + (System.currentTimeMillis() - time1) + "ms");

        return ProvisioningConstants.SUCCESS;

    }

    public Map parseADPath(ProvisionUser user, Map<String, Object> bindingMap, OrganizationDataService orgManager) {

        String siteCode = getUserAttributeByName(user, bindingMap, "siteCode")?.value;
        String bu = this.getCurrentOrganization(orgManager, user, siteCode)?.internalOrgId; ;
        String department = getUserAttributeByName(user, bindingMap, "department")?.value;
        return [company   : bu,
                siteCode  : siteCode,
                department: department]
    }

    private Boolean updateDn(ProvisionUser user, Map<String, Object> bindingMap, String AD_PATH, OrganizationDataService orgManager) {
        updateDn(user, bindingMap, null, AD_PATH, orgManager)
    }

    private Boolean updateDn(ProvisionUser user, Map<String, Object> bindingMap, String status, String AD_PATH, OrganizationDataService orgManager) {
        long updateDnTime = System.currentTimeMillis();
        if (debugMode) {
            println "==ProvisionServicePreProcessor Update DN: AD_PATH=" + AD_PATH
        }
        def dnAttr = getUserAttributeByName(user, bindingMap, "distinguishedName")
        def oldDnValue = dnAttr?.value

        def DN = createDistinguishedName(user, bindingMap, status, AD_PATH, orgManager)
        if (!DN) {
            if (debugMode) {
                println "ProvisionServicePreProcessor Error: DistinguishedName can not be generated for User ${user.firstName} ${user.lastName}"
            }
            return false
        } else if (!DN.equalsIgnoreCase(oldDnValue)) {
            if (user.id && dnAttr?.id) {
                // Instant attribute update, required for DISABLE, DELETE operations, which will not process pUser
                if (dnAttr) {
                    dnAttr.value = DN
                    if (debugMode) {
                        println "==ProvisionServicePreProcessor Update DN:AttributeOperationEnum=REPLACETTTT"
                    }

                    dnAttr.operation = AttributeOperationEnum.REPLACE
                }
                UserDataService userDataService = context.getBean('userManager') as UserDataService
                UserAttributeEntity attributeEntity = new UserAttributeEntity(userId: user.id, name: "distinguishedName", value: DN)
                if (dnAttr) {
                    attributeEntity.id = dnAttr.id
                    userDataService.updateAttribute(attributeEntity)
                } else {
                    userDataService.addAttribute(attributeEntity)
                }
            } else {
                if (!dnAttr) {
                    dnAttr = new UserAttribute("distinguishedName", DN)

                    dnAttr.operation = AttributeOperationEnum.ADD
                    user.saveAttribute(dnAttr)
                } else {
                    dnAttr.value = DN
                    if (dnAttr.operation == AttributeOperationEnum.NO_CHANGE) {
                        if (debugMode) {
                            println "==ProvisionServicePreProcessor Update DN:AttributeOperationEnum=REPLACE"
                        }
                        dnAttr.operation = AttributeOperationEnum.REPLACE
                    }
                }
            }
        }
        if (debugMode) {
            println("2.FINISH update dn time=" + (System.currentTimeMillis() - updateDnTime));
        }
        return true
    }

    private void sendMDMDEActivated(ProvisionUser user, String[] emailAddresses) {
        def loginPrimary = user.getPrimaryPrincipal("0")?.login
        def distinguishedName = user.getAttribute("distinguishedName")?.value

        def mailService = context.getBean("mailService") as MailService

        def subject = "Request to Revoke a PKI Certificate - HPSM ref:"
        def emailAddresses1 = []
        emailAddresses1 << "Servicedesk.akzonobel.giss.bnl@capgemini.com"

        String message = "Hi Team,\n\n" +
                "This is a request to revoke a Certificate for a user who will no longer require to have a smartphone setup with MDM solution. \n" +
                "Please note, the user is still within AkzoNobel company, therefore only PKI certificate related to MDM service should be removed.\n" +
                "UserName: " + loginPrimary + "\n" +
                "LDAPInfo: " + distinguishedName + "\n" +
                "Revocation Reason: AffiliationChanged.\n\n" +
                "Regards,"

        if (debugMode) {
            println("===================================== ProvisionPreProcessor modify send MDM de-activation email = " + message + ", to emailAddresses=" + emailAddresses1 != null ? emailAddresses1 : emailAddresses)
        }
        mailService.sendEmails("Noreply@AkzoNobel.com", emailAddresses1 as String[], null, null, subject, message, false, [] as String[])
    }

    private void sendMDMActivated(ProvisionUser user, String[] emailAddresses) {
        Login loginPrimary = user.getPrimaryPrincipal("0");
        Login exchPrincipal = user.getPrimaryPrincipal(EXCHANGE_MANSYS_ID)

        String subject = "AkzoNobel email on your smartphone or tablet";
        String message = "Hello " + loginPrimary?.login + " ,\n" +
                "\n" +
                "This e-mail is to confirm that your account has been successfully setup with the Mobile Device Management (MDM) service.\n" +
                "Your action is required as you need to enroll your mobile device(s).\n" +
                "\n" +
                "What MDM brings me:\n" +
                "MDM enables remote management of your device. After enrollment, installing applications will not be locked or limited. Android devices will receive a new email application (Touchdown) that will provide access to company email/agenda/ contacts. For Apple devices, it will result in a more secure email setup, easy WiFi access on AkzoNobel sites (AN-Mobile) and direct access to our Intranet.  For the future, MDM will be the corner stone for the use and distribution of AkzoNobel mobile apps on AkzoNobel mobile devices.\n" +
                "\n" +
                "How to enroll the device:\n" +
                "To enable MDM on your device, please complete steps described in the MDM Enrollment guide available on AkzoNobel intranet or watch the MDM enrollment video on OneTube. Via the link below, this invite is available in 8 languages:\n" +
                "Link to documents on http://mobile.one.akzonobel.intra<http://one.akzonobel.intra/function/11/GSS/GTS/OneVoice/MDM/MDM%20Manuals/Forms/AllItems.aspx>\n" +
                "\n" +
                "What will be installed on my device and why is this necessary?\n" +
                "Android devices:\n" +
                "There will be 2 new Apps installed on your device:\n" +
                "\n" +
                "1.       Citrix Mobile Connect App: This application is the core application for Mobile Device Management. Without this application, company email will no longer be available on your device. It can be used to re-enroll your device again if, for some reason, e-mail stops working. The Citrix Mobile Connect App store is still empty. In future it will be used to create the AkzoNobel app store and distribute AkzoNobel mobile applications to AkzoNobel mobile users.\n" +
                "\n" +
                "  1.  TouchDown App:  This is the new вЂњOutlookвЂќ email application. Your company email, contacts and calendar info will be handled and stored in this app. TouchDown is currently one of the most secure email applications available for Android devices.\n" +
                "\n" +
                "Apple devices:\n" +
                "There will be 2 new Apps installed on your device:\n" +
                "\n" +
                "  1.  Citrix Mobile Enroll App: This application is the core application for Mobile Device Management. Without this application, company email will no longer be available on your device. It can be used to re-enroll your device again if, for some reason, e-mail stops working. The Citrix Mobile Connect App store is still empty. In future it will be used to create the AkzoNobel app store and distribute AkzoNobel mobile applications to AkzoNobel mobile users.\n" +
                "  2.  Junos Pulse App: This app is a security feature that will handle the secure connectivity necessary to connect safely to the internal AkzoNobel network. Do not forget to open the app one time when it is installed.\n" +
                "\n" +
                "Windows devices:\n" +
                "For windows phone devices, nothing changes as the mobile operating system is lacking the toolset to be managed by a MDM system. These devices will have no easy WiFi access, no intranet connectivity, and no connection to the future AkzoNobel App stores.\n" +
                "BlackBerry Devices:\n" +
                "BlackBerries are managed differently and are not part of the MDM enrollment.\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "During the enrollment, you will be asked for your logon details (user ID in the UPN format and Windows password). Please find them below.\n" +
                "\n" +
                "User ID:                " + exchPrincipal?.login + "\n" +
                "Password:           It is your standard Windows password used to login to a PC.\n" +
                "\n" +
                "\n" +
                "\n" +
                "Relogin Citrix Mobile Connect in case of Corporate Password Reset\n" +
                "Note:   In case your computer password (Domain password) has been reset or changed, you will also need to open the Citrix Mobile Connect app on your smartphone or tablet and logout and login with the new password, as shown below:\n" +
                "a.       IOS Device:\n" +
                "вЂў         Open the вЂњCitrix ConnectвЂќ application.\n" +
                "вЂў         Click on Logout button.\n" +
                "вЂў         Relogin to the device with new password.\n" +
                "\n" +
                "b.      Android Device:\n" +
                "вЂў         Open Citrix Mobile Connect application and click on Configuration\n" +
                "вЂў         Click вЂњRe-EnrollвЂќ\n" +
                "вЂў         Confirm\n" +
                "вЂў         Confirm the ID and Click on Enroll using new password.\n" +
                "вЂў         Enter the new password and Click Enroll.\n" +
                "вЂў         Your device will be activated with new password.\n" +
                "\n" +
                "\n" +
                "\n" +
                "Thank you.\n" +
                "\n" +
                "This email is automatically generated because of your request to have AkzoNobel email on your smartphone or Tablet."

        def mailService = context.getBean("mailService") as MailService
        String[] emailAddresses1 = null;
        if (exchPrincipal) {
            emailAddresses1 = new String[emailAddresses.length + 1];
            for (int i = 0; i < emailAddresses.length; i++) {
                emailAddresses1[i] = emailAddresses[i]
            }
            emailAddresses1[emailAddresses.length] = exchPrincipal.login
        }
        if (debugMode) {
            println("===================================== ProvisionPreProcessor modify send MDM email = " + message + ", to emailAddresses=" + emailAddresses1 != null ? emailAddresses1 : emailAddresses)
        }
        mailService.sendEmails("Noreply@AkzoNobel.com", emailAddresses1 != null ? emailAddresses1 : emailAddresses, null, null, subject, message, false, [] as String[])
    }


    private void sendCanNotMapSiteAndBU(ProvisionUser user, String siteCode, String bu, boolean siteOrgExists) {
        String subject = "OpenIAM [Error] moving to non existed location ${siteCode}_${bu}!";
        String message = siteOrgExists ? """
            During processing user ${user.nickname} we found that based on requests user should have \n
            SiteCode=${siteCode} \n
            BU Code = ${bu}\n
            Such combination exists in InAD, but no address information for this combination. \n
            Need to fix it and re-import to OpenIAM before continue.""" :
                """
            During processing user ${user.nickname} we found that based on requests user should have \n
            SiteCode=${siteCode} \n
            BU Code = ${bu}\n
            Such combination doesn't exists in InAD. \n
            Need to fix it and re-import to OpenIAM before continue."""

        def mailService = context.getBean("mailService") as MailService
        String[] to = new String[1];
        //to[0]="dmitry.zaporozhec@openiam.com";
        to[0] = "iam@akzonobel.com"
        mailService.sendEmails("noreply@akzonobel.com", to, null, null, subject, message, false, [] as String[])

    }

    private void sendError(ProvisionUser user, String bu) {
        String subject = "OpenIAM [Error] Fail PreProcessor!";
        String message = """
            During processing user ${user.nickname} we get Error: ${bu} \n"""
        message+="From Binding Map!!\n"
        if (attributesMap)
            for (String ua:attributesMap.keySet()) {
                message+="Key in Map = ${ua}. Corresponding Attribute ${attributesMap.get(ua)?.name} value ${attributesMap.get(ua)?.value}\n"
            }
        message+="From User Record!!\n"

        if (user.userAttributes)
            for (String ua:user.userAttributes.keySet()) {
                message+="Key in Map = ${ua}. Corresponding Attribute ${user.userAttributes.get(ua)?.name} value ${user.userAttributes.get(ua)?.value}\n"
            }


        def mailService = context.getBean("mailService") as MailService
        String[] to = new String[2];
//        to[0]="dmitry.zaporozhec@openiam.com";
        //to[1] = "vitaliya.Zhuravleva@openiam.com"
        to[1] = "iam@akzonobel.com"

        mailService.sendEmails("noreply@akzonobel.com", to, null, null, subject, message, false, [] as String[])

    }



    private void sendNotificationBreakProvisioning(ProvisionUser user, String empGrp) {
        String subject = "OpenIAM [Error] Break Provisioning for user ${user.nickname}";
        String message =  """
            Break provisioning for user ${user.nickname}, employeeID: ${user.employeeId} \n
            Employee Group is ${empGrp}."""
        def mailService = context.getBean("mailService") as MailService
        String[] to = new String[1];
        // to[0]="vitaliya.zhuravleva@openiam.com";
        to[0] = "iam@akzonobel.com"
        mailService.sendEmails("noreply@akzonobel.com", to, null, null, subject, message, false, [] as String[])

    }

    public int disable(ProvisionUser user, Map<String, Object> bindingMap) {
        resetLocalFields();
        def orgManager = context.getBean('orgManager') as OrganizationDataService
        Boolean operationDisable = bindingMap.get("operation")
        addAttribute(user, "IN_ONEHUB", "N", bindingMap)
        if (debugMode) {
            println("===================================== ProvisionPreProcessor operationDisable = " + operationDisable)
        }
        def groupService = context.getBean('groupManager') as GroupDataService
        roleWS = context.getBean('roleWS') as RoleDataWebService;

        UserAttribute userEditActionInfoAttr = getUserAttributeByName(user, bindingMap, "userEditActionInfo")
        UserAttribute deactivationDateAttr = getUserAttributeByName(user, bindingMap, "DEACTIVATION_DATE")
        if (!operationDisable) {
            UserDataService userDataService = context.getBean('userManager') as UserDataService
            UserEntity userEntity = userDataService.getUser(user.getId())
            if (userEditActionInfoAttr && userEditActionInfoAttr.getId()) {
                userEntity.removeUserAttribute(userEditActionInfoAttr.getId())
                userDataService.removeAttribute(userEditActionInfoAttr.getId())
                if (debugMode) {
                    println("===================================== ProvisionPreProcessor disable 'userEditActionInfo' deleted attribute ID = " + userEditActionInfoAttr)
                }
            }
            if (deactivationDateAttr && deactivationDateAttr.getId()) {
                userEntity.removeUserAttribute(deactivationDateAttr.getId())
                userDataService.removeAttribute(deactivationDateAttr.getId())
                if (debugMode) {
                    println("===================================== ProvisionPreProcessor disable 'DEACTIVATION_DATE' deleted attribute ID = " + deactivationDateAttr)
                }
            }
            boolean g_GSS_MDMEmailWMS_exists = false;
            boolean g_GSS_MDMUsers_exists = false;
            boolean m_Unity_Disabled_Users_exists = false;

            for (Group gr : user.groups) {
                if (g_GSS_MDMEmailWMS_ID.equals(gr.id)) {
                    g_GSS_MDMEmailWMS_exists = true;
                } else if (g_GSS_MDMUsers_ID.equals(gr.id)) {
                    g_GSS_MDMUsers_exists = true;
                } else if (m_Unity_Disabled_Users_exists_ID.equals(gr.id)) {
                    m_Unity_Disabled_Users_exists = true;
                }
            }

            for (Role r : getUserRoles(user.id)) {
                if (MDM_ROLE_ID.equalsIgnoreCase(r.id)) {
                    if (!g_GSS_MDMEmailWMS_exists) {
                        GroupEntity g_GSS_MDMEmailWMS = groupService.getGroup(g_GSS_MDMEmailWMS_ID);
                        if (g_GSS_MDMEmailWMS != null) {
                            userEntity.addGroup(g_GSS_MDMEmailWMS)
                        }
                        if (debugMode) {
                            println("===================================== ProvisionPreProcessor modify g_GSS_MDMEmailWMS = " + g_GSS_MDMEmailWMS)
                        }
                    }
                    if (!g_GSS_MDMUsers_exists) {
                        GroupEntity g_GSS_MDMUsers = groupService.getGroup(g_GSS_MDMUsers_ID);
                        if (g_GSS_MDMUsers != null) {
                            userEntity.addGroup(g_GSS_MDMUsers)
                        }
                        if (debugMode) {
                            println("===================================== ProvisionPreProcessor modify g_GSS_MDMUsers = " + g_GSS_MDMUsers)
                        }
                    }
                    break;
                }
            }
            if (m_Unity_Disabled_Users_exists) {
                GroupEntity m_Unity_Disabled_Users = groupService.getGroup(m_Unity_Disabled_Users_exists_ID);
                if (m_Unity_Disabled_Users != null) {
                    userEntity.removeGroup(m_Unity_Disabled_Users)
                }
            }
        } else {

            DateTime nextMonthDateTime = new DateTime(System.currentTimeMillis());
            nextMonthDateTime = nextMonthDateTime.plusMonths(1);
            def df = new SimpleDateFormat("dd.MM.yyyy")

            UserDataService userDataService = context.getBean('userManager') as UserDataService
            UserEntity userEntity = userDataService.getUser(user.getId())

            if (!deactivationDateAttr) {
                String formatedDate = df.format(nextMonthDateTime.toDate());
                UserAttributeEntity attributeEntity = new UserAttributeEntity();
                attributeEntity.setUserId(userEntity.id)
                attributeEntity.setName("DEACTIVATION_DATE")
                attributeEntity.setValue(formatedDate)
                if (debugMode) {
                    println("===================================== ProvisionPreProcessor disable 'DEACTIVATION_DATE' add attribute value = " + formatedDate)
                }
                userDataService.addAttribute(attributeEntity)
            }

            // Check MDM_ROLE_ID and remove MDM groups
            boolean g_GSS_MDMEmailWMS_exists = false;
            boolean g_GSS_MDMUsers_exists = false;
            boolean m_Unity_Disabled_Users_exists = false;
            for (Group gr : user.groups) {
                if (g_GSS_MDMEmailWMS_ID.equals(gr.id)) {
                    g_GSS_MDMEmailWMS_exists = true;
                } else if (g_GSS_MDMUsers_ID.equals(gr.id)) {
                    g_GSS_MDMUsers_exists = true;
                } else if (m_Unity_Disabled_Users_exists_ID.equals(gr.id)) {
                    m_Unity_Disabled_Users_exists = true;
                }
            }
            // revert g_GSS_MDMEmailWMS_exists and g_GSS_MDMUsers if MDM user and doesn't exist these groups
            for (Role r : getUserRoles(user.id)) {
                if (MDM_ROLE_ID.equalsIgnoreCase(r.id)) {
                    if (g_GSS_MDMEmailWMS_exists) {
                        GroupEntity g_GSS_MDMEmailWMS = groupService.getGroup(g_GSS_MDMEmailWMS_ID);
                        if (g_GSS_MDMEmailWMS != null) {
                            userEntity.removeGroup(g_GSS_MDMEmailWMS)
                        }
                        if (debugMode) {
                            println("===================================== ProvisionPreProcessor modify g_GSS_MDMEmailWMS = " + g_GSS_MDMEmailWMS)
                        }
                    }
                    if (g_GSS_MDMUsers_exists) {
                        GroupEntity g_GSS_MDMUsers = groupService.getGroup(g_GSS_MDMUsers_ID);
                        if (g_GSS_MDMUsers != null) {
                            userEntity.removeGroup(g_GSS_MDMUsers)
                        }
                        if (debugMode) {
                            println("===================================== ProvisionPreProcessor modify g_GSS_MDMUsers = " + g_GSS_MDMUsers)
                        }
                    }
                    List<EmailAddress> emailList = userDataService.getEmailAddressDtoList(user.id, false)
                    def emailAddresses = emailList?.collect { it -> it.emailAddress } as String[]
                    sendMDMDEActivated(user, emailAddresses)
                    break;
                }
            }
        }
/*        ADLookup adLookup = new ADLookup(AD_MANSYS_ID, context)
        String login = bindingMap.get("targetSystemIdentity");

        adLookup.fire(login, "DISABLE");*/

        def adPath = this.getUserAttributeByName(user, bindingMap, "AD_PATH")?.value;
        //def adPath = "OU=Expired,OU=Unity,DC=d30,DC=intra"
        if (!operationDisable) {
            if (!updateDn(user, bindingMap, "ENABLE", adPath, orgManager)) {
                sendError(user,"1. update DN fail during ENABLE")
                return ProvisioningConstants.FAIL
            }
        } else {
            if (!updateDn(user, bindingMap, "DISABLED", adPath, orgManager)) {
                sendError(user,"2. update DN fail during DISABLE")
                return ProvisioningConstants.FAIL
            }
        }
        return ProvisioningConstants.SUCCESS;
    }

    public int delete(ProvisionUser user, Map<String, Object> bindingMap) {
        resetLocalFields();
        ADLookup adLookup = new ADLookup(AD_MANSYS_ID, context)
        String login = bindingMap.get("targetSystemIdentity");
        addAttribute(user, "IN_ONEHUB", "N", bindingMap)

        adLookup.fire(login, "DELETE");

        def adPath = this.getUserAttributeByName(user, bindingMap, "AD_PATH")?.value;
        def orgManager = context.getBean('orgManager') as OrganizationDataService
        if (!updateDn(user, bindingMap, "DELETED", adPath, orgManager)) {
            sendError(user,"3. update DN fail during DELETED")
            return ProvisioningConstants.FAIL
        }

        return ProvisioningConstants.SUCCESS;
    }

    public int setPassword(PasswordSync passwordSync, Map<String, Object> bindingMap) {
        resetLocalFields();

        if (debugMode) {
            println("ProvisionServicePreProcessor: SetPassword called.");
        }

        showBindingMap(bindingMap);


        return ProvisioningConstants.SUCCESS;

    }

    def addAttribute(ProvisionUser user, String attributeName, String attributeValue, Map<String, Object> bindingMap) {

        def attr = getUserAttributeByName(user, bindingMap, attributeName)
        if (!attr) {
            attr = new UserAttribute(attributeName, attributeValue);
            attr.operation = AttributeOperationEnum.ADD
            user.saveAttribute(attr)
        } else {
            if (attr.value != attributeValue) {
                attr.value = attributeValue
                attr.operation = AttributeOperationEnum.REPLACE
                user.saveAttribute(attr)
            }
        }
    }

    def addFilterAttribute(ProvisionUser user, String attributeName, String filter, Map<String, Object> bindingMap) {
        boolean isMultivalued = filter.length() >= 4000
        def values = null
        if (isMultivalued) {
            values = [] as ArrayList<String>
            int pos = 0
            while (1) {
                int newPos = (pos + 255 < filter.length()) ? (filter.lastIndexOf(',', pos + 255) + 1) : 0
                if (newPos == 0) newPos = filter.length() + 1
                if (newPos == pos) break
                values += filter.substring(pos, newPos - 1) + ','
                pos = newPos
            }
        }
        def attr = getUserAttributeByName(user, bindingMap, attributeName)

        if (!attr) {
            if (filter) {
                attr = new UserAttribute(attributeName, null)
                if (isMultivalued) {
                    attr.values = values
                    attr.value = null
                } else {
                    attr.values.clear()
                    attr.value = filter
                }
                attr.isMultivalued = isMultivalued
                attr.operation = AttributeOperationEnum.ADD
                user.saveAttribute(attr)
            }
        } else {
            if (filter) {
                String curValue = attr.isMultivalued ? attr.values.join("") : attr.value
                if (isMultivalued != attr.isMultivalued || filter != curValue) {
                    if (isMultivalued) {
                        attr.values = values
                        attr.value = null
                    } else {
                        attr.values.clear()
                        attr.value = filter
                    }
                    attr.isMultivalued = isMultivalued
                    attr.operation = AttributeOperationEnum.REPLACE
                }
            } else {
                attr.operation = AttributeOperationEnum.DELETE
            }
        }
    }

    def removeAttribute(ProvisionUser user, Map<String, Object> bindingMap, String attributeName) {
        def attr = getUserAttributeByName(user, bindingMap, attributeName)
        if (debugMode) {
            println("============== ProvisionServicePreProcessor=== removeAttribute=" + attr);
        }
        if (attr) {
            attr.operation = AttributeOperationEnum.DELETE
            user.saveAttribute(attr)
        }
    }

    private void showBindingMap(Map<String, Object> bindingMap) {
        // context to look up spring beans
        if (debugMode) {
            println("Show binding map:");
        }
        for (Map.Entry<String, Object> entry : bindingMap.entrySet()) {
            def key = entry.key
            def val = entry.value as String
            if (key == 'password') {
                val = 'PROTECTED'
            }
            println("- Key=" + key + " value=" + val)
        }
    }

    private String createDistinguishedName(ProvisionUser user, Map<String, Object> bindingMap, String status, String adPath, OrganizationDataService orgManager) {
        long t = System.currentTimeMillis();
        def serviceAccountName = getUserAttributeByName(user, bindingMap, "serviceAccountName")?.value
        String principal = user.getPrimaryPrincipal("0")?.login
        if (!principal) {
            def loginManager = context.getBean("loginManager") as LoginDataService
            principal = loginManager.getByUserIdManagedSys(user.id, '0')?.login
        }
        // for synced users
        if (!user.nickname || changeDisplayName) {
            user.nickname = getUserAttributeByName(user, bindingMap, "displayName")?.value
        }
        if (!user.nickname)
            user.nickname = this.generateDisplayName(user, bindingMap);
        String cnBuilder = (serviceAccountName ? serviceAccountName : (user.nickname ? user.nickname : null));
        if (!cnBuilder) {
            /*if (user.prefix) {
                user.prefixLastName = user.prefix;
            user.prefix="";
            }*/
            cnBuilder = this.generateDisplayName(user, bindingMap);
            // user.nickname = cnBuilder;
        }
        cnBuilder = cnBuilder.replace("/", " ")
        String cn = cnBuilder;
        if (debugMode) {
            println("NEW CN=" + cn);
        }
        // check unique
//        def adSearch = new TargetSystemSearch(AD_MANSYS_ID, context)

        def ctr = 0
        String originCn = cn;
        if (debugMode) {
            println(">>>>>>>>>>>>>>>>>>>>>>>> ADPath :" + adPath);
        }
        def struc = null;
        if (debugMode) {
            println(">>>>>>>>>>>>>>>>>>> struc :" + struc);
        }
        long t2 = System.currentTimeMillis();
        while (isExistInMap("cn", cn)) {
            if (struc == null) {
                struc = parseADPath(user, bindingMap, orgManager)
            }
            ctr++;
            if (ctr == 1) {
                cn = originCn + (struc?.company ? (" [" + struc?.company + "]") : "")
            }
            if (ctr == 2) {
                cn = originCn + ((struc?.company && struc?.siteCode) ? (" [" + struc?.company + " " + struc?.siteCode + "]") : "")
            }
            if (ctr == 3) {
                cn = originCn + ((struc?.company && struc?.siteCode && struc?.department) ? (" [" + struc?.company + " " + struc?.siteCode + " " + struc?.department + "]") : "")
            }
            if (ctr > 3) {
                cn = originCn + ("[${ctr - 3}]");
            }
            if (debugMode) {
                println("============== ProvisionServicePreProcessor === createDistinguishedName ==  Search CN:" + cn);
            }
        }
        if (debugMode) {
            println("Look up duplicates time=" + (System.currentTimeMillis() - t2));
        }
        if (!user.nickname || changeDisplayName) {
            user.nickname = cn;
        }
        def userPath = null as String
        if (adPath) {
            if (debugMode) {
                println("AD_PATH=[" + adPath + "]")
            }
            //def parts = adPath.split(',', 2) as String[]
            // String basePath = parts.length > 1 ? parts[1] : parts[0]
            if ('AKZONOBEL_RSC_ACCOUNT' == user.mdTypeId) {
                userPath = "OU=ResourceMailbox," + adPath
            } else if ('AKZONOBEL_SRV_ACCOUNT' == user.mdTypeId) {
                //   userPath = "OU=Service Accounts," + adPath
                userPath = "OU=Users," + adPath

            } else if (null != getUserRoleById(user, HP_ADMIN_ROLE_ID)) {
                // basePath = adPath
                userPath = "OU=Administrators," + adPath
            } else {
                //basePath = adPath
                userPath = "OU=Users," + adPath

            }

            /*if ((status ?: user.getSecondaryStatus()) == UserStatusEnum.DISABLED) {
                if (userPath.toLowerCase().contains("ou=unity,")) {
                    userPath = basePath.replaceAll("(?i)OU=Unity,", "OU=Expired,OU=Unity,")
                } else if (userPath.toLowerCase().contains("ou=unitymwls,")) {
                    userPath = userPath.replaceAll("(?i)OU=UnityMWLS,", "OU=Expired Objects,")
                }
            }*/

            if (debugMode) {
                println("userPath =" + userPath + " status " + status)
            }

            if (status?.equalsIgnoreCase("DISABLED")) {
                userPath = "OU=Expired,OU=Unity,DC=d30,DC=intra"
            }
        }
        def DN = userPath ? "CN=${cn},${userPath}" : null
        if (debugMode) {
            println("User Path=[" + userPath + "]")
            println("DN=[" + DN + "]")
            println("2. Finish Time for create DN=" + (System.currentTimeMillis() - t))
        }
        return DN
    }

    private String createUserPrincipalName(ProvisionUser user, Map<String, Object> bindingMap) {

        def upnBase
        def upnDomain
        def exchLookup = new TargetSystemLookup(EXCHANGE_MANSYS_ID, context)
        def loginManager = context.getBean("loginManager") as LoginDataService

        def serviceAccountName = getUserAttributeByName(user, bindingMap, "serviceAccountName")?.value
        if (serviceAccountName) {
            upnBase = serviceAccountName
            upnDomain = "@akzonobel.com"
        } else {
            def classification = getUserAttributeByName(user, bindingMap, "classification")?.value
            if ("ADM".equalsIgnoreCase(classification)) {
                upnBase = user.principalList?.find({ it.managedSysId = "0" })?.login
                upnDomain = "@akzonobel.com"
            } else {
                def lastName = (user.prefixLastName ?: '') + (user.lastName ?: '')
                String firstName = convertNonAscii(user.firstName);
                lastName = convertNonAscii(lastName);
                upnBase = firstName.replaceAll(/\W/, '') + "." + lastName.replaceAll(/\W/, '')
                upnDomain = "@akzonobel.com"
            }
        }
        upnBase = upnBase.replaceAll(/\s*/, '')
        def ctr = 0
        def upn = upnBase + upnDomain
        while (isExistInMap("userPrincipalName", upn) || loginManager.loginExists(upn, EXCHANGE_MANSYS_ID)
        ) {
            upn = upnBase + (++ctr) + upnDomain
        }
        if (debugMode) {
            println "====== ProvisionServicePreProcessor.createUserPrincipalName: " + upn
        }
        return upn

    }


    private UserAttribute getUserAttributeByName(ProvisionUser user, Map<String, Object> bindingMap, String attrName) {
        if (debugMode) {
            println("Start getUserAttributeByName")
        }
        def attr = user.getAttribute(attrName)
        if (!attr && user.id) {
            if (attributesMap == null) {
//                attributesMap = (Map<String, UserAttribute>) bindingMap.get("userAttributes")
                def Map<String, UserAttribute> userAttributeMap = new HashMap<>();
                if (bindingMap.get("userAttributes")) {
                    for (Map.Entry<String, UserAttribute> mapEntry : (Map<String, UserAttribute>) bindingMap.get("userAttributes")) {
                        UserAttribute ua = (UserAttribute) mapEntry.getValue()
                        userAttributeMap.put(ua.getName(), ua)
                    }
                    attributesMap = userAttributeMap;
                }
            }

            attr = (attributesMap != null && attributesMap.containsKey(attrName)) ? attributesMap.get(attrName) : null
        }
        if (debugMode) {
            println("Finish getUserAttributeByName")
        }
        return attr?.operation != AttributeOperationEnum.DELETE ? attr : null
    }

    private Role getUserRoleByType(ProvisionUser user, String roleTypeId) {
        def role = user.roles?.find({ Role it -> it.mdTypeId == roleTypeId && AttributeOperationEnum.ADD.equals(it.operation) });
        if (role) {
            if (debugMode) {
                println("UI CALL MOVE TO ANOTHER OU");
            }
            return role;
        }

        if (user.id) {
            def roleWS = context.getBean('roleWS') as RoleDataWebService
            role = roleWS.getRolesForUser(user.getId(), "3000", true, -1, -1).find({
                it.mdTypeId == roleTypeId && it.operation != AttributeOperationEnum.DELETE
            })
        }
        if (role) {
            role.setOperation(AttributeOperationEnum.ADD)
            user.roles.add(role)
        }

        if (!role && user.id) {
            role = getUserRoles(user.id).find({ it.mdTypeId == roleTypeId })
        }
        if (debugMode) {
            println("GET OLD ROLE FROM DB. LOOKS LIKE AUTOMATIC (MYHR/MYIT) CALL. Role=" + role);
        }
        return role
    }

    private String getResourceProperty(final Resource resource, final String propertyName) {
        String retVal = null;
        if (resource != null && StringUtils.isNotBlank(propertyName)) {
            final ResourceProp property = resource.getResourceProperty(propertyName);
            if (property != null) {
                retVal = property.getValue();
            }
        }
        return retVal;
    }

    private Role getUserRoleById(ProvisionUser user, String roleId) {

        // skip if user create mode
        if (!user.getId()) {
            return null;
        }
        def role = getUserRoles(user.id).find({ it.id == roleId })

//        def role = roleWS.getRolesForUser(user.getId(), "3000", true, -1, -1).find({
//          it.id == roleId && it.operation != AttributeOperationEnum.DELETE
//    })

        if (role) {
            role.setOperation(AttributeOperationEnum.ADD)
            user.roles.add(role)
        }

//        if (!role && user.id) {
//            role = getUserRoles(user.id).find({ it.id == roleId })
//        }

        return role
    }

    private Set<Group> getAllUserGroupsByType(ProvisionUser user, String groupTypeId) {
        def groupWS = context.getBean('groupWS') as GroupDataWebService
        def groups = groupWS.getGroupsForUser(user.id, "3000", true, -1, -1).findAll({
            it.mdTypeId == groupTypeId
        }) as Set
        if (user.id) {
            getUserGroups(user.id).findAll({ it.mdTypeId == groupTypeId }).each { g ->
                if (!groups.find { it.id == g.id }) {
                    groups += g
                }
            }
        }
        return groups
    }


    List<Role> getUserRoles(String userId) {
        if (_userRoles == null) {
            def roleWS = context.getBean('roleWS') as RoleDataWebService
            _userRoles = roleWS.getRolesForUser(userId, "3000", false, -1, -1);
        }
        if (debugMode) {
            println("===================================== ProvisionPreProcessor  getUserRoles  " + _userRoles)
        }
        return _userRoles
    }


    List<Group> getUserGroups(String userId) {
        if (_userGroups == null) {
            def groupWS = context.getBean('groupWS') as GroupDataWebService
            _userGroups = groupWS.getGroupsForUser(userId, "3000", false, 0, 100);
        }
        return _userGroups
    }

    private void fillExistedPropMap(ProvisionUser user, String loginID, String firstName, String lastName, Map<String, Object> bindingMap) {
        //search login params
        String currentLogin = null;
        if (user.id) {
            currentLogin = user.principalList?.find({ "0".equals(it.managedSysId) })?.login;
            if (!currentLogin) {
                LoginDataWebService loginManager = context.getBean("loginWS") as LoginDataWebService
                currentLogin = loginManager.getLoginByUser(user.id)?.principalList?.find({
                    "0".equals(it.managedSysId)
                })?.login
            }
        }
        if (debugMode) {
            println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Current LOGIN=" + currentLogin);
        }
        String[] login = new String[4];
        if (loginID) {
            login[0] = loginID + "*";
        }
        if (lastName) {
            def len = Math.min(7, lastName.length())
            login[1] = lastName[0..len - 1] + firstName[0] + "*"
            login[2] = lastName[0..len - (5 < len ? len - 5 : 1)] + firstName[0] + "*"
            login[3] = lastName[0..len - (4 < len ? len - 4 : 1)] + firstName[0] + "*"
        }

        String upnBase = "";
        def serviceAccountName = getUserAttributeByName(user, bindingMap, "serviceAccountName")?.value
        if (serviceAccountName) {
            upnBase = serviceAccountName
        } else {
            def classification = getUserAttributeByName(user, bindingMap, "classification")?.value
            if ("ADM".equalsIgnoreCase(classification)) {
                upnBase = user.principalList?.find({ it.managedSysId = "0" })?.login
            } else {
                lastName = (user.prefixLastName ?: '') + (user.lastName ?: '')
                firstName = convertNonAscii(user.firstName);
                lastName = convertNonAscii(lastName);
                upnBase = firstName.replaceAll(/\W/, '') + "." + lastName.replaceAll(/\W/, '')
            }
        }

        if (!existedAttributes) {
            existedAttributes = new HashMap<String, Set<String>>();
        }
        long t1 = System.currentTimeMillis();
        def adLookup = new TargetSystemSearch(AD_MANSYS_ID, context);
        Map<String, String> map = new HashMap<String, String>();
        List<ObjectValue> objectValueList = null;
        String query = "|";
        for (String l : login) {
            if (l)
                query += "(samaccountname=${l})";
        }
        query += "(userPrincipalName=${upnBase}*)"
        if (debugMode) {
            println("Search query=" + query);
        }
        objectValueList = adLookup.search(query);
        if (objectValueList) {
            for (ObjectValue objectValue : objectValueList) {
                if (objectValue.attributeList) {
                    //check current user
                    boolean isCurrent = false;
                    for (ExtensibleAttribute ea : objectValue.attributeList) {
                        if ("samaccountname".equalsIgnoreCase(ea.getName())) {

                            if (ea.getValue().equalsIgnoreCase(currentLogin)) {
                                if (debugMode) {
                                    println("Find the same=" + currentLogin)
                                }
                                isCurrent = true;
                            }
                            break;
                        }
                    }
                    if (isCurrent)
                        continue;

                    for (ExtensibleAttribute ea : objectValue.attributeList) {
                        if ("samaccountname".equalsIgnoreCase(ea.getName())) {
                            saveExistedParam("samaccountname", ea.getValue()?.toLowerCase())
                            continue;
                        }
                        if ("userprincipalname".equalsIgnoreCase(ea.getName())) {
                            saveExistedParam("userprincipalname", ea.getValue()?.toLowerCase());
                            continue;
                        }
                        if ("Name".equalsIgnoreCase(ea.getName())) {
                            saveExistedParam("cn", ea.getValue()?.toLowerCase())
                            continue;
                        }
                    }
                }
            }
        }
        if (debugMode) {
            println("Map=" + existedAttributes);
            println("fillExistedPropMap time=" + (System.currentTimeMillis() - t1));
        }
    }

    private void saveExistedParam(String name, String value) {
        Set<String> samaccountNameSet = existedAttributes.get(name);
        if (!samaccountNameSet) {
            existedAttributes.put(name, new HashSet<String>());
            existedAttributes.get(name).add(value);
        } else {
            samaccountNameSet.add(value);
        }
    }

    private boolean isExistInMap(String name, String value) {
        boolean isExist = false;
        isExist = existedAttributes?.get(name.toLowerCase())?.contains(value?.toLowerCase());
        return isExist
    }

    private createPrimaryPrincipal(ProvisionUser user, Map<String, Object> bindingMap) {
        def loginManager = context.getBean("loginManager") as LoginDataService
        def loginID = user.getAttribute("serviceAccountName")?.value?.replaceAll(/\s+/, '')
        def lastName = convertNonAscii(user.lastName?.toLowerCase())?.replaceAll("[^a-z0-9]+", "")
        def firstName = convertNonAscii(user.firstName?.toLowerCase())?.replaceAll("[^a-z0-9]+", "")
        this.fillExistedPropMap(user, loginID, firstName, lastName, bindingMap);
        if (loginID) {
            if (loginManager.loginExists(loginID, OIAM_MANSYS_ID) || isExistInMap("samaccountname", loginID)) {
                if (debugMode) {
                    println "Unable to use service account " + loginID + ", conflicts with existing one"
                }
                return null
            } else {
                return loginID
            }
        }



        if (lastName) {
            def len = Math.min(7, lastName.length())
            // the primary way to generate login
            loginID = lastName[0..len - 1] + firstName[0]
            if (!loginManager.loginExists(loginID, OIAM_MANSYS_ID) && !isExistInMap("samaccountname", loginID)) {
                return loginID
            }
            // def loginBase = lastName[0..len - 1] + firstName[0]
            // if not found unique login will use counter
            def tmpCntr = 0
            for (def cntr = 1; cntr <= 99; ++cntr) {
                // def baseLen = Math.min(len, 7 - cntr.toString().length())
                tmpCntr = 7 - ((cntr < 10) ? 2 : 3)
                loginID = lastName[0..len - (tmpCntr < len ? len - tmpCntr : 1)] + firstName[0] + cntr
                if (!loginManager.loginExists(loginID, OIAM_MANSYS_ID) && !isExistInMap("samaccountname", loginID)) {
                    return loginID
                }
            }
        }
        if (debugMode) {
            println "Unable to generate login for user " + user.lastName + ", " + user.firstName
        }

        return null
    }

    private void setDelegationFilter(ProvisionUser user, Map<String, Object> bindingMap, String orgRoleId) {
        def roleIds = [] as TreeSet<String>
        def groupIds = [] as TreeSet<String>

        if (getUserRoleById(user, "9") != null) {
            removeAttribute(user, bindingMap, DelegationFilterHelper.DLG_FLT_ROLE)
            removeAttribute(user, bindingMap, DelegationFilterHelper.DLG_FLT_GRP)
            return
        }

        def roleTypeIds = [BU_ROLE_TYPE_ID, ST_ROLE_TYPE_ID, DELEGATION_ROLE_TYPE_ID] as Set
        if (debugMode) {
            println("======================= ProvisionPreProcessor ======= setDelegationFilter roles collecting start ..")
        }
        def delegationRoles = user.roles.findAll({ roleTypeIds.contains(it.mdTypeId) }) as Set
        if (user.id) {
            getUserRoles(user.id).findAll({ roleTypeIds.contains(it.mdTypeId) }).each { r ->
                if (!delegationRoles.find { it.id == r.id }) {
                    delegationRoles += r
                }
            }
        }
        if (debugMode) {
            println("======================= ProvisionPreProcessor ======= setDelegationFilter roles collecting finished.")
        }
        def delegationIds = delegationRoles.findAll({ it.operation != AttributeOperationEnum.DELETE })?.id as List

//        def roleWS = context.getBean('roleWS') as RoleDataWebService

        // Load tree of IDs
        List<TreeObjectId> treeObjects = roleWS.getRolesWithSubRolesIds(delegationIds, "3000")
        roleIds = new TreeParser().collectValues(treeObjects)

        if (orgRoleId) {
            roleIds += orgRoleId
        }

        addFilterAttribute(user, DelegationFilterHelper.DLG_FLT_ROLE, (roleIds ? roleIds.join(",") : ""), bindingMap)
        long time1 = System.currentTimeMillis();
        def delegationGroups = getAllUserGroupsByType(user, DELEGATION_GROUP_TYPE_ID)
        def groupWS = context.getBean('groupWS') as GroupDataWebService
        delegationGroups.each { delegationGroup ->
            if (delegationGroup.operation != AttributeOperationEnum.DELETE) {
                groupWS.getChildGroups(delegationGroup.id, "3000", false, 0, 100).each { g ->
                    groupIds += g.id
                }
            }
        }
        if (groupIds) {
            addAttribute(user, DelegationFilterHelper.DLG_FLT_GRP, groupIds.join(","), bindingMap)
        }
        if (debugMode) {
            println("======================= ProvisionPreProcessor ======= setDelegationFilter finish group children. Time="
                    + (System.currentTimeMillis() - time1) + "ms");
        }

    }

    static class TreeParser {

        private TreeSet<String> values

        public TreeSet<String> collectValues(final List<TreeObjectId> tree) {
            values = new TreeSet<String>()
            _process(tree)
            return values
        }

        private void _process(final List<TreeObjectId> tree) {
            tree?.each { node ->
                if (node?.value) {
                    values += tree.value
                }
                _process(node.children)
            }
        }
    }

    class TargetSystemLookup {

        private ProvisionService provisionService = null
        private String mSysId
        private ConnectorAdapter connectorAdapter;
        private ManagedSystemWebService managedSysService;
        private ManagedSystemService managedSysDataService;
        private ManagedSysDto mSys;
        private LookupRequest<ExtensibleUser> reqType;


        public TargetSystemLookup(String managedSysId, ApplicationContext context) {
            provisionService = context.getBean('defaultProvision') as ProvisionService
            connectorAdapter = context.getBean(ConnectorAdapter.class) as ConnectorAdapter
            managedSysService = context.getBean(ManagedSystemWebService.class) as ManagedSystemWebService
            managedSysDataService = context.getBean(ManagedSystemService.class) as ManagedSystemService

            mSysId = managedSysId
            mSys = managedSysService.getManagedSys(mSysId);

            List<ExtensibleAttribute> extAttributes = new ArrayList<>()
            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] objList = managedSysService.managedSysObjectParam(managedSysId,
                    ManagedSystemObjectMatch.USER);
            if (objList != null && objList.length > 0) {
                matchObj = objList[0];
            }


            reqType = new LookupRequest<>();
            String requestId = "R" + UUIDGen.getUUID();
            reqType.setRequestID(requestId);

            ExtensibleUser extensibleUser = new ExtensibleUser();
            if (matchObj != null && StringUtils.isNotEmpty(matchObj.getKeyField())) {
                extensibleUser.setPrincipalFieldName(matchObj.getKeyField());
            }
            extensibleUser.setPrincipalFieldDataType("string");
            extensibleUser.setAttributes(extAttributes);
            reqType.setExtensibleObject(extensibleUser);
            reqType.setTargetID(managedSysId);
            reqType.setHostLoginId(mSys.getUserId());
            if (matchObj != null && StringUtils.isNotEmpty(matchObj.getSearchBaseDn())) {
                reqType.setBaseDN(matchObj.getSearchBaseDn());
            }
            String passwordDecoded = managedSysDataService.getDecryptedPassword(mSys)

            reqType.setHostLoginPassword(passwordDecoded);
            reqType.setHostUrl(mSys.getHostUrl());
            reqType.setScriptHandler(mSys.getLookupHandler());

        }

        public boolean exists(String login) {
            long time = System.currentTimeMillis();
            reqType.setSearchValue(login);
            SearchResponse resp = connectorAdapter.lookupRequest(mSys, reqType, MuleContextProvider.getCtx());
            if (debugMode) {
                println("TargetSystemLookup exists time=" + (System.currentTimeMillis() - time));
            }
            return resp.status == StatusCodeType.SUCCESS;
        }

        public boolean exists(String searchValue, String searchAttributeName) {
            long time = System.currentTimeMillis();
            reqType.getExtensibleObject().setPrincipalFieldName(searchAttributeName)
            reqType.setSearchValue(searchValue);
            SearchResponse resp = connectorAdapter.lookupRequest(mSys, reqType, MuleContextProvider.getCtx());
            if (debugMode) {
                println("TargetSystemLookup exists time=" + (System.currentTimeMillis() - time));
            }
            return resp.status == StatusCodeType.SUCCESS;
        }
    }


    class TargetSystemSearch {

        private ProvisionService provisionService = null
        private String mSysId
        private ConnectorAdapter connectorAdapter;
        private ManagedSystemWebService managedSysService;
        private ManagedSystemService managedSysDataService;
        private ManagedSysDto mSys;
        private SearchRequest<ExtensibleUser> searchRequest;
        private ProvisionConnectorDto connector;
        private ProvisionConnectorWebService connectorService;

        public TargetSystemSearch(String managedSysId, ApplicationContext context) {
            provisionService = context.getBean('defaultProvision') as ProvisionService
            connectorAdapter = context.getBean(ConnectorAdapter.class) as ConnectorAdapter
            managedSysService = context.getBean(ManagedSystemWebService.class) as ManagedSystemWebService
            managedSysDataService = context.getBean(ManagedSystemService.class) as ManagedSystemService
            connectorService = context.getBean("provisionConnectorWebService") as ProvisionConnectorWebService;

            mSysId = managedSysId
            mSys = managedSysService.getManagedSys(mSysId);

            connector = connectorService.getProvisionConnector(mSys.getConnectorId());

            List<ExtensibleAttribute> extAttributes = new ArrayList<>()
            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] objList = managedSysService.managedSysObjectParam(managedSysId,
                    ManagedSystemObjectMatch.USER);
            if (objList != null && objList.length > 0) {
                matchObj = objList[0];
            }


            searchRequest = new SearchRequest();
            String requestId = "R" + UUIDGen.getUUID();
            searchRequest.setRequestID(requestId);

            ExtensibleUser extensibleUser = new ExtensibleUser();
            if (matchObj != null && StringUtils.isNotEmpty(matchObj.getKeyField())) {
                extensibleUser.setPrincipalFieldName(matchObj.getKeyField());
            }
            extensibleUser.setPrincipalFieldDataType("string");
            extensibleUser.setAttributes(extAttributes);
            searchRequest.setExtensibleObject(extensibleUser);
            searchRequest.setTargetID(managedSysId);
            searchRequest.setHostLoginId(mSys.getUserId());
            if (matchObj != null && StringUtils.isNotEmpty(matchObj.getSearchBaseDn())) {
                searchRequest.setBaseDN(matchObj.getSearchBaseDn());
            }
            String passwordDecoded = managedSysDataService.getDecryptedPassword(mSys)
            searchRequest.setHostPort((mSys.getPort() != null) ? mSys.getPort().toString() : null);
            searchRequest.setHostLoginPassword(passwordDecoded);
            searchRequest.setHostUrl(mSys.getHostUrl());
            searchRequest.setScriptHandler(mSys.getSearchHandler());

        }

        public boolean existsDuplicate(String searchAttributeName, String searchValue, String currentSamAccountName) {
            long time = System.currentTimeMillis();
            String requestQueryString = "Get-ADUser -LDAPFilter '(AttributeName=AttributeValue)' -SearchBase 'DC=D30,DC=INTRA' -Credential @ADcred -Server \$hostUrl";
            requestQueryString = requestQueryString.replaceAll("AttributeName", searchAttributeName)
            requestQueryString = requestQueryString.replaceAll("AttributeValue", searchValue)
            searchRequest.setSearchValue(searchAttributeName);
            searchRequest.setSearchQuery(requestQueryString);
            if (debugMode) {
                println("======================= ProvisionPreProcessor ======= existsDuplicate searchValue:" + searchValue + " currentSamAccountName:" + currentSamAccountName)
            }
            SearchResponse resp = connectorAdapter.search(searchRequest, connector, MuleContextProvider.getCtx())
            if (resp != null && resp.getStatus() == StatusCodeType.SUCCESS) {
                List<ObjectValue> usersFromRemoteSys = resp.getObjectList();
                if (usersFromRemoteSys != null && usersFromRemoteSys.size() > 0) {
                    if (currentSamAccountName == null) {
                        if (debugMode) {
                            println("TargetSystemSearch existsDuplicate time = " + (System.currentTimeMillis() - time));
                        }
                        return true;
                    } else {
                        ObjectValue extensibleUser = usersFromRemoteSys.get(0);
                        ExtensibleAttribute foundSamAccountName = extensibleUser.attributeList.find { at -> "samAccountName".equalsIgnoreCase(at.name) }
                        if (debugMode) {
                            println("======================= ProvisionPreProcessor ======= existsDuplicate searchValue:" + searchValue + " foundSamAccountName:" + foundSamAccountName?.value)
                            println("TargetSystemSearch existsDuplicate time = " + (System.currentTimeMillis() - time));
                        }
                        return !currentSamAccountName.equals(foundSamAccountName?.value);
                    }
                }
            }
            if (debugMode) {
                println("TargetSystemSearch existsDuplicate time = " + (System.currentTimeMillis() - time));
            }
            return false;
        }


        public List<ObjectValue> search(Map<String, String> searchAttributeMap, String type) {
            long time = System.currentTimeMillis();
            List<ObjectValue> retVal = null;
            String mySearchRequest = "";
            if (!searchAttributeMap || searchAttributeMap.size() == 0) {
                return null;
            }
            if (searchAttributeMap.size() > 1) {
                if ("OR".equals(type)) {
                    mySearchRequest = "|";
                } else {
                    mySearchRequest = "&";
                }

            }
            for (String attr : searchAttributeMap.keySet()) {
                mySearchRequest += "(${attr}=${searchAttributeMap.get(attr)})";
            }
            String requestQueryString = "Get-ADUser -LDAPFilter '(${mySearchRequest})' -SearchBase '${DOMAIN_CONTEXT}' -Credential @ADcred -Server \$hostUrl";
//            requestQueryString = requestQueryString.replaceAll("AttributeName", searchAttributeName)
//            requestQueryString = requestQueryString.replaceAll("AttributeValue", searchValue)
//            searchRequest.setSearchValue(searchAttributeName);


            searchRequest.setSearchQuery(requestQueryString);
            SearchResponse resp = connectorAdapter.search(searchRequest, connector, MuleContextProvider.getCtx())
            if (resp != null && resp.getStatus() == StatusCodeType.SUCCESS) {
                List<ObjectValue> usersFromRemoteSys = resp.getObjectList();
                retVal = usersFromRemoteSys;
            }
            if (debugMode) {
                println("TargetSystemSearch search time = " + (System.currentTimeMillis() - time));
            }
            return retVal;
        }


        public List<ObjectValue> search(String query) {
            long time = System.currentTimeMillis();
            List<ObjectValue> retVal;
            String requestQueryString = "Get-ADUser -LDAPFilter '(${query})' -SearchBase '${DOMAIN_CONTEXT}' -Credential @ADcred -Server \$hostUrl";
            searchRequest.setSearchQuery(requestQueryString);
            //SearchResponse resp = connectorAdapter.search(searchRequest, connector, MuleContextProvider.getCtx())
            SearchResponse resp = connectorAdapter.search(searchRequest, connector)
            if (resp != null && resp.getStatus() == StatusCodeType.SUCCESS) {
                List<ObjectValue> usersFromRemoteSys = resp.getObjectList();
                retVal = usersFromRemoteSys;
            }
            if (debugMode) {
                println("TargetSystemSearch search time = " + (System.currentTimeMillis() - time));
            }
            return retVal;
        }
    }


    class ADLookup {
        //Delimiter used in CSV file
        private final String COMMA_DELIMITER = ",";
        private final String NEW_LINE_SEPARATOR = "\n";

        //CSV file header
        private static
        final String FILE_HEADER = "User Login, Managed System ID, SamAccountName, UserCertificate, Date, Action";

        private final ProvisionService provisionService = null
        private final ResourceDataService dataService = null;
        private final ManagedSystemWebService managedSysService = null;
        private String mSysId
        private final List<ExtensibleAttribute> attributes
        private final String csvFilePath;

        public ADLookup(String managedSysId, ApplicationContext context) {
            provisionService = context.getBean('defaultProvision') as ProvisionService
            managedSysService = context.getBean('managedSysService') as ManagedSystemWebService
            dataService = context.getBean('resourceDataService') as ResourceDataService
            ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);
            Resource res = dataService.getResource(mSys.getResourceId(), null);
            csvFilePath = getResourceProperty(res, "PKI_REVOCATION_FILE");
            mSysId = managedSysId
            attributes = new ArrayList<>()
            attributes.add(new ExtensibleAttribute("SamAccountName", ""));
            attributes.add(new ExtensibleAttribute("UserCertificate", ""));
        }

        private String getResourceProperty(final Resource resource, final String propertyName) {
            String retVal = null;
            if (resource != null && StringUtils.isNotBlank(propertyName)) {
                final ResourceProp property = resource.getResourceProperty(propertyName);
                if (property != null) {
                    retVal = property.getValue();
                }
            }
            return retVal;
        }

        public boolean fire(String login, String action) {
            LookupUserResponse resp = provisionService.getTargetSystemUser(login, mSysId, attributes)
            List<ExtensibleAttribute> attrs = resp.getAttrList();
            if (StringUtils.isNotBlank(csvFilePath)) {
                byte[] cert = null;
                String sAMAccountName = null;
                for (ExtensibleAttribute attr : attrs) {
                    if ("SamAccountName".equalsIgnoreCase(attr.getName())) {
                        sAMAccountName = attr.getValue();
                    } else if ("UserCertificate".equalsIgnoreCase(attr.getName())) {
                        cert = attr.getValueAsByteArray();
                    }
                }
                //  if(cert != null && sAMAccountName != null) {
                writeCSVRecord(csvFilePath, login, AD_MANSYS_ID, sAMAccountName, cert != null ? cert.encodeBase64().toString() : "null", action);
                // } else {
                log.info("========= PKI revocation: " + (cert == null ? " cert is NULL, " : "") + (sAMAccountName == null ? " sAMAccountName" : ""))
                //  }
            } else {
                log.error("=========== PKI revocation: CSV File is not available ... Please check the attirbute = PKI_REVOCATION_FILE for Managed System =" + AD_MANSYS_ID);
            }
            return resp.success
        }

        private boolean writeCSVRecord(String filePath, String userLogin, String managedSysId, String sAMAccountName, String permCert, String action) {
            DateTime dateTime = new DateTime(System.currentTimeMillis());
            DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
            String dateTimeString = fmt.print(dateTime);

            FileWriter fileWriter = null;

            try {
                boolean newFile = !new File(filePath).exists();
                fileWriter = new FileWriter(filePath, true);
                //Write the CSV file header
                if (newFile) {
                    fileWriter.append(FILE_HEADER.toString());
                    fileWriter.append(NEW_LINE_SEPARATOR);
                }


                fileWriter.append(userLogin);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(managedSysId);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(sAMAccountName);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(permCert);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(dateTimeString);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(action);
                fileWriter.append(NEW_LINE_SEPARATOR);

                log.info("CSV file was created successfully !!!");

            } catch (Exception e) {
                log.error("Error in CsvFileWriter !!!");
                e.printStackTrace();
            } finally {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    if (debugMode) {
                        System.out.println("Error while flushing/closing fileWriter !!!");
                    }
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
    }

    private void setAssistantDNAttribute(ProvisionUser user) {
        UserDataWebService userWS = context.getBean('userWS')
        def attrs = userWS.getUserAttributes(user.alternateContactId)
        if (attrs) {
            def curAssistantDN = user.getAttribute('assistantDN')
            def assistantDNValue = attrs.find { a -> a.name == 'distinguishedName' }?.value
            assistantDNValue = assistantDNValue.replaceAll(',ou=', ',OU=')
            def ouPos = assistantDNValue?.indexOf(',OU=')
            if (ouPos > 0) {
                def cnDN = assistantDNValue.substring(0, ouPos)
                cnDN = cnDN.replaceAll(',', { '\\,' }).replaceAll(/\\\\${','}/, { '\\,' })
                assistantDNValue = cnDN + assistantDNValue.substring(ouPos);
                if (debugMode) {
                    println(">>>>>>>>>>>>>>>>>>>>>>>> assistantDNValue:" + assistantDNValue)
                }
            }
            if (!curAssistantDN && assistantDNValue) {
                def assAttr = new UserAttribute('assistantDN', assistantDNValue)
                assAttr.operation = AttributeOperationEnum.ADD
                user.saveAttribute(assAttr)
            } else if (curAssistantDN && assistantDNValue && curAssistantDN.value != assistantDNValue) {
                curAssistantDN.value = assistantDNValue
                curAssistantDN.operation = AttributeOperationEnum.REPLACE
            } else if (curAssistantDN?.value && !assistantDNValue) {
                curAssistantDN.operation = AttributeOperationEnum.DELETE
            }
        }
    }

    private void processUserID(UserAttribute attr, String principal) {
        if (attr?.value) {
            def value = attr.value.replaceAll("(?i)\\{userID\\}", "{userID}")
            if (attr.operation == AttributeOperationEnum.ADD ||
                    attr.operation == AttributeOperationEnum.REPLACE) {
                if (value?.contains('\${userID}')) {
                    value = value.replace('\${userID}', principal)
                    attr.value = value
                }
            } else if ((!attr.operation) || attr?.operation == AttributeOperationEnum.NO_CHANGE) {
                if (value?.contains('\${userID}')) {
                    value = value.replace('\${userID}', principal)
                    attr.value = value
                    attr.operation = AttributeOperationEnum.REPLACE
                }
            }
        }
    }

    private static String convertNonAscii(String originalString) {
        if (originalString == null) return null;
        StringBuilder sb = new StringBuilder();
        int n = originalString.length();
        for (int i = 0; i < n; i++) {
            String c = originalString.charAt(i) as String;
            int pos = UNICODE.indexOf(c);
            if (pos > -1) {
                sb.append(PLAIN_ASCII.charAt(pos));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String generateDisplayName(ProvisionUser user, Map<String, Object> bindingMap) {
        UserAttribute a = this.getUserAttributeByName(user, bindingMap, "USER_DISPLAY_NAME");
        UserAttribute savedFormat = this.getUserAttributeByName(user, bindingMap, "USER_SAVED_NAME");
        String savedFormatString = savedFormat?.value;
        String format = a?.value ? a?.value : (savedFormatString ? savedFormatString : "50");
        if (!formatValues.contains(format)) {
            format = savedFormatString;
            if (!formatValues.contains(format)) {
                format = "50";
            }
        }
        String displayName = a?.value;
        if (StringUtils.isBlank(user.middleInit) || "null".equalsIgnoreCase(user.middleInit)) {
            user.middleInit = user.firstName.substring(0, 1);
        }
        if (debugMode) {
            println("********************************** FORMAT IS=" + format)
        }
        if ("51".equals(format?.trim())) {
            String secondLastName = user.partnerName;
            String secondLastNamePrefix = user.prefixPartnerName;
            String userLastName = user.lastName;
            if (StringUtils.isNotBlank(secondLastName) && !"null".equalsIgnoreCase(secondLastName)) {
                secondLastName = (StringUtils.isNotBlank(secondLastNamePrefix) && !"null".equalsIgnoreCase(secondLastNamePrefix)) ?
                        "${secondLastNamePrefix} ${secondLastName}" : secondLastName;
                userLastName = "${user.lastName}-${secondLastName}";
            }
            displayName = genName(userLastName, user.firstName, user.middleInit, user.prefixLastName);
        } else if ("52".equals(format?.trim())) {
            String secondLastName = (StringUtils.isNotBlank(user.partnerName) && !"null".equalsIgnoreCase(user.partnerName)) ? user.partnerName : user.lastName;
            String secondLastNamePrefix = (StringUtils.isNotBlank(user.partnerName) && !"null".equalsIgnoreCase(user.partnerName)
                    && StringUtils.isNotBlank(user.prefixPartnerName)
                    && !"null".equalsIgnoreCase(user.prefixPartnerName)) ? user.prefixPartnerName : "";
            displayName = genName(secondLastName, user.firstName, user.middleInit, secondLastNamePrefix);
        } else if ("53".equals(format?.trim())) {
            String secondLastName = user.partnerName;
            String secondLastNamePrefix = user.prefixPartnerName;
            String userLastName = StringUtils.isNotBlank(user.prefixLastName) ? "${user.prefixLastName} ${user.lastName}" : user.lastName;
            if (StringUtils.isNotBlank(secondLastName) && !"null".equalsIgnoreCase(secondLastName)) {
                userLastName = "${secondLastName}-${userLastName}";
            }
            displayName = genName(userLastName, user.firstName, user.middleInit, secondLastNamePrefix);
        } else if (!format || "50".equals(format?.trim()) || "00".equals(format?.trim()) || "0".equals(format?.trim())) {
            displayName = genName(user.lastName, user.firstName, user.middleInit, user.prefixLastName);
        }
        if (a) {
            a.setValue(displayName);
        } else {
            addAttribute(user, "USER_DISPLAY_NAME", displayName, bindingMap);
        }
        if (savedFormat) {
            a.setValue(format);
        } else {
            addAttribute(user, "USER_SAVED_NAME", format, bindingMap);
        }
        return displayName;
    }

    public String genName(String lastName, String givenName, String initials, String infix) {
        initials = initials?.replaceAll(/\s+|\./, '')?.toUpperCase()
        if ((initials == null) || ("null".equalsIgnoreCase(initials))) {
            initials = ""
        }
        def inits = ''
        initials?.each { l ->
            inits += (l + ".")
        }
        if ((infix == null) || ("null".equalsIgnoreCase(infix))) {
            infix = ""
        }

        if ((givenName == null) || ("null".equalsIgnoreCase(givenName))) {
            givenName = ""
        }
        if ((lastName == null) || ("null".equalsIgnoreCase(lastName))) {
            lastName = ""
        }
        return (lastName + ", ${inits}" + (infix ? " " + infix : "") + " (" + givenName + ")")
    }

    private boolean processMetadataType(ProvisionUser user, Map<String, Object> bindingMap) {

        boolean isChanged = false;
        String mailboxValue = getUserAttributeByName(user, bindingMap, "mailbox")?.value;

        if (!mailboxValue) {
            mailboxValue = user.getAttribute("mailbox")?.value;

        }
        if (!user.id && !user.mdTypeId && !mailboxValue) {

            user.mdTypeId = "AKZONOBEL_USER_NO_MBX";
        } else if (mailboxValue && !"NONE".equalsIgnoreCase(mailboxValue)) {

            if (user.mdTypeId == null || user.mdTypeId?.equalsIgnoreCase("AKZONOBEL_USER_NO_MBX")) {

                user.mdTypeId = "AKZONOBEL_USER_MBX";
                isChanged = true;
            }
        }

        String source = getUserAttributeByName(user, bindingMap, "USER_CREATION_SOURCE")?.value;
        if (!source) {
            source = user.getAttribute("USER_CREATION_SOURCE")?.value;

        }

        if ("UI".equalsIgnoreCase(source) && !user.id) {
            isChanged = true;
        }

        return isChanged;
    }

    private void processMailBox(ProvisionUser user, Map<String, Object> bindingMap) {
        String mailboxValue = getUserAttributeByName(user, bindingMap, "mailbox")?.value;
        if (!mailboxValue) {
            mailboxValue = user.getAttribute("mailbox")?.value;
        }
        if (mailboxValue) {
            if (debugMode) {
                println("Mailbox value=" + mailboxValue)
            }
            switch (mailboxValue) {
                case "NONE":
                    user.getAttribute("mailbox")?.setValue("");
                    addAttribute(user, "mailbox", "", bindingMap);
                    break;
                case "O365":
                    //user.getAttribute("mailbox")?.setValue("");
                    //addAttribute(user, "mailbox", "Office365", bindingMap);
                    addAttribute(user, "archieve", "Cached - Laptop", bindingMap);
                    addGroup(user, GRP_VAULT_CACHE_ENABLED);
                    addGroup(user,GRP_INTUNE_ENABLED);
                    break;
                case "STANDARD":
                    if (debugMode) {
                        println("Mailbox value COME TO STANDARD=" + mailboxValue)
                    }
                    user.getAttribute("mailbox")?.setValue("Regular");
                    addAttribute(user, "mailbox", "Regular", bindingMap);
                    addAttribute(user, "archieve", "Cached - Laptop", bindingMap);
                    addGroup(user, GRP_VAULT_CACHE_ENABLED);
                    break;
                case "LIGHT":
                    user.getAttribute("mailbox")?.setValue("Small");
                    addAttribute(user, "mailbox", "Small", bindingMap);
                    break;
                case "MEDIUM":
                    user.getAttribute("mailbox")?.setValue("Medium");
                    addAttribute(user, "mailbox", "Medium", bindingMap);
                    addAttribute(user, "archieve", "Cached - Laptop", bindingMap);
                    addGroup(user, GRP_VAULT_CACHE_ENABLED);
                    break;
                default: break;
            }
        }
        if ("AKZONOBEL_USER_MBX".equals(user.mdTypeId)) {
            if (!user.emailAddresses || CollectionUtils.isEmpty(user.emailAddresses)) {
                EmailAddress email = new EmailAddress();
                email.setEmailAddress(getUserAttributeByName(user, bindingMap, "UserPrincipalName")?.value);
                email.setIsActive(true);
                email.setIsDefault(true);
                email.setMetadataTypeId("PRIMARY_EMAIL");
                email.operation = AttributeOperationEnum.ADD;
                user.emailAddresses = new HashSet<EmailAddress>();
                user.emailAddresses.add(email);
                addRole(user, "EXCHANGE_ROLE_ID");
            }
        }
    }


    private void fillLocation(Location location, ProvisionUser user) {
        def locationId = location.locationId
        def locationIdAttr = new UserAttribute("LOCATION_ID", locationId)
        locationIdAttr.operation = AttributeOperationEnum.ADD
        user.saveAttribute(locationIdAttr)


        def postalCode = location.postalCd
        def postalCodeAttr = new UserAttribute("POSTAL_CODE", postalCode)
        postalCodeAttr.operation = AttributeOperationEnum.ADD
        user.saveAttribute(postalCodeAttr)

        def city = location.city
        def cityAttr = new UserAttribute("CITY", city)
        cityAttr.operation = AttributeOperationEnum.ADD
        user.saveAttribute(cityAttr)

        def state = location.state
        def stateAttr = new UserAttribute("STATE", state)
        stateAttr.operation = AttributeOperationEnum.ADD
        user.saveAttribute(stateAttr)

        def streetAddress = location.address1
        def streetAttr = new UserAttribute("STREET_ADDRESS", streetAddress)
        streetAttr.operation = AttributeOperationEnum.ADD
        user.saveAttribute(streetAttr)

        def country = location.country
        def countryAttr = new UserAttribute("COUNTRY", country)
        countryAttr.operation = AttributeOperationEnum.ADD
        user.saveAttribute(countryAttr)

        def country2alfa = location.country
        if (country2alfa && country2alfa.length() == 2) {
            CountryCode cc = CountryCode.getByAlpha2Code(country2alfa)
            def String countryCode1 = cc?.numeric ?: null
            println "string"
            def countryCodeAttr = new UserAttribute("COUNTRY_CODE", countryCode1)
            countryCodeAttr.operation = AttributeOperationEnum.ADD
            user.saveAttribute(countryCodeAttr)
        }
        def country2alfa1 = location.country
        if (country2alfa1 && country2alfa1.length() == 2) {
            CountryCode cc = CountryCode.getByAlpha2Code(country2alfa1)
            def countryCode1 = cc?.name ?: null
            def countryCoAttr = new UserAttribute("COUNTRY_CO", countryCode1)
            countryCoAttr.operation = AttributeOperationEnum.ADD
            user.saveAttribute(countryCoAttr)
        }
        populateAddress(user, location);
    }


    private provisionToLocation(ProvisionUser user, Map<String, Object> bindingMap, boolean isADD) {
        if (isADD) {
            addRole(user, DEFAULT_HOLDING_ROLE_ID);
        }
        //I know that this is not good place to update clasification, but method calles from both add amd modify
        if (user.userSubTypeId) {
            addAttribute(user, "classification", user.userSubTypeId, bindingMap);
            user.userSubTypeId = null;
        }
        String siteCode = getUserAttributeByName(user, bindingMap, "siteCode")?.value;
        def orgManager = context.getBean('orgManager') as OrganizationDataService
        Organization org = this.getCurrentOrganization(orgManager, user, siteCode);
        if (debugMode) {
            println("Start provision to pass")
        }
        String companyName = org?.internalOrgId;
        addAttribute(user, "BU_CODE", companyName, bindingMap);
        addAttribute(user, "ORG_NAME", org?.name, bindingMap);
        addAttribute(user, "ORG_BA_SHORT_NAME", org?.attributes?.find({
            it.name.equals("GlobalOrgUnit Business Area Shortname")
        })?.value, bindingMap);
        String adPath = this.getUserAttributeByName(user, bindingMap, "AD_PATH")?.value;
        if (isADD && !adPath) {
            adPath = defaulADPath;
        }
        if (companyName) {
            if (siteCode) {
                Organization siteOrg = org.getChildOrganizations().find({ it.internalOrgId.equals(siteCode) });
                if (siteOrg) {
                    def siteAttributes = siteOrg?.attributes;
                    def cluster = siteAttributes.find({ "CLUSTER".equalsIgnoreCase(it.name) && it.value })?.value
                    if (cluster) {
                        this.addAttribute(user, "CLUSTER", cluster, bindingMap)
                    }

                    def region = siteAttributes.find({ "REGION".equalsIgnoreCase(it.name) && it.value })?.value
                    if (region) {
                        this.addAttribute(user, "REGION", region, bindingMap)
                    }

                    def serviceType = siteAttributes.find({
                        "SERVICE_TYPE".equalsIgnoreCase(it.name) && it.value
                    })?.value
                    if (serviceType) {
                        this.addAttribute(user, "serviceType", serviceType, bindingMap)
                    }
                    String changeADPath = this.getUserAttributeByName(user, bindingMap, "OU_CHANGE_ALLOW")?.value;
                    if (!adPath || "Y".equals(changeADPath) || defaulADPath.equalsIgnoreCase(adPath)) {
                        if (defaulADPath.equalsIgnoreCase(adPath)) {
                            this.addAttribute(user, "SEND_ON_UPDATE", "1", bindingMap);
                        }
                        adPath = siteAttributes.find({
                            "AD_PATH".equalsIgnoreCase((String) it.name) && it.value
                        })?.value
                        this.addAttribute(user, "OU_CHANGE_ALLOW", "N", bindingMap);
                    }
                    if (debugMode) {
                        println("AD_PATH from role attribute=[" + adPath + "]")
                    }
                    if (this.currentLocation) {
                        fillLocation(this.currentLocation, user);
                    } else {
                        sendCanNotMapSiteAndBU(user, siteCode, companyName, true);
                        this.addAttribute(user, "ERROR:LOCATION", "No location for ${siteCode}_${companyName}", bindingMap)
                    }
                } else {
                    this.addAttribute(user, "ERROR:SITECODE ORGANIZATION", "No Site Code ${siteCode} organization for BU ${companyName}", bindingMap)
                    sendCanNotMapSiteAndBU(user, siteCode, companyName, false);
                }
            }
        }
        if (!adPath) {
            //try to extract AD_PATH FROM Distiguidhed Name
            adPath = extractOrgRoleName(user, bindingMap);
        }
        if (!adPath) {
            adPath = defaulADPath;
        }
        if (adPath) {
            this.addAttribute(user, "AD_PATH", adPath, bindingMap)
        }
        if (!updateDn(user, bindingMap, adPath, orgManager)) {
            sendError(user,"4. update DN fail during provisionToLocation")
            return ProvisioningConstants.FAIL
        }
        return ProvisioningConstants.SUCCESS
    }

    private String extractOrgRoleName(ProvisionUser pUser, Map<String, Object> bindingMap) {
        String distinguishedName = this.getUserAttributeByName(pUser, bindingMap, "distinguishedName")
        if (!distinguishedName) {
            return;
        }
        String adPath = null;
        if (distinguishedName.contains("OU=UserTransfer,DC=d30,DC=intra")) {
            adPath = "OU=UserTransfer,DC=d30,DC=intra"
        } else {
            Pattern pattern = Pattern.compile(".*OU=(Users|Administrators|ResourceMailbox|Service Accounts|Services Accounts|External Accounts|Resources|Remote Access Users|Expired),(.*)".toLowerCase());
            Matcher matcher = pattern.matcher(distinguishedName.toLowerCase());
            if (matcher.matches()) {
                adPath = matcher.group(2);
            }
        }
        return adPath;
    }

    private static final String PLAIN_ASCII =
            "AaEeIiOoUuAaEeIiOoUuYyAaEeIiOoUuYyAaOoNnAaEeIiOoUuYyAaCcOoUu";

    private static final String UNICODE =
            "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF\u00C5\u00E5\u00C7\u00E7\u0150\u0151\u0170\u0171";


    private void addRole(ProvisionUser pUser, String id) {
//        def roleWS = context.getBean('roleWS') as RoleDataWebService
        def r = pUser.roles.find({ it.id == id })
        if (!r) {
            def role = roleWS.getRole(id, "3000")
            if (role) {
                role.operation = AttributeOperationEnum.ADD
                pUser.roles.add(role)
                if ("MDM_ROLE_ID".equalsIgnoreCase(id)) {
                    addGroup(pUser, g_GSS_MDMEmailWMS_ID)
                    addGroup(pUser, g_GSS_MDMUsers_ID)
                }

            } else {
                println "Warning: role with id '" + id + "' is not found"
            }
        } else {
            r.operation = AttributeOperationEnum.NO_CHANGE
        }
    }

    private void addGroup(ProvisionUser pUser, String id) {
        def groupWS = context.getBean('groupWS') as GroupDataWebService
        def g = pUser.groups.find({ it.id == id })
        if (!g) {
            def group = groupWS.getGroup(id, "3000")
            if (group) {
                group.operation = AttributeOperationEnum.ADD
                pUser.groups.add(group)
            } else {
                println "Warning: group with id '" + id + "' is not found"
            }
        } else {

            g.operation = AttributeOperationEnum.NO_CHANGE
        }
    }

    private boolean isActive(Date startDate) {
        /*boolean val = false;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        Date date = cal.getTime();
        val = date.after(startDate)
        if (debugMode) {
            println("Start date=" + startDate);
            println("Resolution to activate now=" + val);
        }
        return val; */
        return true;
    }


    private boolean isDisable(Date lastDate) {
        if (!lastDate) {
            return false;
        }

        Calendar defaultDate = Calendar.getInstance();
        defaultDate.set(1972, Calendar.JANUARY, 1);

        boolean retVal = false;

        Calendar disableDate = Calendar.getInstance();
        disableDate.setTime(lastDate);
        disableDate.add(Calendar.DATE, 1);

        if (disableDate.before(defaultDate)) {
            return false;
        }


        retVal = disableDate.before(Calendar.getInstance());
        if (debugMode) {
            retVal ? println("DISABLE NOW!!!") : println("DISABLE LATER!!!");
        }
        return retVal;
    }

    private Organization getCurrentOrganization(OrganizationDataService orgManager, ProvisionUser user, String siteCode) {
        long time = System.currentTimeMillis();
        if (currectOrganization != null) {
            if (debugMode) {
                println("Current organization exists=" + currectOrganization?.name)
            }
            return this.currectOrganization;
        }
        if (debugMode) {
            println("Current organization exists in users=" + user?.organizationUserDTOs?.size())
            for (OrganizationUserDTO oud : user?.getOrganizationUserDTOs()) {
                println(String.format("%s/%s/%s", oud.mdTypeId, oud.operation, oud.organization?.id));
            }
        }
        Organization organization = null;
        String organizationId = user?.getOrganizationUserDTOs()?.find({
            "DEFAULT_AFFILIATION".equals(it.mdTypeId) && !AttributeOperationEnum.DELETE.equals(it.operation)
        })?.organization?.id;
        if (debugMode) {
            println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ORG FROM USER ADD=" + organizationId)
        }
        if (!organizationId && user?.id) {
            def orgList = orgManager.getUserAffiliationsByType(user.id, "DEFAULT_AFFILIATION", 0, 10, "3000", null);
            if (debugMode) {
                println("COME HERE TO FIND ORG=" + orgList?.size());
            }
            if (orgList && orgList.size() > 0) {
                if (debugMode) {
                    println("COME HERE TO FIND COMPANY NAME");
                }
                OrganizationSearchBean osb = new OrganizationSearchBean();
                osb.setFindInCache(true);
                osb.setDeepCopy(true);
                osb.forCurrentUsersOnly = true;
                Set<String> userIds = new HashSet<String>();
                userIds.add(user.id);
                osb.setUserIdSet(userIds);
                organization = orgManager.findBeans(osb, null, 0, 1)?.get(0);
            }
        } else {
            if (debugMode) {
                println("COME HERE TO FIND COMPANY NAME");
            }
            OrganizationSearchBean osb = new OrganizationSearchBean();
            osb.setFindInCache(true);
            osb.setDeepCopy(true);
            osb.forCurrentUsersOnly = true;
            osb.setKey(organizationId);
            organization = orgManager.findBeans(osb, null, 0, 1)?.get(0);
        }
//        List<OrganizationAttribute> attrs = orgManager.getOrganizationAttributes(organization?.getId())
//
//        if (attrs) {
//            organization.setAttributes(new HashSet<OrganizationAttribute>(attrs));
//        }

        if (siteCode) {
            if (debugMode) {
                println("siteCode is ${siteCode}");
            }
            Organization childOrg = organization.getChildOrganizations().find({ siteCode.equals(it.internalOrgId) });
            if (childOrg) {
                println("childOrg is ${childOrg?.name}");
                if (CollectionUtils.isEmpty(childOrg.getAttributes())) {
                    List<OrganizationAttribute> organizationAttributeList = orgManager.getOrganizationAttributes(childOrg.id)

                    if (organizationAttributeList) {
                        println("organizationAttributeList is ${organizationAttributeList?.size()}");
                        organization.getChildOrganizations().find({
                            siteCode.equals(it.internalOrgId)
                        }).setAttributes(new HashSet<OrganizationAttribute>(organizationAttributeList));
                    }
                }
                if (currentLocation == null || !currentLocation.organizationId.equals(childOrg.id)) {
                    List<Location> locations = orgManager.getLocationList(childOrg.id);
                    currentLocation = locations?.size() > 0 ? locations.first() : null;
                }
            }
        }

        currectOrganization = organization;
        if (debugMode) {

            println("ORGANIZATION childs=" + organization?.childOrganizations?.size())
            println("ORGANIZATION attrs=" + organization?.attributes?.size())
            if (organization?.childOrganizations) {
                for (Organization org : organization?.childOrganizations) {
                    println("CHILD ORG ATTR=" + org?.attributes?.size());
                }
            }
            println("currect Location =" + currentLocation?.name)

            println("Time for organization=" + (System.currentTimeMillis() - time));
        }
        return organization;
    }


    private void populateAddress(User user, Location location) {
        def address = user.addresses?.find({ true }) ?: new Address()
        if (location) {
            address.city = location.city
            address.state = location.state
            address.country = location.country
            address.postalCd = location.postalCd
            address.address1 = location.address1
            address.metadataTypeId = "OFFICE_ADDRESS";
        }
        address.operation = address.addressId ? AttributeOperationEnum.REPLACE : AttributeOperationEnum.ADD;
        if (AttributeOperationEnum.ADD.equals(address.operation)) {
            user.addresses.add(address);
        }
    }

    def addDisabledAttributes(ProvisionUser user, Map<String, Object> bindingMap) {
        if (!getUserAttributeByName(user, bindingMap, "userEditActionInfo")) {
            addAttribute(user, "userEditActionInfo", "myHR", bindingMap)
        }
        if (!getUserAttributeByName(user, bindingMap, "DEACTIVATION_DATE")) {
            Date nextMonthDateTime = user.lastDate
            nextMonthDateTime = nextMonthDateTime.plus(30)
            def df = new SimpleDateFormat("dd.MM.yyyy")
            String formatedDate = df.format(nextMonthDateTime);
            addAttribute(user, "DEACTIVATION_DATE", formatedDate, bindingMap)
        }
    }

    def isProlonged(ProvisionUser user) {
        if (!user.lastDate) {
            return false;
        }
        boolean retVal = false;
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(user.lastDate);
        retVal = lastDate.after(Calendar.getInstance());
        if (retVal && user.getStatus() == UserStatusEnum.LEAVE && user.getSecondaryStatus() == UserStatusEnum.DISABLED) {
            if (debugMode)
                println("Starting rehire process for user " + user.displayName)
            return true

        } else return false
    }

    def rehire(ProvisionUser user) {
        def groupWS = context.getBean('groupWS') as GroupDataWebService
        def userDataService = context.getBean('userManager') as UserDataService
        def String emailAddress = "AN" + user.getEmployeeId() + ".iamterm@akzonobel.com"
        def userEmailList = userDataService.getEmailAddressDtoList(user.id,true)
        for (EmailAddress oldEmails : userEmailList) {
            if (oldEmails.emailAddress.equalsIgnoreCase(emailAddress)) {
                oldEmails.operation = AttributeOperationEnum.DELETE
                user.emailAddresses.add(oldEmails)
            } else {
                oldEmails.setIsDefault(true)
                oldEmails.setMetadataTypeId("PRIMARY_EMAIL")
                oldEmails.setIsActive(true)
                oldEmails.operation = AttributeOperationEnum.REPLACE;
                user.emailAddresses.add(oldEmails)
            }
        }

        user.setStatus(UserStatusEnum.ACTIVE)
        user.setSecondaryStatus(null)

        boolean disableGrpExist = false;
        for (Group gr : user.getGroups()) {
            if (m_Unity_Disabled_Users_exists_ID.equals(gr.getId())) {

                disableGrpExist = true;
                break;
            }
        }
        if (disableGrpExist) {
            Group grp = groupWS.getGroup(m_Unity_Disabled_Users_exists_ID, "3000")
//user.markGroupAsDeleted(m_Unity_Disabled_Users_exists_ID)
            grp.setOperation(AttributeOperationEnum.DELETE)
        } else {
            println "delete GRP Unity_Disabled_Users for rehire"
            Group grp = groupWS.getGroup(m_Unity_Disabled_Users_exists_ID, "3000")
            if (grp) {
                grp.operation = AttributeOperationEnum.DELETE
                user.groups << grp
            }
        }
    }
}


