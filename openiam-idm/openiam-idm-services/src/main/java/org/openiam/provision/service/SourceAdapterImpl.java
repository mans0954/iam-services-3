package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SearchParam;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.searchbeans.*;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.org.service.OrganizationDataServiceImpl;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationResponse;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.*;
import org.openiam.provision.dto.srcadapter.*;
import org.openiam.provision.resp.PasswordResponse;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import javax.jws.WebService;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zaporozhec on 10/29/15.
 */
@WebService(endpointInterface = "org.openiam.provision.service.SourceAdapter", targetNamespace = "http://www.openiam.org/service/provision", portName = "SourceAdapterServicePort", serviceName = "SourceAdapterService")
@Component("sourceAdapter")
public class SourceAdapterImpl implements SourceAdapter {

    @Autowired
    private ProvisioningDataService provisioningDataService;
    @Autowired
    private UserDataWebService userDataService;
    @Autowired
    private GroupDataWebService groupDataWebService;
    @Autowired
    private RoleDataWebService roleDataWebService;
    @Autowired
    private ResourceDataService resourceDataService;
    @Autowired
    private OrganizationDataService organizationDataService;
    @Autowired
    private MetadataWebService metadataWS;
    @Autowired
    private ManagedSystemWebService managedSysService;
    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    protected AuditLogService auditLogService;

    final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
    final static String WARNING = "Warning! %s.\n";
    private String source;

    @Override
    public SourceAdapterResponse perform(SourceAdapterRequest request) {
        SourceAdapterResponse response = new SourceAdapterResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        StringBuilder warnings = new StringBuilder();
        IdmAuditLog idmAuditLog = new IdmAuditLog();

        if (request.isForceMode()) {
            idmAuditLog.addCustomRecord("Skip Warnings", "true");
            warnings.append(getWarning("Warnings will be skipped!"));
        }
        String requestorId = null;
        try {

            if (request.getAction() == null) {
                throw new Exception("Can't process... 'action' is not defined!");
            }

            User requestor = this.getUser(request.getRequestor(), request);
            if (requestor != null && StringUtils.isNotBlank(requestor.getId())) {
                requestorId = requestor.getId();
            }
            if (StringUtils.isBlank(requestorId)) {
                throw new Exception("Requestor not found");
            }
            idmAuditLog.setRequestorUserId(requestorId);
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setError(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            auditLogService.save(idmAuditLog);
            return response;
        }
        ProvisionUser pUser = null;
        try {
            pUser = this.convertToProvisionUser(request, warnings, requestorId);
            if (SourceAdapterOperationEnum.ADD.equals(request.getAction()) && StringUtils.isNotBlank(pUser.getId())) {
                throw new Exception("Such user exists. Can't add! User=" + pUser.getDisplayName());
            }
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setError(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            auditLogService.save(idmAuditLog);
            return response;
        }
        idmAuditLog.setUserId(pUser.getId());
        idmAuditLog.setAction(AuditAction.SOURCE_ADAPTER_CALL.value());
        idmAuditLog.setSource("Source Adapter");
        idmAuditLog.setAuditDescription("Operation:" + request.getAction().name());
        if (warnings.length() > 0 && !request.isForceMode()) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setError(warnings.toString());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(warnings.toString());
            auditLogService.save(idmAuditLog);
            return response;
        }
        switch (request.getAction()) {
            case ADD: {
                pUser.setOperation(AttributeOperationEnum.ADD);
                ProvisionUserResponse provisionUserResponse = provisioningDataService.addUser(pUser);
                response.setStatus(provisionUserResponse.getStatus());
                response.setError(provisionUserResponse.getErrorText());
                break;
            }
            case MODIFY: {
                pUser.setOperation(AttributeOperationEnum.REPLACE);
                ProvisionUserResponse provisionUserResponse = provisioningDataService.modifyUser(pUser);
                response.setStatus(provisionUserResponse.getStatus());
                response.setError(provisionUserResponse.getErrorText());
                break;
            }
            case DELETE: {
                ProvisionUserResponse provisionUserResponse = provisioningDataService.deleteByUserWithSkipManagedSysList(pUser.getId(), UserStatusEnum.REMOVE, requestorId, null);
                response.setStatus(provisionUserResponse.getStatus());
                response.setError(provisionUserResponse.getErrorText());
                break;
            }
            case ENABLE: {
                Response resp = provisioningDataService.disableUser(pUser.getId(), false, requestorId);
                response.setStatus(resp.getStatus());
                response.setError(resp.getErrorText());
                break;
            }
            case DISABLE: {
                Response resp = provisioningDataService.disableUser(pUser.getId(), true, requestorId);
                response.setStatus(resp.getStatus());
                response.setError(resp.getErrorText());
                break;
            }
            case CHANGE_PASSWORD: {
                if (request.getPasswordRequest() != null) {
                    PasswordSync passwordSync = new PasswordSync();
                    passwordSync.setUserId(pUser.getId());
                    passwordSync.setRequestorId(requestorId);
                    passwordSync.setManagedSystemId(request.getPasswordRequest().getManagedSystemId());
                    passwordSync.setPassword(request.getPasswordRequest().getPassword());
                    passwordSync.setSendPasswordToUser(request.getPasswordRequest().isSendToUser());
                    passwordSync.setUserActivateFlag(request.getPasswordRequest().isActivate());
                    PasswordValidationResponse resetPasswordResponse = provisioningDataService.setPassword(passwordSync);
                    response.setStatus(resetPasswordResponse.getStatus());
                    response.setError(resetPasswordResponse.getErrorText());
                } else {
                    response.setStatus(ResponseStatus.FAILURE);
                    warnings.append("Change password request is empty");
                }
                break;
            }
            case RESET_PASSWORD: {
                if (request.getPasswordRequest() != null) {
                    PasswordSync passwordSync = new PasswordSync();
                    passwordSync.setUserId(pUser.getId());
                    passwordSync.setRequestorId(requestorId);
                    passwordSync.setManagedSystemId(request.getPasswordRequest().getManagedSystemId());
                    passwordSync.setPassword(request.getPasswordRequest().getPassword());
                    passwordSync.setSendPasswordToUser(request.getPasswordRequest().isSendToUser());
                    passwordSync.setUserActivateFlag(request.getPasswordRequest().isActivate());
                    PasswordResponse resetPasswordResponse = provisioningDataService.resetPassword(passwordSync);
                    response.setStatus(resetPasswordResponse.getStatus());
                    response.setError(resetPasswordResponse.getErrorText());
                } else {
                    response.setStatus(ResponseStatus.FAILURE);
                    warnings.append("Reset password request is empty");
                }
                break;
            }
            case NO_CHANGE: {
                break;
            }
            default:
                response.setStatus(ResponseStatus.FAILURE);
                response.setError("Operation not supported");
                idmAuditLog.fail();
                idmAuditLog.setFailureReason("Operation not supported");
                auditLogService.save(idmAuditLog);
        }
        response.setError(warnings.toString());
        if (ResponseStatus.FAILURE.equals(response.getStatus())) {
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(warnings.toString());
            auditLogService.save(idmAuditLog);
        }
        return response;
    }

    @Override
    public SourceAdapterInfoResponse info() {
        SourceAdapterInfoResponse response = new SourceAdapterInfoResponse();
        MetadataTypeSearchBean metadataTypeSearchBean = new MetadataTypeSearchBean();
        metadataTypeSearchBean.setDeepCopy(false);
        metadataTypeSearchBean.setActive(true);
        List<String> notes = new ArrayList<String>();
        notes.add(this.getKeyNote());
        notes.add(this.getWarningNote());
        notes.add(this.getManagedSystems());
        notes.add(this.getNote(metadataTypeSearchBean, MetadataTypeGrouping.ADDRESS));
        notes.add(this.getNote(metadataTypeSearchBean, MetadataTypeGrouping.AFFILIATIONS));
        notes.add(this.getNote(metadataTypeSearchBean, MetadataTypeGrouping.EMAIL));
        notes.add(this.getNote(metadataTypeSearchBean, MetadataTypeGrouping.PHONE));
        notes.add(this.getNote(metadataTypeSearchBean, MetadataTypeGrouping.USER_TYPE));
        response.setNotes(notes);
        return response;
    }

    private String getManagedSystems() {
        StringBuilder sb = new StringBuilder("Available Managed System Ids (for principals) \n");
        List<ManagedSysDto> managedSysDtos = managedSysService.getAllManagedSys();
        if (CollectionUtils.isNotEmpty(managedSysDtos)) {
            for (ManagedSysDto managedSysDto : managedSysDtos) {
                if ("ACTIVE".equals(managedSysDto.getStatus())) {
                    sb.append("id:");
                    sb.append(managedSysDto.getId());
                    sb.append("/Name:");
                    sb.append(managedSysDto.getName());
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    private String getKeyNote() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<key>,<requestor>,<user-supervisor> entities are working with name/value attributes\n");
        sb.append("This means that user will be found by this key. Available key values are:\n");
        sb.append("'user_id' - find by internal user Id\n");
        sb.append("'email' - find by primary email\n");
        sb.append("'employee_id' - find by employee Id\n");
        return sb.toString();
    }

    private String getWarningNote() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<skipWarnings> entity provide ability to skip all validation warnings and perform request\n");
        sb.append("It may be wrong operation or role/group/role with unavailable names\n");
        sb.append("In this case only wrong parts will be ignored\n");
        return sb.toString();
    }

    private String getNote(MetadataTypeSearchBean metadataTypeSearchBean, MetadataTypeGrouping name) {
        metadataTypeSearchBean.setGrouping(name);
        List<MetadataType> types = metadataWS.findTypeBeans(metadataTypeSearchBean, 0, Integer.MAX_VALUE, null);
        StringBuilder sb = new StringBuilder();
        sb.append("\nAvailable types for ");
        sb.append(name.name());
        sb.append("\n");
        if (CollectionUtils.isNotEmpty(types)) {
            for (MetadataType type : types) {
                sb.append("Description:");
                sb.append(type.getDescription());
                sb.append("/Value:");
                sb.append(type.getId());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private ProvisionUser convertToProvisionUser(SourceAdapterRequest request, StringBuilder warnings, String requestorId) throws Exception {
        ProvisionUser pUser = new ProvisionUser(this.getUser(request.getKey(), request));
        this.fillProperties(request, pUser);
        this.fillGroups(pUser, request, warnings, requestorId);
        this.fillRoles(pUser, request, warnings, requestorId);
        this.fillResources(pUser, request, warnings);
        fillAddresses(pUser, request, warnings);
        fillPhones(pUser, request, warnings);
        fillEmail(pUser, request, warnings);
        fillUserAttribute(pUser, request, warnings);
        fillOrganizations(pUser, request, warnings, requestorId);
        fillSuperVisors(pUser, request, warnings);
        fillPrincipals(pUser, request, warnings, requestorId);
        //superiors
        return pUser;
    }

    private void fillProperties(SourceAdapterRequest request, ProvisionUser pUser) throws Exception {
        if (StringUtils.isNotBlank(request.getEmployeeId())) {
            pUser.setEmployeeId(getNULLValue(request.getEmployeeId()));
        }
        if (StringUtils.isNotBlank(request.getFirstName())) {
            pUser.setFirstName(getNULLValue(request.getFirstName()));
        }
        if (StringUtils.isNotBlank(request.getLastName())) {
            pUser.setLastName(getNULLValue(request.getLastName()));
        }
        if (request.getLastDate() != null) {
            pUser.setLastDate("null".equals(request.getLastDate()) ? null : sdf.parse(request.getLastDate()));
        }
        if (request.getStartDate() != null) {
            pUser.setStartDate("null".equals(request.getStartDate()) ? null : sdf.parse(request.getStartDate()));
        }
        if (StringUtils.isNotBlank(request.getMaidenName())) {
            pUser.setMaidenName(getNULLValue(request.getMaidenName()));
        }
        if (StringUtils.isNotBlank(request.getMiddleName())) {
            pUser.setMiddleInit(getNULLValue(request.getMiddleName()));
        }
        if (StringUtils.isNotBlank(request.getNickname())) {
            pUser.setNickname(getNULLValue(request.getNickname()));
        }
        if (StringUtils.isNotBlank(request.getPrefix())) {
            pUser.setPrefix(getNULLValue(request.getPrefix()));
        }
        if (request.getSecondaryStatus() != null) {
            pUser.setSecondaryStatus(request.getSecondaryStatus());
        }
        if (request.getStatus() != null) {
            pUser.setStatus(request.getStatus());
        }
        if (StringUtils.isNotBlank(request.getSex())) {
            pUser.setSex(getNULLValue(request.getSex()));
        }
        if (StringUtils.isNotBlank(request.getSuffix())) {
            pUser.setSuffix(getNULLValue(request.getSuffix()));
        }
        if (StringUtils.isNotBlank(request.getTitle())) {
            pUser.setTitle(getNULLValue(request.getTitle()));
        }
        if (StringUtils.isNotBlank(request.getUserTypeId())) {
            pUser.setUserTypeInd(getNULLValue(request.getUserTypeId()));
        }
        if (StringUtils.isNotBlank(request.getUserSubTypeId())) {
            pUser.setUserSubTypeId(getNULLValue(request.getUserSubTypeId()));
        }
        if (StringUtils.isNotBlank(request.getPrefixLastName())) {
            if (request.getPrefixLastName().length() > 10)
                throw new Exception("Lenght of preffix last name must be not more than 10 characters");
            pUser.setPrefixLastName(getNULLValue(request.getPrefixLastName()));
        }
        if (StringUtils.isNotBlank(request.getPartnerName())) {
            if (request.getPartnerName().length() > 60)
                throw new Exception("Lenght of Partner name must be not more than 60 characters");
            pUser.setPartnerName(getNULLValue(request.getPartnerName()));
        }
        if (StringUtils.isNotBlank(request.getPrefixPartnerName())) {
            if (request.getPrefixPartnerName().length() > 10)
                throw new Exception("Lenght of preffix partner name must be not more than 10 characters");
            pUser.setPrefixPartnerName(getNULLValue(request.getPrefixPartnerName()));
        }
    }

    private void fillPrincipals(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings, String requestorId) throws Exception {
        if (CollectionUtils.isNotEmpty(request.getLogins())) {
            boolean isFound;
            if (pUser.getPrincipalList() == null) {
                pUser.setPrincipalList(new ArrayList<Login>());
            }
            for (SourceAdapterLoginRequest loginRequest : request.getLogins()) {
                if (loginRequest.getOperation() == null) {
                    warnings.append(getWarning("Incorrect operation for group=" + loginRequest.getLogin() + " Skip this!"));
                    continue;
                }
                isFound = false;
                for (Login l : pUser.getPrincipalList()) {
                    if (l.getManagedSysId().equals(loginRequest.getManagedSystemId())) {
                        if (AttributeOperationEnum.DELETE.equals(loginRequest.getOperation())) {
                            //delete
                            l.setOperation(AttributeOperationEnum.DELETE);
                        } else if (AttributeOperationEnum.REPLACE.equals(loginRequest.getOperation())) {
                            //replace
                            populateLogin(loginRequest, l, AttributeOperationEnum.REPLACE);
                        } else if (AttributeOperationEnum.ADD.equals(loginRequest.getOperation())) {
                            //add
                            populateLogin(loginRequest, l, AttributeOperationEnum.REPLACE);
                            warnings.append(this.getWarning("Can't ADD this login. Login for this managed system has already existed for user (Updating founded) =" + loginRequest.getLogin()));
                        }
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    if (AttributeOperationEnum.ADD.equals(loginRequest.getOperation()) || AttributeOperationEnum.REPLACE.equals(loginRequest.getOperation())) {
                        if (AttributeOperationEnum.REPLACE.equals(loginRequest.getOperation()))
                            warnings.append(this.getWarning("Can't replace this login. (call ADD instead of REPLACE)=" + loginRequest.getLogin() + ". Skip it."));

                        Login l = new Login();
                        populateLogin(loginRequest, l, AttributeOperationEnum.ADD);
                        pUser.addPrincipal(l);
                    }
                }
            }
        }
    }

    private static void populateLogin(SourceAdapterLoginRequest loginRequest, Login login, AttributeOperationEnum operation) {
        login.setOperation(operation);
        login.setLogin(StringUtils.isBlank(loginRequest.getNewLogin()) ? loginRequest.getLogin() : loginRequest.getNewLogin());
        login.setManagedSysId(loginRequest.getManagedSystemId());
    }

    private void fillGroups(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings, String
            requestorId) throws Exception {
        if (CollectionUtils.isNotEmpty(request.getGroups())) {
            boolean isFound;
            if (pUser.getGroups() == null) {
                pUser.setGroups(new HashSet<Group>());
            }
            for (SourceAdapterEntityManagedSystemRequest group : request.getGroups()) {
                if (group.getOperation() == null) {
                    warnings.append(getWarning("Incorrect operation for group=" + group.getName() + " Skip this!"));
                    continue;
                }
                if (AttributeOperationEnum.REPLACE.equals(group.getOperation())) {
                    warnings.append(this.getWarning("'replace' operation not supported for groups entitlement. Skip it. Group Name=" + group.getName()));
                    continue;
                }
                isFound = false;
                for (Group g : pUser.getGroups()) {
                    if ((g.getManagedSysId() == null || g.getManagedSysId().equals(group.getManagedSystemId())) && g.getName().equals(group.getName())) {
                        if (AttributeOperationEnum.DELETE.equals(group.getOperation())) {
                            g.setOperation(AttributeOperationEnum.DELETE);
                        }
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    GroupSearchBean gsb = new GroupSearchBean();
                    gsb.setName(group.getName());
                    gsb.setManagedSysId(group.getManagedSystemId());
                    List<Group> dbGroups = groupDataWebService.findBeans(gsb, requestorId, -1, -1);
                    if (CollectionUtils.isNotEmpty(dbGroups)) {
                        if (dbGroups.size() > 1) {
                            warnings.append(this.getWarning("Not unique name. Skip it. Group Name=" + group.getName()));
                            continue;
                        } else {
                            dbGroups.get(0).setOperation(group.getOperation());
                            pUser.addGroup(dbGroups.get(0));
                        }
                    } else {
                        warnings.append(this.getWarning("No such group with Name=" + group.getName() + ". Skip it."));
                        continue;
                    }
                }
            }
        }

    }

    private void fillOrganizations(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder
            warnings, String requestorId) throws Exception {
        if (CollectionUtils.isNotEmpty(request.getOrganizations())) {
            boolean isFound = false;
            if (pUser.getOrganizationUserDTOs() == null) {
                pUser.setOrganizationUserDTOs(new HashSet<OrganizationUserDTO>());
            }
            List<OrganizationUserDTO> result = new ArrayList<OrganizationUserDTO>();
            for (SourceAdapterOrganizationRequest org : request.getOrganizations()) {
                Organization organizationDB = null;
                isFound = false;
                if (org.getOperation() == null) {
                    warnings.append(getWarning("Incorrect operation for organization=" + org.getName() + " Skip this!"));
                    continue;
                }

                for (OrganizationUserDTO organizationUserDTO : pUser.getOrganizationUserDTOs()) {
                    if (organizationUserDTO.getOrganization().getName().equals(org.getName()) && organizationUserDTO.getOrganization().getOrganizationTypeId().equals(org.getOrganizationTypeId())) {
                        isFound = true;
                        switch (org.getOperation()) {
                            case ADD: {
                                warnings.append(getWarning("Incorrect operation ADD for organization=" + org.getName() + " Such organization has been already entitled with user. Skip this!"));
                                break;
                            }
                            case REPLACE: {
                                organizationUserDTO.setMdTypeId(org.getMetadataTypeId());
                                organizationDB = this.getOrganizationFromDataBase(org, warnings, requestorId);
                                this.convertToOrganization(organizationDB, org, warnings);
                                Response response = organizationDataService.saveOrganization(organizationDB, requestorId);
                                if (response.isFailure()) {
                                    warnings.append(getWarning("Organization doesn't added/updated to DataBase. " + response.getErrorCode() + ":" + response.getErrorText()));
                                }
                            }
                            case DELETE: {
                                organizationUserDTO.setOperation(org.getOperation());
                                break;
                            }
                            default:
                                break;
                        }
                    }
                    if (isFound) {
                        break;
                    }
                }
                if (!isFound) {
                    if (!AttributeOperationEnum.ADD.equals(org.getOperation())) {
                        warnings.append(getWarning("Incorrect operation for organization=" + org.getName() + " this organization is not entitled with user. Use ADD operation. Skip this!"));
                    } else {
                        organizationDB = this.getOrganizationFromDataBase(org, warnings, requestorId);
                        if (organizationDB == null && !org.isAddIfNotExistsInOpenIAM()) {
                            break;
                        } else {
                            if (organizationDB == null) {
                                organizationDB = new Organization();
                            }
                            this.convertToOrganization(organizationDB, org, warnings);
                            if (StringUtils.isBlank(organizationDB.getId()) && CollectionUtils.isNotEmpty(organizationDB.getAttributes())) {
                                Set<OrganizationAttribute> attributes = organizationDB.getAttributes();
                                organizationDB.setAttributes(null);
                                Response response = organizationDataService.saveOrganization(organizationDB, requestorId);
                                if (response.isSuccess()) {
                                    organizationDB.setId((String) response.getResponseValue());
                                    organizationDB.setAttributes(attributes);
                                }
                            }
                            Response response = organizationDataService.saveOrganization(organizationDB, requestorId);
                            if (response.isSuccess()) {
                                organizationDB.setId((String) response.getResponseValue());
                                result.add(new OrganizationUserDTO(pUser.getId(), organizationDB.getId(), org.getMetadataTypeId(), AttributeOperationEnum.ADD));
                            } else {
                                warnings.append(getWarning("Organization doesn't added/updated to DataBase. " + response.getErrorCode() + ":" + response.getErrorText()));
                                break;
                            }
                        }

                    }
                }
            }
            pUser.getOrganizationUserDTOs().addAll(result);
        }
    }

    private void convertToOrganization(Organization organizationDB, SourceAdapterOrganizationRequest org, StringBuilder warnings) {
        if (organizationDB == null || org == null) {
            return;
        }
        if (StringUtils.isNotBlank(org.getAbbreviation())) {
            organizationDB.setAbbreviation(org.getAbbreviation());
        }
        if (StringUtils.isNotBlank(org.getAlias())) {
            organizationDB.setAlias(org.getAlias());
        }
        if (StringUtils.isNotBlank(org.getClassification())) {
            organizationDB.setClassification(org.getClassification());
        }
        if (StringUtils.isNotBlank(org.getDescription())) {
            organizationDB.setDescription(org.getDescription());
        }
        organizationDB.setName(StringUtils.isBlank(org.getNewName()) ? org.getName() : org.getNewName());
        if (StringUtils.isNotBlank(org.getDomainName())) {
            organizationDB.setDomainName(org.getDomainName());
        }
        if (StringUtils.isNotBlank(org.getInternalOrgId())) {
            organizationDB.setInternalOrgId(org.getInternalOrgId());
        }
        if (StringUtils.isNotBlank(org.getLdapString())) {
            organizationDB.setLdapStr(org.getLdapString());
        }
        if (StringUtils.isNotBlank(org.getOrganizationTypeId())) {
            organizationDB.setOrganizationTypeId(org.getOrganizationTypeId());
        }
        if (StringUtils.isNotBlank(org.getStatus())) {
            organizationDB.setStatus(org.getStatus());
        }
        if (StringUtils.isNotBlank(org.getSymbol())) {
            organizationDB.setSymbol(org.getSymbol());
        }


        if (CollectionUtils.isEmpty(org.getEntityAttributes())) {
            organizationDB.setAttributes(null);
        } else {
            for (SourceAdapterAttributeRequest attributeRequest : org.getEntityAttributes()) {
                this.processAttribute(organizationDB.getAttributes(), attributeRequest, organizationDB.getId(), warnings);
            }
        }
    }

    private void processAttribute(Set<OrganizationAttribute> attributes, SourceAdapterAttributeRequest attributeRequest, String orgId, StringBuilder warnings) {
        if (attributeRequest == null || StringUtils.isBlank(attributeRequest.getName())) {
            return;
        }
        if (attributes == null) {
            return;
        }
        boolean isFound = false;
        Iterator<OrganizationAttribute> organizationAttributeIterator = attributes.iterator();
        while (organizationAttributeIterator.hasNext()) {
            OrganizationAttribute attribute = organizationAttributeIterator.next();
            if (attribute.getName().equals(attributeRequest.getName())) {
                isFound = true;
                switch (attributeRequest.getOperation()) {
                    case ADD:
                    case REPLACE: {
                        attribute.setName(StringUtils.isNotBlank(attributeRequest.getNewName()) ? attributeRequest.getNewName() : attributeRequest.getName());
                        if (CollectionUtils.isEmpty(attributeRequest.getValues())) {
                            attribute.setValues(null);
                            attribute.setIsMultivalued(false);
                            attribute.setValue(attributeRequest.getValue());
                        } else {
                            attribute.setValues(attributeRequest.getValues());
                            attribute.setIsMultivalued(true);
                            attribute.setValue(null);
                        }
                        break;
                    }
                    case DELETE:
                        organizationAttributeIterator.remove();
                        break;
                    default:
                        break;
                }
            }
        }
        if (!isFound) {
            OrganizationAttribute organizationAttribute = new OrganizationAttribute();
            organizationAttribute.setName(StringUtils.isNotBlank(attributeRequest.getNewName()) ? attributeRequest.getNewName() : attributeRequest.getName());
            if (CollectionUtils.isEmpty(attributeRequest.getValues())) {
                organizationAttribute.setValues(null);
                organizationAttribute.setIsMultivalued(false);
                organizationAttribute.setValue(attributeRequest.getValue());
            } else {
                organizationAttribute.setValues(attributeRequest.getValues());
                organizationAttribute.setIsMultivalued(true);
                organizationAttribute.setValue(null);
            }
            attributes.add(organizationAttribute);
        }
    }

    private Organization getOrganizationFromDataBase(SourceAdapterOrganizationRequest org, StringBuilder
            warnings, String requestorId) {
        OrganizationSearchBean organizationSearchBean = new OrganizationSearchBean();
        organizationSearchBean.setName(org.getName());
        organizationSearchBean.setOrganizationTypeId(org.getOrganizationTypeId());
        organizationSearchBean.setDeepCopy(false);
        List<Organization> organization = organizationDataService.findBeans(organizationSearchBean, requestorId, 0, 2);
        if (CollectionUtils.isEmpty(organization)) {
            warnings.append(getWarning("No such organization name=" + org.getName() + ". Organization Type=" + org.getOrganizationTypeId() + (org.isAddIfNotExistsInOpenIAM() ? " Will be added to database" : " Skip this")));
            return null;
        }
        if (organization.size() > 1) {
            warnings.append(getWarning("Multiple associations with organization! name=" + org.getName() + ". Organization Type=" + org.getOrganizationTypeId() + " Skip this!"));
            return null;
        }
        return organization.get(0);
    }

    private void fillSuperVisors(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings) throws
            Exception {
        if (CollectionUtils.isNotEmpty(request.getSupervisors())) {
            boolean isFound = false;
            List<User> superiorsFromDB = null;
            if (pUser.getId() != null) {
                superiorsFromDB = userDataService.getSuperiors(pUser.getId(), 0, Integer.MAX_VALUE);
            }
            if (CollectionUtils.isNotEmpty(superiorsFromDB)) {
                pUser.setSuperiors(new HashSet<User>(superiorsFromDB));
            }
            List<User> result = new ArrayList<User>();
            for (SourceAdapterMemberhipKey superUser : request.getSupervisors()) {
                isFound = false;
                if (superUser.getOperation() == null) {
                    warnings.append(getWarning("Incorrect operation for organization=" + superUser.getValue() + " Skip this!"));
                    continue;
                }
                User user = this.getUser(superUser, request);
                if (user == null || user.getId() == null) {
                    break;
                }
                for (User supervisor : pUser.getSuperiors()) {
                    if (supervisor.getId().equals(user.getId())) {
                        isFound = true;
                        switch (superUser.getOperation()) {
                            case ADD: {
                                warnings.append(getWarning("Incorrect operation ADD for Supervisor=" + user.getName() + " Such Supervisor has been already entitled with user. Skip this!"));
                                break;
                            }
                            case REPLACE: {
                                warnings.append(getWarning("Operation replace not supported"));
                                break;
                            }
                            case DELETE: {
                                supervisor.setOperation(superUser.getOperation());
                                break;
                            }
                            default:
                                break;
                        }
                    }
                    if (isFound) {
                        break;
                    }
                }
                if (!isFound) {
                    if (!AttributeOperationEnum.ADD.equals(superUser.getOperation())) {
                        warnings.append(getWarning("Incorrect operation for User=[" + user.getDisplayName() + "] this supervisor is not entitled with user. Use ADD operation. Skip this!"));
                    } else {
                        user.setOperation(AttributeOperationEnum.ADD);
                        result.add(user);
                    }
                }
            }
            pUser.getSuperiors().addAll(result);
        }
    }


    private void fillRoles(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings, String
            requestorId) throws Exception {
        if (CollectionUtils.isNotEmpty(request.getRoles())) {
            boolean isFound;
            if (pUser.getRoles() == null) {
                pUser.setRoles(new HashSet<Role>());
            }

            for (SourceAdapterEntityManagedSystemRequest role : request.getRoles()) {
                if (role.getOperation() == null) {
                    warnings.append(getWarning("Incorrect operation for role=" + role.getName() + " Skip this!"));
                    continue;
                }

                if (AttributeOperationEnum.REPLACE.equals(role.getOperation())) {
                    warnings.append(this.getWarning("'replace' operation not supported for Roles entitlement. Skip it. Role Name=" + role.getName()));
                    continue;
                }
                isFound = false;
                for (Role r : pUser.getRoles()) {
                    if ((r.getManagedSysId() == null || r.getManagedSysId().equals(role.getManagedSystemId())) && r.getName().equals(role.getName())) {
                        if (AttributeOperationEnum.DELETE.equals(role.getOperation())) {
                            r.setOperation(AttributeOperationEnum.DELETE);
                        }
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    RoleSearchBean rsb = new RoleSearchBean();
                    rsb.setName(role.getName());
                    rsb.setManagedSysId(role.getManagedSystemId());
                    List<Role> dbRoles = roleDataWebService.findBeans(rsb, requestorId, -1, -1);
                    if (CollectionUtils.isNotEmpty(dbRoles)) {
                        if (dbRoles.size() > 1) {
                            warnings.append(this.getWarning("Not unique name. Skip it. Role Name=" + role.getName()));
                            continue;
                        } else {
                            dbRoles.get(0).setOperation(role.getOperation());
                            pUser.addRole(dbRoles.get(0));
                        }
                    } else {
                        warnings.append(this.getWarning("No such Role with Name=" + role.getName() + ". Skip it."));
                        continue;
                    }
                }
            }
        }
    }

    private void fillResources(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings) throws
            Exception {
        if (CollectionUtils.isNotEmpty(request.getResources())) {
            boolean isFound;
            if (pUser.getResources() == null) {
                pUser.setResources(new HashSet<Resource>());
            }
            for (SourceAdapterEntityRequest resource : request.getResources()) {
                if (resource.getOperation() == null) {
                    warnings.append(getWarning("Incorrect operation for Resource=" + resource.getName() + " Skip this!"));
                    continue;
                }
                if (AttributeOperationEnum.REPLACE.equals(resource.getOperation())) {
                    warnings.append(this.getWarning("'replace' operation not supported for Resource entitlement. Skip it. Role Name=" + resource.getName()));
                    continue;
                }
                isFound = false;
                for (Resource r : pUser.getResources()) {
                    if (r.getName().equals(resource.getName())) {
                        if (AttributeOperationEnum.DELETE.equals(resource.getOperation())) {
                            r.setOperation(AttributeOperationEnum.DELETE);
                        }
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    ResourceSearchBean rsb = new ResourceSearchBean();
                    rsb.setName(resource.getName());
                    List<Resource> dbResource = resourceDataService.findBeans(rsb, -1, -1, null);
                    if (CollectionUtils.isNotEmpty(dbResource)) {
                        if (dbResource.size() > 1) {
                            warnings.append(this.getWarning("Not unique name. Skip it. Resource Name=" + resource.getName()));
                            continue;
                        } else {
                            dbResource.get(0).setOperation(resource.getOperation());
                            pUser.addResource(dbResource.get(0));
                        }
                    } else {
                        warnings.append(this.getWarning("No such resource with Name=" + resource.getName() + ". Skip it."));
                        continue;
                    }
                }
            }
        }
    }

    private static void fillAddresses(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder
            warnings) throws Exception {
        if (CollectionUtils.isNotEmpty(request.getAddresses())) {
            boolean isFound;
            if (pUser.getAddresses() == null) {
                pUser.setAddresses(new HashSet<Address>());
            }
            for (SourceAdapterAddressRequest fromWS : request.getAddresses()) {
                if (fromWS.getOperation() == null) {
                    warnings.append(getWarning("Incorrect operation for Address=" + fromWS.getTypeId() + " Skip this!"));
                    continue;
                }
                isFound = false;
                for (Address r : pUser.getAddresses()) {
                    if (r.getMetadataTypeId().equals(fromWS.getTypeId())) {
                        convertToAddress(r, fromWS, AttributeOperationEnum.ADD.equals(fromWS.getOperation()) ?
                                AttributeOperationEnum.REPLACE : fromWS.getOperation());
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    if (!AttributeOperationEnum.ADD.equals(fromWS.getOperation())) {
                        warnings.append(getWarning("Email not exists in OIAM and comes with not ADD operation. Address Type=" + fromWS.getTypeId()));
                    } else {
                        Address r = new Address();
                        convertToAddress(r, fromWS, AttributeOperationEnum.ADD);
                        pUser.getAddresses().add(r);
                    }
                }
            }
        }
    }

    private static void fillEmail(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings) throws
            Exception {
        if (CollectionUtils.isNotEmpty(request.getEmails())) {
            boolean isFound;
            if (pUser.getEmailAddresses() == null) {
                pUser.setEmailAddresses(new HashSet<EmailAddress>());
            }
            for (SourceAdapterEmailRequest fromWS : request.getEmails()) {
                if (fromWS.getOperation() == null) {
                    warnings.append(getWarning("Incorrect operation for Email=" + fromWS.getEmail() + " Skip this!"));
                    continue;
                }
                isFound = false;
                for (EmailAddress r : pUser.getEmailAddresses()) {
                    if (r.getMetadataTypeId().equals(fromWS.getTypeId())) {
                        convertToEmailAddress(r, fromWS, AttributeOperationEnum.ADD.equals(fromWS.getOperation()) ?
                                AttributeOperationEnum.REPLACE : fromWS.getOperation());
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    if (!AttributeOperationEnum.ADD.equals(fromWS.getOperation()))
                        warnings.append(getWarning("Email not exists in OIAM and comes with not ADD operation. Adding... Email=" + fromWS.getEmail()));

                    EmailAddress r = new EmailAddress();
                    convertToEmailAddress(r, fromWS, AttributeOperationEnum.ADD);
                    pUser.getEmailAddresses().add(r);

                }
            }
        }
    }

    private static void fillUserAttribute(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder
            warnings) throws Exception {
        if (CollectionUtils.isNotEmpty(request.getUserAttributes())) {
            for (SourceAdapterAttributeRequest fromWS : request.getUserAttributes()) {
                if (fromWS.getOperation() == null) {
                    warnings.append(getWarning("Incorrect operation for User Attribute=" + fromWS.getName() + " Skip this!"));
                    continue;
                }
                UserAttribute a = pUser.getAttribute(fromWS.getName());
                if (a != null) {
                    a.setOperation(AttributeOperationEnum.ADD.equals(fromWS.getOperation()) ? AttributeOperationEnum.REPLACE : fromWS.getOperation());
                    a.setName(StringUtils.isBlank(fromWS.getNewName()) ? fromWS.getName() : fromWS.getNewName());
                    if (CollectionUtils.isNotEmpty(fromWS.getValues())) {
                        a.setIsMultivalued(true);
                        a.setValues(fromWS.getValues());
                        a.setValue(null);
                    } else {
                        a.setValues(null);
                        a.setIsMultivalued(false);
                        a.setValue(getNULLValue(fromWS.getValue()));
                    }
                } else {
                    UserAttribute attr = new UserAttribute(fromWS.getName(), fromWS.getValue());
                    if (!AttributeOperationEnum.ADD.equals(fromWS.getOperation()))
                        warnings.append(getWarning("User Attribute not exists in OIAM, but comes with not ADD operation. Adding... " +
                                " User Attribute=" + fromWS.getName()));

                    attr.setOperation(AttributeOperationEnum.ADD);
                    pUser.saveAttribute(attr);
                }
            }
        }
    }


    private static void fillPhones(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings) throws
            Exception {
        if (CollectionUtils.isNotEmpty(request.getPhones())) {
            boolean isFound;
            if (pUser.getPhones() == null) {
                pUser.setPhones(new HashSet<Phone>());
            }
            for (SourceAdapterPhoneRequest fromWS : request.getPhones()) {
                if (fromWS.getOperation() == null) {
                    warnings.append(getWarning("Incorrect operation for Phone=" + fromWS.getTypeId() + " Skip this!"));
                    continue;
                }
                isFound = false;
                for (Phone r : pUser.getPhones()) {
                    if (r.getMetadataTypeId().equals(fromWS.getTypeId())) {
                        convertToPhone(r, fromWS, AttributeOperationEnum.ADD.equals(fromWS.getOperation()) ?
                                AttributeOperationEnum.REPLACE : fromWS.getOperation());
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    Phone r = new Phone();
                    if (!AttributeOperationEnum.ADD.equals(fromWS.getOperation())) {
                        warnings.append(getWarning("Phone not exists in OIAM and comes with not ADD operation." +
                                " Phone Type=" + fromWS.getTypeId()));
                    } else {
                        convertToPhone(r, fromWS, AttributeOperationEnum.ADD);
                        pUser.getPhones().add(r);
                    }
                }
            }
        }
    }

    private static void convertToEmailAddress(EmailAddress r, SourceAdapterEmailRequest
            address, AttributeOperationEnum operation) throws Exception {
        r.setOperation(operation);
        r.setMetadataTypeId(StringUtils.isBlank(address.getNewTypeId()) ? address.getTypeId() : address.getNewTypeId());
        r.setIsDefault(address.isPrimary());
        r.setIsActive(address.isActive());
        r.setEmailAddress(address.getEmail());
    }

    private static void convertToAddress(Address r, SourceAdapterAddressRequest address, AttributeOperationEnum
            operation) {
        r.setOperation(operation);
        r.setAddress1(getNULLValue(address.getAddress()));
        r.setBldgNumber(getNULLValue(address.getBldgNumber()));
        r.setSuite(getNULLValue(address.getSuite()));
        r.setStreetDirection(getNULLValue(address.getStreetDirection()));
        r.setState(getNULLValue(address.getState()));
        r.setPostalCd(getNULLValue(address.getPostalCode()));
        r.setMetadataTypeId(StringUtils.isBlank(address.getNewTypeId()) ? address.getTypeId() : address.getNewTypeId());
        r.setIsDefault(address.isPrimary());
        r.setIsActive(address.isActive());
        r.setCountry(getNULLValue(address.getCountry()));
        r.setCity(getNULLValue(address.getCity()));
    }

    private static void convertToPhone(Phone r, SourceAdapterPhoneRequest address, AttributeOperationEnum
            operation) {
        r.setOperation(operation);
        r.setMetadataTypeId(StringUtils.isBlank(address.getNewTypeId()) ? address.getTypeId() : address.getNewTypeId());
        r.setIsDefault(address.isPrimary());
        r.setIsActive(address.isActive());
        r.setAreaCd(getNULLValue(address.getAreaCode()));
        r.setCountryCd(getNULLValue(address.getCountryCode()));
        r.setName(getNULLValue(address.getName()));
        r.setPhoneNbr(getNULLValue(address.getPhoneNumber()));
    }


    private static String getWarning(String warn) {
        return String.format(WARNING, warn);
    }

    private static String getNULLValue(String source) {
        return (source == null || "NULL".equals(source)) ? null : source;
    }

    private User getUser(SourceAdapterKey keyPair, SourceAdapterRequest request) throws Exception {
        if (keyPair == null && SourceAdapterOperationEnum.ADD.equals(request.getAction())) {
            return new User();
        }
        UserSearchBean searchBean = new UserSearchBean();
        SourceAdapterKeyEnum matchAttrName = keyPair.getName();
        String matchAttrValue = keyPair.getValue();
        if ((matchAttrName == null || StringUtils.isBlank(matchAttrValue)) &&
                !SourceAdapterOperationEnum.ADD.equals(request.getAction())) {
            throw new Exception("Match Key is empty");
        }
        if (SourceAdapterKeyEnum.USERID.equals(matchAttrName)) {
            searchBean.setKey(matchAttrValue);
            searchBean.setUserId(matchAttrValue);
        } else if (SourceAdapterKeyEnum.PRINCIPAL.equals(matchAttrName)) {
            LoginSearchBean lsb = new LoginSearchBean();
            lsb.setLoginMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
            lsb.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
            searchBean.setPrincipal(lsb);
        } else if (SourceAdapterKeyEnum.EMAIL.equals(matchAttrName)) {
            searchBean.setEmailAddressMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
        } else if (SourceAdapterKeyEnum.EMPLOYEE_ID.equals(matchAttrName)) {
            searchBean.setEmployeeIdMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
        }
        searchBean.setDeepCopy(true);
        List<User> userList = userDataService.findBeans(searchBean, 0, Integer.MAX_VALUE);
        if (CollectionUtils.isNotEmpty(userList)) {
            if (userList.size() > 1) {
                throw new Exception("Identifier not unique=" + matchAttrName + ":" + matchAttrValue);
            }
            return userList.get(0);
        } else if (SourceAdapterOperationEnum.ADD.equals(request.getAction())) {
            return new User();
        } else {
            throw new Exception("No user with such Identifier=" + matchAttrName + ":" + matchAttrValue);
        }
    }

}
