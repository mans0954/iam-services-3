package org.openiam.spml2.spi.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.parser.csv.ProvisionUserCSVParser;
import org.openiam.idm.parser.csv.UserCSVParser;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.recon.command.ReconciliationCommandFactory;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultBean;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultCase;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultRow;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultUtil;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.recon.util.Serializer;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.util.UserUtils;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

public class AbstractCSVCommand {
    protected static final Log log = LogFactory
            .getLog(AbstractCSVCommand.class);
    @Autowired
    protected ManagedSystemService managedSysService;
    @Autowired
    protected ResourceService resourceDataService;
    @Autowired
    protected UserCSVParser userCSVParser;
    @Autowired
    protected ProvisionUserCSVParser provisionUserCSVParser;
    @Autowired
    private MailService mailService;
    @Resource(name = "userServiceClient")
    protected UserDataWebService userDataWebService;
    @Autowired
    private UserDozerConverter userDozerConverter;
    @Value("${iam.files.location}")
    private String absolutePath;

    @Value("${openiam.default_managed_sys}")
    protected String defaultManagedSysId;

    // public static ApplicationContext ac;

    @Deprecated
    // TODO REMOVE THIS, WHEN UI will be implemented FULLY
    public void improveFile(String pathToFile) throws Exception {
        // ScriptIntegration se = ScriptFactory.createModule(this.scriptEngine);
        // CSVImproveScript script = (CSVImproveScript)
        // se.instantiateClass(null,
        // "/recon/ImproveScript.groovy");
        // script.execute(pathToFile, orgManager.getAllOrganizations());
    }

    protected char getSeparator(ReconciliationConfig config) {
        char separator;

        if (StringUtils.hasText(config.getSeparator()))
            separator = config.getSeparator().toCharArray()[0];
        else
            separator = ',';
        return separator;
    }

    protected char getEndOfLine(ReconciliationConfig config) {
        char EOL;
        if (StringUtils.hasText(config.getEndOfLine()))
            EOL = config.getEndOfLine().toCharArray()[0];
        else
            EOL = '\n';
        return EOL;
    }

    protected ResponseType reconcile(ReconciliationConfig config) {
        ResponseType response = new ResponseType();
        // init recocnilation result data
        ReconciliationResultBean resultBean = new ReconciliationResultBean();
        resultBean.setObjectType("USER");
        List<ReconciliationResultRow> rows = new ArrayList<ReconciliationResultRow>();
        resultBean.setRows(rows);
        // email mesage
        StringBuilder message = new StringBuilder();
        ResourceEntity res = resourceDataService.findResourceById(config
                .getResourceId());
        String managedSysId = res.getManagedSysId();
        ManagedSysEntity mSys = managedSysService
                .getManagedSysById(managedSysId);

        Map<String, ReconciliationCommand> situations = new HashMap<String, ReconciliationCommand>();
        for (ReconciliationSituation situation : config.getSituationSet()) {
            situations.put(situation.getSituation().trim(),
                    ReconciliationCommandFactory.createCommand(
                            situation.getSituationResp(), situation,
                            managedSysId));
            log.debug("Created Command for: " + situation.getSituation());
        }
        List<User> users = userDataWebService
                .getByManagedSystem(defaultManagedSysId);
        if (users == null) {
            log.error("user list from DB is empty");
            response.setStatus(StatusCodeType.FAILURE);
            response.setErrorMessage("user list from DB is empty");
            return response;
        }
        List<AttributeMapEntity> attrMapList = managedSysService
                .getResourceAttributeMaps(mSys.getResourceId());
        resultBean.setHeader(ReconciliationResultUtil
                .setHeaderInReconciliationResult(attrMapList));
        if (CollectionUtils.isEmpty(attrMapList)) {
            log.error("user list from DB is empty");
            response.setStatus(StatusCodeType.FAILURE);
            response.setErrorMessage("attrMapList is empty");
            return response;
        }
        List<ReconciliationObject<User>> idmUsers = null;
        List<ReconciliationObject<User>> sourceUsers = null;
        List<ReconciliationObject<User>> dbUsers = new ArrayList<ReconciliationObject<User>>();
        for (User u : users) {
            dbUsers.add(userCSVParser.toReconciliationObject(u, attrMapList));
        }

        try {
            idmUsers = userCSVParser.getObjects(mSys, attrMapList,
                    CSVSource.IDM);

            // Improve uploaded file
            improveFile(userCSVParser.getFileName(mSys, CSVSource.UPLOADED));
            sourceUsers = userCSVParser.getObjects(mSys, attrMapList,
                    CSVSource.UPLOADED);
            ReconciliationResultRow headerRow = ReconciliationResultUtil
                    .setHeaderInReconciliationResult(attrMapList);
            // First run from IDM search in Sourse
            try {
                log.debug("First cycle");
                dbUsers.removeAll(reconCicle(headerRow, rows, idmUsers,
                        dbUsers, attrMapList, mSys, situations,
                        config.getManualReconciliationFlag()));
            } catch (Exception e) {
                log.error(e.getMessage());
                response.setStatus(StatusCodeType.FAILURE);
                response.setErrorMessage(e.getMessage());
                message.append("ERROR:" + response.getErrorMessage());
            }
            try {
                log.debug("Second cycle");
                dbUsers.removeAll(reconCicle(headerRow, rows, sourceUsers,
                        dbUsers, attrMapList, mSys, situations,
                        config.getManualReconciliationFlag()));
            } catch (Exception e) {
                log.error(e.getMessage());
                response.setStatus(StatusCodeType.FAILURE);
                response.setErrorMessage(e.getMessage());
                message.append("ERROR:" + response.getErrorMessage());
            }
            for (ReconciliationObject<User> obj : dbUsers) {
                // String login = "";
                if (!CollectionUtils
                        .isEmpty(obj.getObject().getPrincipalList())) {
                    // login = obj.getObject().getPrincipalList().get(0)
                    // .getLogin();
                }
                rows.add(this.setRowInReconciliationResult(headerRow,
                        attrMapList, obj, null,
                        ReconciliationResultCase.NOT_EXIST_IN_RESOURCE));
                if (!config.getManualReconciliationFlag()) {
                    ReconciliationCommand command = situations
                            .get(ReconciliationCommand.IDM_EXISTS__SYS_NOT_EXISTS);
                    if (command != null) {
                        Login l = null;
                        if (CollectionUtils.isEmpty(obj.getObject()
                                .getPrincipalList())) {
                            l = obj.getObject().getPrincipalList().get(0);
                        } else {
                            l = new Login();
                            l.setLogin(obj.getPrincipal());
                            l.setDomainId(mSys.getDomainId());
                            l.setManagedSysId(managedSysId);
                        }
                        log.debug("Call command for: Record in resource and in IDM");
                        command.execute(l, obj.getObject(), this
                                .getExtensibleAttributesList(headerRow,
                                        attrMapList, obj));
                    }
                }
            }

            // -----------------------------------------------
        } catch (Exception e) {
            log.error(e);
            response.setStatus(StatusCodeType.FAILURE);
            response.setErrorMessage(e.getMessage() + e.getStackTrace());
            message.append(response.getErrorMessage());
        }
        this.saveReconciliationResults(mSys.getResourceId(), resultBean);
        this.sendMail(message, config, res);
        return response;
    }

    private void sendMail(StringBuilder message, ReconciliationConfig config,
            ResourceEntity res) {
        if (StringUtils.hasText(config.getNotificationEmailAddress())) {
            message.append("Resource: " + res.getName() + ".\n");
            message.append("Uploaded CSV file: " + res.getResourceId()
                    + ".csv was successfully reconciled.\n");
            mailService.sendEmails(null,
                    new String[] { config.getNotificationEmailAddress() },
                    null, null, "CSVConnector", message.toString(), false,
                    new String[] {});
        }
    }

    private void saveReconciliationResults(String fileName,
            ReconciliationResultBean resultBean) {
        Serializer.serialize(resultBean, absolutePath + fileName + ".rcndat");
    }

    private ReconciliationResultRow setRowInReconciliationResult(
            ReconciliationResultRow headerRow,
            List<AttributeMapEntity> attrMapList,
            ReconciliationObject<User> currentObject,
            ReconciliationObject<User> findedObject,
            ReconciliationResultCase caseReconciliation) {
        ReconciliationResultRow row = new ReconciliationResultRow();

        Map<String, ReconciliationResultField> resultMap = null;
        if (currentObject != null && findedObject != null) {
            resultMap = this.matchFields(attrMapList, currentObject,
                    findedObject);
            if (!MapUtils.isEmpty(resultMap)) {
                for (ReconciliationResultField field : resultMap.values())
                    if (field.getValues().size() > 1) {
                        caseReconciliation = ReconciliationResultCase.MATCH_FOUND_DIFFERENT;
                        break;
                    }
            }
        } else if (currentObject != null) {
            resultMap = userCSVParser.convertToMap(attrMapList, currentObject);
        }
        row.setCaseReconciliation(caseReconciliation);
        List<ReconciliationResultField> fieldList = new ArrayList<ReconciliationResultField>();
        if (!MapUtils.isEmpty(resultMap)) {
            for (ReconciliationResultField field : headerRow.getFields()) {
                ReconciliationResultField value = resultMap.get(field
                        .getValues().get(0));
                if (value == null)
                    continue;
                ReconciliationResultField newField = new ReconciliationResultField();
                newField.setValues(value.getValues());
                fieldList.add(newField);
            }
        }
        row.setFields(fieldList);
        return row;
    }

    private Map<String, ReconciliationResultField> matchFields(
            List<AttributeMapEntity> attrMap, ReconciliationObject<User> u,
            ReconciliationObject<User> o) {
        return userCSVParser.matchFields(attrMap, u, o);
    }

    /**
     * 
     * @param hList
     * @param report
     * @param preffix
     * @param reconUserList
     * @param dbUsers
     * @param attrMapList
     * @param mSys
     * @return
     * @throws Exception
     */
    private Set<ReconciliationObject<User>> reconCicle(
            ReconciliationResultRow headerRow,
            List<ReconciliationResultRow> rows,
            List<ReconciliationObject<User>> reconUserList,
            List<ReconciliationObject<User>> dbUsers,
            List<AttributeMapEntity> attrMapList, ManagedSysEntity mSys,
            Map<String, ReconciliationCommand> situations, boolean isManualRecon)
            throws Exception {
        Set<ReconciliationObject<User>> used = new HashSet<ReconciliationObject<User>>(
                0);
        for (ReconciliationObject<User> u : reconUserList) {
            log.info("User " + u.toString());
            if (u.getObject() == null || u.getPrincipal() == null) {
                log.warn("Skip USER" + u.toString() + " key or objecy is NULL");
                if (u.getObject() != null) {
                    rows.add(this.setRowInReconciliationResult(headerRow,
                            attrMapList, u, null,
                            ReconciliationResultCase.BROKEN_CSV));
                }
                continue;
            }

            // if (!isUnique(u, reconUserList)) {
            // report.add(new ReconciliationHTMLRow(preffix,
            // ReconciliationHTMLReportResults.NOT_UNIQUE_KEY, this
            // .objectToString(hList,
            // csvParser.convertToMap(attrMapList, u))));
            // continue;
            // }
            boolean isFind = false;
            boolean isMultiple = false;
            ReconciliationObject<User> finded = null;
            for (ReconciliationObject<User> o : dbUsers) {
                if (used.contains(o)) {
                    log.debug("already used");
                    continue;
                }
                if (!StringUtils.hasText(o.getPrincipal())) {
                    used.add(o);
                    continue;
                }
                if (o.getPrincipal().equals(u.getPrincipal())) {
                    if (!isFind) {
                        isFind = true;
                        finded = o;
                        used.add(finded);
                        continue;
                    } else {
                        isMultiple = true;
                        rows.add(this.setRowInReconciliationResult(headerRow,
                                attrMapList, u, null,
                                ReconciliationResultCase.NOT_UNIQUE_KEY));
                        break;
                    }
                }
            }

            if (!isFind) {
                rows.add(this.setRowInReconciliationResult(headerRow,
                        attrMapList, u, null,
                        ReconciliationResultCase.NOT_EXIST_IN_IDM_DB));
                // SYS_EXISTS__IDM_NOT_EXISTS
                if (!isManualRecon) {
                    ReconciliationCommand command = situations
                            .get(ReconciliationCommand.SYS_EXISTS__IDM_NOT_EXISTS);
                    if (command != null) {
                        Login l = new Login();
                        l.setDomainId(mSys.getDomainId());
                        l.setLogin(u.getPrincipal());
                        l.setManagedSysId(mSys.getManagedSysId());

                        ProvisionUser newUser = new ProvisionUser(u.getObject());
                        // ADD Target user principal
                        newUser.getPrincipalList().add(l);
                        log.debug("Call command for Match Found");
                        command.execute(l, newUser, null);
                    }
                }
            } else if (!isMultiple && finded != null) {
                if (finded.getObject().getPrincipalList().get(0) == null) {
                    if (UserStatusEnum.DELETED.equals(finded.getObject()
                            .getStatus())) {
                        rows.add(this.setRowInReconciliationResult(headerRow,
                                attrMapList, u, null,
                                ReconciliationResultCase.IDM_DELETED));
                        if (!isManualRecon) {
                            ReconciliationCommand command = situations
                                    .get(ReconciliationCommand.IDM_DELETED__SYS_EXISTS);
                            if (command != null) {
                                Login l = null;
                                if (CollectionUtils.isEmpty(finded.getObject()
                                        .getPrincipalList())) {
                                    l = finded.getObject().getPrincipalList()
                                            .get(0);
                                } else {
                                    l = new Login();
                                    l.setLogin(finded.getPrincipal());
                                    l.setDomainId(mSys.getDomainId());
                                    l.setManagedSysId(mSys.getManagedSysId());
                                }
                                log.debug("Call command for: Record in resource but deleted in IDM");
                                command.execute(l, u.getObject(), this
                                        .getExtensibleAttributesList(headerRow,
                                                attrMapList, finded));
                            }
                        }
                        continue;
                    }
                    rows.add(this.setRowInReconciliationResult(headerRow,
                            attrMapList, u, null,
                            ReconciliationResultCase.LOGIN_NOT_FOUND));
                    continue;
                } else {
                    if (!isManualRecon) {
                        ReconciliationCommand command = situations
                                .get(ReconciliationCommand.IDM_EXISTS__SYS_EXISTS);
                        if (command != null) {
                            Login l = new Login();
                            l.setLogin(u.getPrincipal());
                            l.setDomainId(mSys.getDomainId());
                            l.setManagedSysId(mSys.getManagedSysId());
                            log.debug("Call command for: Record in resource and in IDM");
                            command.execute(l,
                                    new ProvisionUser(u.getObject()), this
                                            .getExtensibleAttributesList(
                                                    headerRow, attrMapList, u));
                        }
                    }
                    rows.add(this.setRowInReconciliationResult(headerRow,
                            attrMapList, u, finded,
                            ReconciliationResultCase.MATCH_FOUND));
                    continue;
                }
                // FIXME fix login cheking
                // Login l = null;
                // List<Login> logins = loginManager.getLoginByUser(finded
                // .getObject().getUserId());
                // if (logins != null) {
                // for (Login login : logins) {
                // if (login.getId().getDomainId()
                // .equalsIgnoreCase(mSys.getDomainId())
                // ) {
                // l = login;
                // break;
                // }
                // }
                // }
                // if (l == null) {
                // if (UserStatusEnum.DELETED.equals(finded.getObject()
                // .getStatus())) {
                // report.add(new ReconciliationHTMLRow(preffix,
                // ReconciliationHTMLReportResults.IDM_DELETED,
                // this.objectToString(hList,
                // csvParser.convertToMap(attrMapList, u))));
                // continue;
                // }
                // report.add(new ReconciliationHTMLRow(preffix,
                // ReconciliationHTMLReportResults.LOGIN_NOT_FOUND,
                // this.objectToString(hList,
                // csvParser.convertToMap(attrMapList, u))));
                // continue;
                // } else {
                // report.add(new ReconciliationHTMLRow(preffix,
                // ReconciliationHTMLReportResults.MATCH_FOUND, this
                // .objectToString(
                // hList,
                // matchFields(csvParser.convertToMap(
                // attrMapList, u), csvParser
                // .convertToMap(attrMapList,
                // finded)))));
                // continue;
                // }
            }
        }
        return used;
    }

    protected List<ReconciliationObject<User>> getUsersFromCSV(
            ManagedSysEntity managedSys) throws Exception {
        List<AttributeMapEntity> attrMapList = managedSysService
                .getResourceAttributeMaps(managedSys.getResourceId());
        return userCSVParser.getObjects(managedSys, attrMapList, CSVSource.IDM);
    }

    protected Map<String, ReconciliationResultField> getUserProvisionMap(
            ReconciliationObject<User> obj, ManagedSysEntity managedSys)
            throws Exception {
        List<AttributeMapEntity> attrMapList = managedSysService
                .getResourceAttributeMaps(managedSys.getResourceId());
        return userCSVParser.convertToMap(attrMapList, obj);
    }

    private List<ExtensibleAttribute> getExtensibleAttributesList(
            ReconciliationResultRow headerRow,
            List<AttributeMapEntity> attrMapList, ReconciliationObject<User> u) {
        return UserUtils.reconciliationResultFieldMapToExtensibleAttributeList(
                headerRow, userCSVParser.convertToMap(attrMapList, u));
    }

    protected void addUsersToCSV(String principal, User newUser,
            ManagedSysEntity managedSys) throws Exception {
        List<AttributeMapEntity> attrMapList = managedSysService
                .getResourceAttributeMaps(managedSys.getResourceId());
        userCSVParser.add(new ReconciliationObject<User>(principal, newUser),
                managedSys, attrMapList, CSVSource.IDM);
    }

    protected void deleteUser(String principal, User newUser,
            ManagedSysEntity managedSys) throws Exception {
        List<AttributeMapEntity> attrMapList = managedSysService
                .getResourceAttributeMaps(managedSys.getResourceId());
        userCSVParser.delete(principal, managedSys, attrMapList, CSVSource.IDM);
    }

    protected void updateUser(ReconciliationObject<User> newUser,
            ManagedSysEntity managedSys) throws Exception {
        List<AttributeMapEntity> attrMapList = managedSysService
                .getResourceAttributeMaps(managedSys.getResourceId());
        userCSVParser.update(newUser, managedSys, attrMapList, CSVSource.IDM);
    }

    protected boolean lookupObjectInCSV(String findValue,
            ManagedSysEntity managedSys, List<ExtensibleObject> extOnjectList)
            throws Exception {
        List<ReconciliationObject<User>> users = this
                .getUsersFromCSV(managedSys);
        List<ExtensibleAttribute> eAttr = new ArrayList<ExtensibleAttribute>(0);

        for (ReconciliationObject<User> user : users) {
            ExtensibleObject extOnject = new ExtensibleObject();
            if (match(findValue, user, extOnject)) {
                Map<String, ReconciliationResultField> res = this
                        .getUserProvisionMap(user, managedSys);
                for (String key : res.keySet())
                    if (res.get(key) != null)
                        eAttr.add(new ExtensibleAttribute(key, user
                                .getPrincipal()));
                extOnject.setAttributes(eAttr);
                extOnjectList.add(extOnject);
                return true;
            }
        }
        return false;
    }

    protected boolean match(String findValue, ReconciliationObject<User> user2,
            ExtensibleObject extOnject) {
        if (!StringUtils.hasText(findValue) || user2 == null) {
            return false;
        }
        if (findValue.equals(user2.getPrincipal())) {
            extOnject.setObjectId(user2.getPrincipal());
            return true;
        }
        return false;
    }

}
