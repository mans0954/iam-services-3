package org.openiam.idm.parser.csv;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.am.srvc.constants.UserFields;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/9/13
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("extensibleUserCSVParser")
public class ExtensibleUserCSVParser extends AbstractCSVParser<ExtensibleUser, UserFields> implements CSVParser<ExtensibleUser> {

    @Override
    protected void putValueInDTO(ExtensibleUser extUser, UserFields field, String objValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        switch (field) {
            case birthdate:
            case companyOwnerId:
            case createDate:
            case createdBy:
            case employeeId:
            case employeeType:
            case firstName:
            case jobCode:
            case lastName:
            case lastUpdate:
            case lastUpdatedBy:
            case locationCd:
            case locationName:
            case metadataTypeId:
            case classification:
            case middleInit:
            case prefix:
            case sex:
            case status:
            case secondaryStatus:
            case suffix:
            case title:
            case uid:
            case userId:
            case userTypeInd:
            case userNotes:
            case emailAddresses:
            case costCenter:
            case startDate:
            case lastDate:
            case mailCode:
            case nickname:
            case maidenName:
            case passwordTheme:
            case mail:
            case email:
            case emailAddress:
            case showInSearch:
            case alternateContactId:
            case securityDomain:
            case userOwnerId:
            case datePasswordChanged:
            case dateITPolicyApproved:
            case dateChallengeRespChanged:
                extUser.getAttributes().add(new ExtensibleAttribute(field.name(), objValue, 1, "String"));
            case DEFAULT:
            case principalList:
            case phones:
            case userAttributes:
                break;
            default:
                break;

        }
    }

    @Override
    protected String putValueIntoString(ExtensibleUser obj, UserFields field) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String objValue = getValueFromObject(field, obj.getAttributes());
        return objValue;
    }

    private String getValueFromObject(UserFields field, List<ExtensibleAttribute> attributes) {
        String value = "";
        if(CollectionUtils.isNotEmpty(attributes)){
           for (ExtensibleAttribute attr : attributes){
                  if(field.name().equals(attr.getName())){
                      value = attr.getValue();
                      break;
                  }
           }
        }
        return value;
    }

    @Override
    public ReconciliationObject<ExtensibleUser> toReconciliationObject(ExtensibleUser pu, List<AttributeMapEntity> attrMap) {
        return this.toReconciliationObject(pu, attrMap, UserFields.class);
    }

    @Override
    public List<ReconciliationObject<ExtensibleUser>> getObjects(ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        return getObjectList(managedSys, attrMapList, ExtensibleUser.class,
                UserFields.class, source);
    }

    @Override
    public void update(ReconciliationObject<ExtensibleUser> newUser, ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        List<ReconciliationObject<ExtensibleUser>> users = this.getObjects(managedSys,
                attrMapList, source);
        List<ReconciliationObject<ExtensibleUser>> newUsers = new ArrayList<ReconciliationObject<ExtensibleUser>>(0);
        for (ReconciliationObject<ExtensibleUser> user : users) {
            if (newUser.getPrincipal().equals(user.getPrincipal())) {
                newUsers.add(newUser);
            } else {
                newUsers.add(user);
            }
        }
        updateCSV(users, managedSys, attrMapList, ExtensibleUser.class, UserFields.class,
                false, source);
    }

    @Override
    public void delete(String principal, ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        List<ReconciliationObject<ExtensibleUser>> users = this.getObjects(managedSys,
                attrMapList, source);
        Iterator<ReconciliationObject<ExtensibleUser>> userIter = users.iterator();
        while (userIter.hasNext()) {
            ReconciliationObject<ExtensibleUser> user = userIter.next();
            if (principal != null) {
                if (principal.equals(user.getPrincipal())) {
                    userIter.remove();
                }
            }
        }
        updateCSV(users, managedSys, attrMapList, ExtensibleUser.class, UserFields.class,
                false, source);
    }

    @Override
    public void add(ReconciliationObject<ExtensibleUser> newObject, ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        appendObjectToCSV(newObject, managedSys, attrMapList, ExtensibleUser.class,
                UserFields.class, true, source);
    }

    @Override
    public Map<String, ReconciliationResultField> convertToMap(List<AttributeMapEntity> attrMap, ReconciliationObject<ExtensibleUser> obj) {
        return super.convertToMap(attrMap, obj, UserFields.class);
    }

    @Override
    public String objectToString(List<String> head, Map<String, ReconciliationResultField> obj) {
        StringBuilder stb = new StringBuilder();
        for (String h : head) {
            if (obj.get(h.trim()) == null) {
                stb.append(",");
            } else {
                ReconciliationResultField field = obj.get(h);
                stb.append(StringUtils.arrayToDelimitedString(field.getValues()
                        .toArray(), "\\"));
                stb.append(",");
            }
        }
        stb.deleteCharAt(stb.length() - 1);
        return stb.toString();
    }

    @Override
    public String objectToString(List<String> head, List<AttributeMapEntity> attrMapList, ReconciliationObject<ExtensibleUser> u) {
        return this.objectToString(head, this.convertToMap(attrMapList, u));
    }

    @Override
    public ExtensibleUser getObjectByReconResltFields(List<ReconciliationResultField> header, List<ReconciliationResultField> objFieds, boolean onlyKeyField) throws InstantiationException, IllegalAccessException {
        return this.reconRowToObject(ExtensibleUser.class, UserFields.class, header,
                objFieds, null, onlyKeyField);
    }

    @Override
    public Map<String, ReconciliationResultField> matchFields(List<AttributeMapEntity> attrMap, ReconciliationObject<ExtensibleUser> u, ReconciliationObject<ExtensibleUser> o) {
        Map<String, ReconciliationResultField> res = new HashMap<String, ReconciliationResultField>(
                0);
        Map<String, ReconciliationResultField> one = this.convertToMap(attrMap,
                u);
        Map<String, ReconciliationResultField> two = this.convertToMap(attrMap,
                o);
        for (String field : one.keySet()) {
            ReconciliationResultField resultField = new ReconciliationResultField();
            if (one.get(field) == null && two.get(field) == null) {
                res.put(field, null);
                continue;
            }
            if (one.get(field) == null && two.get(field) != null) {
                resultField.setValues(two.get(field).getValues());
                res.put(field, resultField);
                continue;
            }
            if (one.get(field) != null && two.get(field) == null) {
                resultField.setValues(one.get(field).getValues());
                res.put(field, resultField);
                continue;
            }
            if (one.get(field) != null && two.get(field) != null) {
                ReconciliationResultField firstVal = one.get(field);
                ReconciliationResultField secondVal = two.get(field);
                if (firstVal.equals(secondVal)) {
                    resultField.setValues(secondVal.getValues());
                } else {
                    resultField.setValues(new ArrayList<String>(firstVal
                            .getValues()));
                    resultField.getValues().addAll(secondVal.getValues());
                }
                res.put(field, resultField);
                continue;
            }
        }

        return res;
    }

    @Override
    public String getObjectSimlpeClass() {
        return this.getObjectSimlpeClass(ExtensibleUser.class);
    }

    @Override
    public ExtensibleUser addObjectByReconResltFields(List<ReconciliationResultField> header, List<ReconciliationResultField> objFieds, ExtensibleUser user) throws InstantiationException, IllegalAccessException {
        return this.reconRowToObject(ExtensibleUser.class, UserFields.class, header,
                objFieds, user, false);
    }
}
