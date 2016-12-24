package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.SysConfiguration;
import org.openiam.base.response.PasswordResponse;
import org.openiam.base.response.PasswordValidationResponse;
import org.openiam.base.response.ProvisionUserResponse;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.SearchParam;
import org.openiam.concurrent.OpenIAMRunnable;
import org.openiam.idm.searchbeans.*;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.dto.*;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.dto.common.UserSearchKey;
import org.openiam.provision.dto.common.UserSearchKeyEnum;
import org.openiam.provision.dto.common.UserSearchMemberhipKey;
import org.openiam.provision.dto.srcadapter.*;
import org.openiam.util.AuditLogHelper;
import org.openiam.util.SpringSecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by: Alexander Duckardt
 * Date: 7/22/14.
 */
@Component("sourceAdapterDispatcher")
public class SourceAdapterDispatcher implements Runnable {
    protected final long SHUTDOWN_TIME = 5000;
    @Autowired
    private ProvisioningDataService provisioningDataService;
    @Autowired
    @Qualifier("userManager")
    protected UserDataService userManager;
    @Autowired
    private GroupDataService groupDataService;
    @Autowired
    private RoleDataService roleDataService;
    @Autowired
    private ResourceService resourceDataService;
    @Autowired
    private OrganizationService organizationDataService;
    @Autowired
    protected SysConfiguration sysConfiguration;
    
    @Autowired
    private AuditLogHelper auditLogHelper;

    @Autowired
    protected AuditLogService auditLogService;
    
    @Value("${org.openiam.idm.system.user.id}")
    private String sysUserId;

    final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    final static String WARNING = "Warning! %s.\n";

    private static Logger log = Logger.getLogger(SourceAdapterDispatcher.class);

    private BlockingQueue<SourceAdapterRequest> requestQueue = new LinkedBlockingQueue<SourceAdapterRequest>();

    private volatile boolean terminate = false;

    private synchronized void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    private synchronized boolean getTerminate() {
        return this.terminate;
    }

    @PostConstruct
    public void init() {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new OpenIAMRunnable(this, sysUserId, null));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                setTerminate(true);
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(SHUTDOWN_TIME, TimeUnit.MILLISECONDS)) { //optional *
                        log.warn("Executor did not terminate in the specified time. Killing...."); //optional *
                        executorService.shutdownNow(); //optional **
                    }
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
        });
    }

    public void pushToQueue(SourceAdapterRequest request) {
        requestQueue.add(request);
    }

    public SourceAdapterRequest pullFromQueue() throws InterruptedException {
        return requestQueue.take();
    }

    @Override
    public void run() {
        try {
            SourceAdapterRequest request = null;
            while (!getTerminate() && (request = pullFromQueue()) != null) {
            	try {
            		if(request.getRequestor() != null) {
            			SpringSecurityHelper.setAuthenticationInformation(request.getRequestor().getValue(), null);
            		}
            		process(request);
            	} finally {
            		SpringSecurityHelper.clearContext();
            	}
                // keep this due to existed in old code
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

    }
//    @Autowired
//    private JmsTemplate jmsTemplate;
//
//    @Autowired
//    @Qualifier(value = "sourceAdapterQueue")
//    private Queue queue;
//
//    @Autowired
//    @Qualifier("transactionManager")
//    private PlatformTransactionManager platformTransactionManager;
//    private final Object mutex = new Object();

//    @Override
//    //TODO change when Spring 3.2.2 @Scheduled(fixedDelayString = "${org.openiam.metadata.threadsweep}")
//    @Scheduled(fixedDelay = 10000)
//    public void sweep() {
//        jmsTemplate.browse(queue, new BrowserCallback<Object>() {
//            @Override
//            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
//                synchronized (mutex) {
//
//                    final StopWatch sw = new StopWatch();
//                    sw.start();
//                    try {
//                        log.info("Starting SourceAdapterRequest sweeper thread");
//
//                        Enumeration e = browser.getEnumeration();
//
//                        while (e.hasMoreElements()) {
//                            final SourceAdapterRequest request = (SourceAdapterRequest) ((ObjectMessage) jmsTemplate.receive(queue)).getObject();
//
//                            TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
//                            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
//                            Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
//                                @Override
//                                public Boolean doInTransaction(TransactionStatus status) {
//                                    process(request);
//
//                                    //stupid
//                                    try {
//                                        // to give other threads chance to be executed
//                                        Thread.sleep(100);
//                                    } catch (InterruptedException e1) {
//                                        log.warn(e1.getMessage());
//                                    }
//                                    return true;
//                                }
//                            });
//
//                            e.nextElement();
//                        }
//
//                    } finally {
//                        log.info(String.format("Done with SourceAdapterRequest sweeper thread.  Took %s ms", sw.getTime()));
//                    }
//                    return null;
//                }
//            }
//        });
//    }

    private void process(SourceAdapterRequest request) {
//        MuleContextProvider.getCtx().getDefaultMessageReceiverThreadingProfile().get
        StringBuilder warnings = new StringBuilder();
        IdmAuditLogEntity idmAuditLog = auditLogHelper.newInstance();
        try {
            idmAuditLog.addCustomRecord("Request XML", this.write(request));
        } catch (Exception e) {
            log.error("Can't serialize request to XML");
        }
        long time = System.currentTimeMillis();
        if (request.isForceMode()) {
            idmAuditLog.addCustomRecord("Skip Warnings", "true");
            warnings.append(getWarning("Warnings will be skipped!"));
        }
        String requestorId = null;
        try {
            if (request.getAction() == null) {
                if (request.getKey() == null) {
                    request.setAction(SourceAdapterOperationEnum.ADD);
                } else {
                    request.setAction(SourceAdapterOperationEnum.MODIFY);
                }
            } else if (request.getKey() == null) {
                request.setAction(SourceAdapterOperationEnum.ADD);
            } else {
                request.setAction(SourceAdapterOperationEnum.MODIFY);
            }

            idmAuditLog.setRequestorUserId(requestorId);
        } catch (Exception e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setError(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            idmAuditLog.addCustomRecord(WARNING, warnings.toString());
            auditLogService.save(idmAuditLog);
            return;
//            return response;
        }
        ProvisionUser pUser = null;
        try {
            pUser = this.convertToProvisionUser(request, warnings);
            if (SourceAdapterOperationEnum.ADD.equals(request.getAction()) && StringUtils.isNotBlank(pUser.getId())) {
                throw new Exception("Such user exists. Can't add! User=" + pUser.getDisplayName());
            }
        } catch (Exception e) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setError(e.toString());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            idmAuditLog.addCustomRecord(WARNING, warnings.toString());
            auditLogService.save(idmAuditLog);
            return;
//            return response;
        }
        idmAuditLog.setUserId(pUser.getId());
        idmAuditLog.setAction(AuditAction.SOURCE_ADAPTER_CALL.value());
        idmAuditLog.setSource("Source Adapter");
        idmAuditLog.setAuditDescription("Operation:" + request.getAction().name());
        if (warnings.length() > 0 && !request.isForceMode()) {
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setError(warnings.toString());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(warnings.toString());
            auditLogService.save(idmAuditLog);
            idmAuditLog.addCustomRecord(WARNING, warnings.toString());
            return;
//            return response;
        }
        switch (request.getAction()) {
            case ADD: {
                pUser.setOperation(AttributeOperationEnum.ADD);
                ProvisionUserResponse provisionUserResponse = provisioningDataService.addUser(pUser);
//                response.setStatus(provisionUserResponse.getStatus());
//                response.setError(provisionUserResponse.getErrorText());
                break;
            }
            case MODIFY: {
                pUser.setOperation(AttributeOperationEnum.REPLACE);
                ProvisionUserResponse provisionUserResponse = provisioningDataService.modifyUser(pUser);
//                response.setStatus(provisionUserResponse.getStatus());
//                response.setError(provisionUserResponse.getErrorText());
                break;
            }
            case DELETE: {
                ProvisionUserResponse provisionUserResponse = provisioningDataService.deleteByUserWithSkipManagedSysList(pUser.getId(), UserStatusEnum.REMOVE, null);
//                response.setStatus(provisionUserResponse.getStatus());
//                response.setError(provisionUserResponse.getErrorText());
                break;
            }
            case ENABLE: {
                Response resp = provisioningDataService.disableUser(pUser.getId(), false);
//                response.setStatus(resp.getStatus());
//                response.setError(resp.getErrorText());
                break;
            }
            case DISABLE: {
                Response resp = provisioningDataService.disableUser(pUser.getId(), true);
//                response.setStatus(resp.getStatus());
//                response.setError(resp.getErrorText());
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
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(warnings.toString());
//                    response.setStatus(resetPasswordResponse.getStatus());
//                    response.setError(resetPasswordResponse.getErrorText());
                } else {
//                    response.setStatus(ResponseStatus.FAILURE);
                    warnings.append("Change password request is empty");
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(warnings.toString());
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
//                    response.setStatus(resetPasswordResponse.getStatus());
//                    response.setError(resetPasswordResponse.getErrorText());
                } else {
//                    response.setStatus(ResponseStatus.FAILURE);
                    warnings.append("Reset password request is empty");
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(warnings.toString());
                }
                break;
            }
            case NO_CHANGE: {
                break;
            }
            default:
//                response.setStatus(ResponseStatus.FAILURE);
//                response.setError("Operation not supported");
                idmAuditLog.fail();
                idmAuditLog.setFailureReason("Operation not supported");
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(warnings.toString());
        }
//        response.setError(warnings.toString());
        idmAuditLog.addCustomRecord(WARNING, warnings.toString());
        idmAuditLog.setAuditDescription("Processing time=" + ((System.currentTimeMillis() - time) / 1000) + "s");
        auditLogService.save(idmAuditLog);
    }

    private ProvisionUser convertToProvisionUser(SourceAdapterRequest request, StringBuilder warnings) throws Exception {
        //shit for AKZO
        User u = null;
        if (StringUtils.isNotBlank(request.getEmployeeId())) {
            UserSearchBean usb = new UserSearchBean();
            SearchParam sp = new SearchParam();
            sp.setMatchType(MatchType.EXACT);
            sp.setValue(request.getEmployeeId());
            usb.setEmployeeIdMatchToken(sp);
            usb.setDeepCopy(true);
            List<User> users = userManager.findBeansDto(usb, 0, 2);
            if (CollectionUtils.isNotEmpty(users)) {
                if (users.size() == 1) {
                    u = users.get(0);
                    request.setAction(SourceAdapterOperationEnum.MODIFY);
                } else {
                    warnings.append(getWarning("More than 1 user with such employeeID"));
                    throw new Exception("Ununique employeeID");
                }
            }
        }
        if (u == null)
            u = this.getUser(request.getKey(), request);
        if (u == null)
            throw new Exception("Can't find user!");
        ProvisionUser pUser = new ProvisionUser(u);
        this.fillProperties(request, pUser);
        this.fillGroups(pUser, request, warnings);
        this.fillRoles(pUser, request, warnings);
        this.fillResources(pUser, request, warnings);
        fillAddresses(pUser, request, warnings);
        fillPhones(pUser, request, warnings);
        fillEmail(pUser, request, warnings);
        fillUserAttribute(pUser, request, warnings);
        fillOrganizations(pUser, request, warnings);
        fillSuperVisors(pUser, request, warnings);
        fillPrincipals(pUser, request, warnings);
        fillAlternativeContact(pUser, request, warnings);
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

    private void fillPrincipals(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings) throws Exception {
        if (CollectionUtils.isNotEmpty(request.getLogins())) {
            boolean isFound;
            if (pUser.getPrincipalList() == null) {
                pUser.setPrincipalList(new ArrayList<Login>());
            }
            for (SourceAdapterLoginRequest loginRequest : request.getLogins()) {
                isFound = false;
                for (Login l : pUser.getPrincipalList()) {
                    if (l.getManagedSysId().equals(loginRequest.getManagedSystemId())) {
                        if (AttributeOperationEnum.DELETE.equals(loginRequest.getOperation())) {
                            //delete
                            l.setOperation(AttributeOperationEnum.DELETE);
                        } else {
                            populateLogin(loginRequest, l, false);
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
                        populateLogin(loginRequest, l, true);
                        pUser.addPrincipal(l);
                    }
                }
            }
        }
    }

    private static void populateLogin(SourceAdapterLoginRequest loginRequest, Login login, boolean isADD) {
        login.setOperation(isADD ? AttributeOperationEnum.ADD : AttributeOperationEnum.REPLACE);
        login.setLogin(StringUtils.isBlank(loginRequest.getNewLogin()) ? loginRequest.getLogin() : loginRequest.getNewLogin());
        login.setManagedSysId(loginRequest.getManagedSystemId());
    }

    private void fillGroups(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings) throws Exception {
        if (CollectionUtils.isNotEmpty(request.getGroups())) {
            boolean isFound;
            if (pUser.getGroups() == null) {
                pUser.setGroups(new HashSet<UserToGroupMembershipXref>());
            }
            for (SourceAdapterEntityManagedSystemRequest group : request.getGroups()) {
                if (AttributeOperationEnum.REPLACE.equals(group.getOperation())) {
                    continue;
                }
                isFound = false;
/*
                FUCK!!!!
                for (Group g : pUser.getGroups()) {
                    if ((g.getManagedSysId() == null || g.getManagedSysId().equals(group.getManagedSystemId())) && g.getName().equals(group.getName())) {
                        if (AttributeOperationEnum.DELETE.equals(group.getOperation())) {
                            g.setOperation(AttributeOperationEnum.DELETE);
                        }
                        isFound = true;
                        break;
                    }
                }*/
                if (!isFound) {
                    GroupSearchBean gsb = new GroupSearchBean();
                    gsb.setNameToken(new SearchParam(group.getName(), MatchType.EXACT));
                    gsb.setManagedSysId(group.getManagedSystemId());
                    List<Group> dbGroups = groupDataService.findDtoBeans(gsb, -1, -1);
                    if (CollectionUtils.isNotEmpty(dbGroups)) {
                        if (dbGroups.size() > 1) {
                            warnings.append(this.getWarning("Not unique name. Skip it. Group Name=" + group.getName()));
                            continue;
                        } else {
                            dbGroups.get(0).setOperation(AttributeOperationEnum.ADD);
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
            warnings) throws Exception {
        if (CollectionUtils.isNotEmpty(request.getOrganizations())) {
            boolean isFound = false;
            if (pUser.getAffiliations() == null) {
                pUser.setAffiliations(new HashSet<UserToOrganizationMembershipXref>());
            }
            List<UserToOrganizationMembershipXref> result = new ArrayList<UserToOrganizationMembershipXref>();
            for (SourceAdapterOrganizationRequest org : request.getOrganizations()) {
                isFound = false;
                Organization organizationDB = this.getOrganizationFromDataBase(org, warnings);
                if (organizationDB == null) {
                    break;
                }
                for (UserToOrganizationMembershipXref organizationUserDTO : pUser.getAffiliations()) {
                    if (organizationUserDTO.getMemberEntityId().equals(organizationDB.getId())) {
                        isFound = true;
                        if (org.getOperation() == null) {
                            org.setOperation(AttributeOperationEnum.REPLACE);
                        }
                        switch (org.getOperation()) {
                            case ADD:
                            case REPLACE: {
                                organizationUserDTO.setOrganizationTypeId(org.getMetadataTypeId());
                                this.convertToOrganization(organizationDB, org, warnings);
                                Response response = organizationDataService.saveOrganization(organizationDB);
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
                    org.setOperation(AttributeOperationEnum.ADD);
                    if (org.getOperation().equals(AttributeOperationEnum.DELETE)) {
                        break;
                    }
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
                            Response response = organizationDataService.saveOrganization(organizationDB);
                            if (response.isSuccess()) {
                                organizationDB.setId((String) response.getResponseValue());
                                organizationDB.setAttributes(attributes);
                            }
                        }
                        Response response = organizationDataService.saveOrganization(organizationDB);
                        if (response.isSuccess()) {
                            organizationDB.setId((String) response.getResponseValue());

                            // FUKING 2 DIFFERENT AFFILIATION
                            //result.add(new UserToOrganizationMembershipXref(pUser.getId(), organizationDB.getId(), org.getMetadataTypeId(), AttributeOperationEnum.ADD));
                            //result.add(new UserToOrganizationMembershipXref());
                        } else {
                            warnings.append(getWarning("Organization doesn't added/updated to DataBase. " + response.getErrorCode() + ":" + response.getErrorText()));
                            break;
                        }
                    }
                }
            }
            pUser.getAffiliations().addAll(result);
        }
    }

    private List<Organization> findOrganization(SourceAdapterOrganizationRequest org) {
        List<Organization> organization = null;
        OrganizationSearchBean osb = new OrganizationSearchBean();
        if (StringUtils.isNotBlank(org.getName()) && StringUtils.isNotBlank(org.getOrganizationTypeId())) {
            osb.setNameToken(new SearchParam(org.getName(), MatchType.EXACT));
            osb.setOrganizationTypeId(org.getOrganizationTypeId());
        } else if (org.getAttributeLookup() != null && StringUtils.isNotBlank(org.getAttributeLookup().getName()) && StringUtils.isNotBlank(org.getAttributeLookup().getValue())) {
            osb.addAttribute(org.getAttributeLookup().getName(), org.getAttributeLookup().getValue());
        }
        osb.setDeepCopy(false);
        organization = organizationDataService.findBeansDto(osb, 0, Integer.MAX_VALUE, null);
        return organization;
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

        if (StringUtils.isNotBlank(org.getName()) || StringUtils.isNotBlank(org.getNewName()))
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


        if (CollectionUtils.isNotEmpty(org.getEntityAttributes())) {
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
                if (attributeRequest.getOperation() == null) {
                    attributeRequest.setOperation(AttributeOperationEnum.REPLACE);
                }
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
            warnings) {
        List<Organization> organization = this.findOrganization(org);
        if (CollectionUtils.isEmpty(organization)) {
            warnings.append(getWarning("can't find org=" + org.toString()));
            return null;
        }
        Organization orgDB = null;
        if (organization.size() > 1) {
            orgDB = organization.get(0);
        }
        orgDB = organization.get(0);

        if (orgDB != null && orgDB.getId() != null) {
            List<OrganizationAttribute> organizationAttributes = organizationDataService.getOrgAttributesDtoList(orgDB.getId());
            if (CollectionUtils.isNotEmpty(organizationAttributes))
                orgDB.setAttributes(new HashSet<OrganizationAttribute>(organizationAttributes));
        }
        return orgDB;
    }

    private void fillAlternativeContact(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings) throws
            Exception {
        UserSearchKey alternativeContact = request.getAlternativeContact();
        if (alternativeContact == null || alternativeContact.getName() == null || StringUtils.isBlank(alternativeContact.getValue())) {
            return;
        }
        if ("null".equalsIgnoreCase(alternativeContact.getValue())) {
            pUser.setAlternateContactId(null);
            return;
        }

        User alternativeUser = this.getUser(alternativeContact, request);
        if (alternativeUser != null) {
            pUser.setAlternateContactId(alternativeUser.getId());
        }

    }

    private void fillSuperVisors(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings) throws
            Exception {
        if (CollectionUtils.isNotEmpty(request.getSupervisors())) {
            boolean isFound = false;
            List<User> superiorsFromDB = null;
            if (pUser.getId() != null) {
                superiorsFromDB = userManager.getSuperiorsDto(pUser.getId(), 0, Integer.MAX_VALUE);
            }
            if (CollectionUtils.isNotEmpty(superiorsFromDB)) {
                pUser.setSuperiors(new HashSet<User>(superiorsFromDB));
            }
            List<User> result = new ArrayList<User>();
            for (UserSearchMemberhipKey superUser : request.getSupervisors()) {
                if (superUser.getValue() == null || "NULL".equalsIgnoreCase(superUser.getValue())) {
                    warnings.append(this.getWarning("Supervisor has NULL identifier value."));
                    continue;
                }
                isFound = false;
                User user = this.getUser(superUser, request);
                if (superUser.getOperation() == null) {
                    superUser.setOperation(AttributeOperationEnum.ADD);
                }
                if (user == null || user.getId() == null) {
                    warnings.append(getWarning("No such manager in system=" + superUser.getValue() + " Skip this!"));
                    String value = superUser.getName() + "=" + superUser.getValue();
                    UserAttribute supervisorDetails = user.getAttribute("SUPERVISOR_DETAILS");
                    if (supervisorDetails == null) {
                        supervisorDetails = new UserAttribute();
                        supervisorDetails.setName("SUPERVISOR_DETAILS");
                        supervisorDetails.setOperation(AttributeOperationEnum.ADD);
                        pUser.saveAttribute(supervisorDetails);
                    } else {
                        supervisorDetails.setOperation(AttributeOperationEnum.REPLACE);
                    }
                    supervisorDetails.setValue(value);
                    break;
                }
                for (User supervisor : pUser.getSuperiors()) {
                    if (supervisor.getId().equals(user.getId())) {
                        isFound = true;
                        switch (superUser.getOperation()) {
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
                    user.setOperation(AttributeOperationEnum.ADD);
                    result.add(user);
                }
            }
            pUser.getSuperiors().addAll(result);
        }
    }


    private void fillRoles(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder warnings) throws Exception {
        if (CollectionUtils.isNotEmpty(request.getRoles())) {
            boolean isFound;
            if (pUser.getRoles() == null) {
                pUser.setRoles(new HashSet<UserToRoleMembershipXref>());
            }

            for (SourceAdapterEntityManagedSystemRequest role : request.getRoles()) {
                isFound = false;
                for (UserToRoleMembershipXref r : pUser.getRoles()) {
                    if ((r.getEntityId() == null || r.getEntityId().equals(role.getManagedSystemId())) && r.getName().equals(role.getName())) {
                        if (AttributeOperationEnum.DELETE.equals(role.getOperation())) {
                            r.setOperation(AttributeOperationEnum.DELETE);
                        }
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    RoleSearchBean rsb = new RoleSearchBean();
                    rsb.setNameToken(new SearchParam(role.getName(), MatchType.EXACT));
                    rsb.setManagedSysId(role.getManagedSystemId());
                    List<Role> dbRoles = roleDataService.findBeansDto(rsb, -1, -1);
                    if (CollectionUtils.isNotEmpty(dbRoles)) {
                        if (dbRoles.size() > 1) {
                            warnings.append(this.getWarning("Not unique name. Skip it. Role Name=" + role.getName()));
                            continue;
                        } else {
                            dbRoles.get(0).setOperation(AttributeOperationEnum.ADD);
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
                pUser.setResources(new HashSet<UserToResourceMembershipXref>());
            }
            for (SourceAdapterEntityRequest resource : request.getResources()) {
                if (resource.getOperation() == null) {
                    resource.setOperation(AttributeOperationEnum.ADD);
                }
                isFound = false;
                for (UserToResourceMembershipXref r : pUser.getResources()) {
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
                    rsb.setNameToken(new SearchParam(resource.getName(), MatchType.EXACT));
                    List<Resource> dbResource = resourceDataService.findBeansDTO(rsb, -1, -1);
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
                isFound = false;
                for (Address r : pUser.getAddresses()) {
                    if (r.getMdTypeId().equals(fromWS.getTypeId())) {
                        if (AttributeOperationEnum.DELETE.equals(fromWS.getOperation())) {
                            r.setOperation(AttributeOperationEnum.DELETE);
                        } else {
                            convertToAddress(r, fromWS);
                        }
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    Address r = new Address();
                    convertToAddress(r, fromWS);
                    pUser.getAddresses().add(r);
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
                isFound = false;
                for (EmailAddress r : pUser.getEmailAddresses()) {
                    if (r.getMdTypeId().equals(fromWS.getTypeId())) {
                        if (AttributeOperationEnum.DELETE.equals(fromWS.getOperation())) {
                            r.setOperation(AttributeOperationEnum.DELETE);
                        } else {
                            convertToEmailAddress(r, fromWS);
                        }
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    EmailAddress r = new EmailAddress();
                    convertToEmailAddress(r, fromWS);
                    pUser.getEmailAddresses().add(r);

                }
            }
        }
    }

    private static void fillUserAttribute(ProvisionUser pUser, SourceAdapterRequest request, StringBuilder
            warnings) throws Exception {
        if (CollectionUtils.isNotEmpty(request.getUserAttributes())) {
            for (SourceAdapterAttributeRequest fromWS : request.getUserAttributes()) {
                UserAttribute a = pUser.getAttribute(fromWS.getName());
                if (a != null) {
                    if (AttributeOperationEnum.DELETE.equals(fromWS.getOperation())) {
                        pUser.setOperation(AttributeOperationEnum.DELETE);
                        continue;
                    }

                    a.setOperation(AttributeOperationEnum.REPLACE);
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
                    attr.setOperation(AttributeOperationEnum.ADD);
                    pUser.saveAttribute(attr);
                }
            }
        }
        //Mark that user created from source adapter.
        if (StringUtils.isBlank(pUser.getId())) {
            UserAttribute attr = new UserAttribute("USER_CREATION_SOURCE", "SOURCE_ADAPTER");
            attr.setOperation(AttributeOperationEnum.ADD);
            pUser.saveAttribute(attr);
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
                isFound = false;
                for (Phone r : pUser.getPhones()) {
                    if (r.getMetadataTypeId().equals(fromWS.getTypeId())) {
                        if (AttributeOperationEnum.DELETE.equals(fromWS.getOperation())) {
                            r.setOperation(AttributeOperationEnum.DELETE);
                        } else {
                            convertToPhone(r, fromWS);
                        }
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    Phone r = new Phone();
                    convertToPhone(r, fromWS);
                    pUser.getPhones().add(r);
                }
            }
        }
    }

    private static void convertToEmailAddress(EmailAddress r, SourceAdapterEmailRequest
            address) throws Exception {
        if (StringUtils.isBlank(r.getId())) {
            r.setOperation(AttributeOperationEnum.ADD);
        } else {
            r.setOperation(AttributeOperationEnum.REPLACE);
        }
        r.setMdTypeId(StringUtils.isBlank(address.getNewTypeId()) ? address.getTypeId() : address.getNewTypeId());
        r.setIsDefault(address.isPrimary());
        r.setIsActive(address.isActive());
        r.setEmailAddress(address.getEmail());
    }

    private static void convertToAddress(Address r, SourceAdapterAddressRequest address) {
        if (StringUtils.isNotBlank(r.getId()))
            r.setOperation(AttributeOperationEnum.REPLACE);
        else {
            r.setOperation(AttributeOperationEnum.ADD);
        }
        r.setAddress1(getNULLValue(address.getAddress()));
        r.setBldgNumber(getNULLValue(address.getBldgNumber()));
        r.setSuite(getNULLValue(address.getSuite()));
        r.setStreetDirection(getNULLValue(address.getStreetDirection()));
        r.setState(getNULLValue(address.getState()));
        r.setPostalCd(getNULLValue(address.getPostalCode()));
        r.setMdTypeId(StringUtils.isBlank(address.getNewTypeId()) ? address.getTypeId() : address.getNewTypeId());
        r.setIsDefault(address.isPrimary());
        r.setIsActive(address.isActive());
        r.setCountry(getNULLValue(address.getCountry()));
        r.setCity(getNULLValue(address.getCity()));
    }

    private static void convertToPhone(Phone r, SourceAdapterPhoneRequest address) {
        if (StringUtils.isBlank(r.getId())) {
            r.setOperation(AttributeOperationEnum.ADD);
        } else {
            r.setOperation(AttributeOperationEnum.REPLACE);
        }
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

    private User getUser(UserSearchKey keyPair, SourceAdapterRequest request) throws Exception {
        if (keyPair == null && SourceAdapterOperationEnum.ADD.equals(request.getAction())) {
            //create
            return new User();
        } else if (keyPair != null && keyPair.getName() == null && StringUtils.isBlank(keyPair.getValue())) {
            request.setAction(SourceAdapterOperationEnum.ADD);
            return new User();
        } else if (keyPair != null && keyPair.getName() == null && StringUtils.isNotBlank(keyPair.getValue())) {
            User u = null;
            for (UserSearchKeyEnum keyEnum : UserSearchKeyEnum.values()) {
                u = this.findByKey(keyEnum, keyPair.getValue(), request);
                if (u != null) {
                    break;
                }
            }
            //try to find by all keys
            return u;
        } else if (keyPair != null && keyPair.getName() != null && StringUtils.isNotBlank(keyPair.getValue())) {
            return this.findByKey(keyPair.getName(), keyPair.getValue(), request);
        } else {
            return null;
        }
    }


    private User findByKey(UserSearchKeyEnum matchAttrName, String matchAttrValue, SourceAdapterRequest request) throws Exception {
        UserSearchBean searchBean = new UserSearchBean();
        if (UserSearchKeyEnum.USERID.equals(matchAttrName)) {
            searchBean.addKey(matchAttrValue);
            searchBean.setUserId(matchAttrValue);
        } else if (UserSearchKeyEnum.PRINCIPAL.equals(matchAttrName)) {
            LoginSearchBean lsb = new LoginSearchBean();
            lsb.setLoginMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
            lsb.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
            searchBean.setPrincipal(lsb);
        } else if (UserSearchKeyEnum.EMAIL.equals(matchAttrName)) {
            searchBean.setEmailAddressMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
        } else if (UserSearchKeyEnum.EMPLOYEE_ID.equals(matchAttrName)) {
            searchBean.setEmployeeIdMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
        }
        searchBean.setDeepCopy(true);
        List<User> userList = userManager.findBeansDto(searchBean, 0, Integer.MAX_VALUE);
        if (CollectionUtils.isNotEmpty(userList)) {
            if (userList.size() > 1) {
                throw new Exception("Identifier not unique=" + matchAttrName + ":" + matchAttrValue);
            }
            return userList.get(0);
        } else if (SourceAdapterOperationEnum.ADD.equals(request.getAction())) {
            return new User();
        } else {
            return null;
        }
    }

    public static String write(SourceAdapterRequest request) throws Exception {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        XMLEncoder xmlEncoder = new XMLEncoder(baos);
//        xmlEncoder.writeObject(request);
//        xmlEncoder.close();


        //Write it
        JAXBContext ctx = JAXBContext.newInstance(SourceAdapterRequest.class);

        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        m.marshal(request, sw);
        sw.close();

        return prettyFormat(sw.toString(), 7);
    }

    public static String prettyFormat(String input, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            transformerFactory.setAttribute("indent-number", indent);

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }
}
