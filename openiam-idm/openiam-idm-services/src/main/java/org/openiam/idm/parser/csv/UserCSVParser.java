package org.openiam.idm.parser.csv;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.am.srvc.constants.UserFields;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("userCSVParser")
public class UserCSVParser extends AbstractCSVParser<User, UserFields>
        implements CSVParser<User> {

    @Override
    public ReconciliationObject<User> toReconciliationObject(User pu,
            List<AttributeMapEntity> attrMap) {
        return this.toReconciliationObject(pu, attrMap, UserFields.class);
    }

    @Override
    protected void putValueInDTO(User user, UserFields field, String objValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        switch (field) {
        case birthdate:
            try {
                user.setBirthdate(sdf.parse(objValue));
            } catch (Exception e) {
                user.setBirthdate(null);
            }
            break;
        case companyOwnerId:
            user.setCompanyOwnerId(objValue);
            break;
        case createDate:
            try {
                user.setCreateDate(sdf.parse(objValue));
            } catch (Exception e) {
                user.setCreateDate(null);
            }
            break;
        case createdBy:
            user.setCreatedBy(objValue);
            break;
        case employeeId:
            user.setEmployeeId(objValue);
            break;
        case employeeType:
            user.setEmployeeType(objValue);
            break;
        case firstName:
            user.setFirstName(objValue);
            break;
        case jobCode:
            user.setJobCode(objValue);
            break;
        case lastName:
            user.setLastName(objValue);
            break;
        case lastUpdate:
            try {
                user.setLastUpdate(sdf.parse(objValue));
            } catch (Exception e) {
                user.setLastUpdate(null);
            }
            break;
        case lastUpdatedBy:
            user.setLastUpdatedBy(objValue);
            break;
        case locationCd:
            user.setLocationCd(objValue);
            break;
        case locationName:
            user.setLocationName(objValue);
            break;
        case metadataTypeId:
            user.setMetadataTypeId(objValue);
            break;
        case classification:
            user.setClassification(objValue);
            break;
        case middleInit:
            user.setMiddleInit(objValue);
            break;
        case prefix:
            user.setPrefix(objValue);
            break;
        case sex:
            user.setSex(objValue);
            break;
        case status:
            user.setStatus(Enum.valueOf(UserStatusEnum.class,
                    objValue.toUpperCase()));
            break;
        case secondaryStatus:
            user.setSecondaryStatus(Enum.valueOf(UserStatusEnum.class,
                    objValue.toUpperCase()));
            break;
        case suffix:
            user.setSuffix(objValue);
            break;
        case title:
            user.setTitle(objValue);
            break;
        case uid:
        case userId:
            user.setId(objValue);
            break;
        case userTypeInd:
            user.setUserTypeInd(objValue);
            break;
        case userNotes:
        case emailAddresses:
        case userAttributes:
            break;
        case costCenter:
            user.setCostCenter(objValue);
            break;
        case startDate:
            try {
                user.setStartDate(sdf.parse(objValue));
            } catch (Exception e) {
                user.setStartDate(null);
            }
            break;
        case lastDate:
            try {
                user.setLastDate(sdf.parse(objValue));
            } catch (Exception e) {
                user.setLastDate(null);
            }
            break;
        case mailCode:
            user.setMailCode(objValue);
            break;
        case nickname:
            user.setNickname(objValue);
            break;
        case maidenName:
            user.setMaidenName(objValue);
            break;
        case passwordTheme:
            user.setPasswordTheme(objValue);
            break;
        case mail:
        case email:
        case emailAddress:
            user.setEmail(objValue);
            break;
        case showInSearch:
            try {
                user.setShowInSearch(Integer.valueOf(objValue));
            } catch (Exception e) {
                user.setShowInSearch(null);
            }
            break;
        case principalList:
        case phones:
            break;
        case alternateContactId:
            user.setAlternateContactId(objValue);
            break;
        case userOwnerId:
            user.setUserOwnerId(objValue);
            break;
        case datePasswordChanged:
            try {
                user.setDatePasswordChanged(sdf.parse(objValue));
            } catch (Exception e) {
                user.setDatePasswordChanged(null);
            }
            break;
        case dateITPolicyApproved:
            try {
                user.setDateITPolicyApproved(sdf.parse(objValue));
            } catch (Exception e) {
                user.setDateITPolicyApproved(null);
            }
            break;
        case dateChallengeRespChanged:
            try {
                user.setDateChallengeRespChanged(sdf.parse(objValue));
            } catch (Exception e) {
                user.setDateChallengeRespChanged(null);
            }
            break;
        case DEFAULT:
            break;
        default:
            break;

        }
    }

    @Override
    protected String putValueIntoString(User user, UserFields field) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String objValue = "";
        switch (field) {
        case birthdate:
            objValue = user.getBirthdate() == null ? "" : toString(sdf
                    .format(user.getBirthdate()));
            break;
        case companyOwnerId:
            objValue = toString(user.getCompanyOwnerId());
            break;
        case createDate:
            objValue = user.getCreateDate() == null ? "" : toString(sdf
                    .format(user.getCreateDate()));
            break;
        case createdBy:
            objValue = toString(user.getCreatedBy());
            break;
        case employeeId:
            objValue = toString(user.getEmployeeId());
            break;
        case employeeType:
            objValue = toString(user.getEmployeeType());
            break;
        case firstName:
            objValue = toString(user.getFirstName());
            break;
        case jobCode:
            objValue = toString(user.getJobCode());
            break;
        case lastName:
            objValue = toString(user.getLastName());
            break;
        case lastUpdate:
            objValue = user.getLastUpdate() == null ? "" : toString(sdf
                    .format(user.getLastUpdate()));
            break;
        case lastUpdatedBy:
            objValue = toString(user.getLastUpdatedBy());
            break;
        case locationCd:
            objValue = toString(user.getLocationCd());
            break;
        case locationName:
            objValue = toString(user.getLocationName());
            break;
        case metadataTypeId:
            objValue = toString(user.getMetadataTypeId());
            break;
        case classification:
            objValue = toString(user.getClassification());
            break;
        case middleInit:
            objValue = toString(user.getMiddleInit());
            break;
        case prefix:
            objValue = toString(user.getPrefix());
            break;
        case sex:
            objValue = toString(user.getSex());
            break;
        case status:
            objValue = toString(user.getStatus());
            break;
        case secondaryStatus:
            objValue = toString(user.getSecondaryStatus());
            break;
        case suffix:
            objValue = toString(user.getSuffix());
            break;
        case title:
            objValue = toString(user.getTitle());
            break;
        case uid:
        case userId:
            objValue = toString(user.getId());
            break;
        case userTypeInd:
            objValue = toString(user.getUserTypeInd());
            break;
        case costCenter:
            objValue = toString(user.getCostCenter());
            break;
        case startDate:
            objValue = user.getStartDate() == null ? "" : toString(sdf
                    .format(user.getStartDate()));
            break;
        case lastDate:
            objValue = user.getLastDate() == null ? "" : toString(sdf
                    .format(user.getLastDate()));
            break;
        case mailCode:
            objValue = toString(user.getMailCode());
            break;
        case nickname:
            objValue = toString(user.getNickname());
            break;
        case maidenName:
            objValue = toString(user.getMaidenName());
            break;
        case passwordTheme:
            objValue = toString(user.getPasswordTheme());
            break;
        case mail:
        case email:
        case emailAddress:
            objValue = toString(user.getEmail());
            break;
        case showInSearch:
            objValue = toString(user.getShowInSearch());
            break;
        case alternateContactId:
            objValue = toString(user.getAlternateContactId());
            break;
        case userOwnerId:
            objValue = toString(user.getUserOwnerId());
            break;
        case datePasswordChanged:
            objValue = user.getDatePasswordChanged() == null ? ""
                    : toString(sdf.format(user.getDatePasswordChanged()));
            break;
        case dateITPolicyApproved:
            objValue = user.getDateITPolicyApproved() == null ? ""
                    : toString(sdf.format(user.getDateITPolicyApproved()));
            break;
        case dateChallengeRespChanged:
            objValue = user.getDateChallengeRespChanged() == null ? ""
                    : toString(sdf.format(user.getDateChallengeRespChanged()));
            break;
        case userAttributes:
        case principalList:
        case userNotes:
        case phones:
        case emailAddresses:
            break;
        case DEFAULT:
            objValue = toString("");
            break;
        default:
            break;
        }
        return objValue;
    }

    @Override
    public void add(ReconciliationObject<User> newObject,
            ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
            CSVSource source) throws Exception {
        appendObjectToCSV(newObject, managedSys, attrMapList, User.class,
                UserFields.class, true, source);
    }

    @Override
    public void delete(String principal, ManagedSysEntity managedSys,
            List<AttributeMapEntity> attrMapList, CSVSource source)
            throws Exception {
        List<ReconciliationObject<User>> users = this.getObjects(managedSys,
                attrMapList, source);
        Iterator<ReconciliationObject<User>> userIter = users.iterator();
        while (userIter.hasNext()) {
            ReconciliationObject<User> user = userIter.next();
            if (principal != null) {
                if (principal.equals(user.getPrincipal())) {
                    userIter.remove();
                }
            }
        }
        updateCSV(users, managedSys, attrMapList, User.class, UserFields.class,
                false, source);
    }

    @Override
    public void update(ReconciliationObject<User> newUser,
            ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
            CSVSource source) throws Exception {
        List<ReconciliationObject<User>> users = this.getObjects(managedSys,
                attrMapList, source);
        List<ReconciliationObject<User>> newUsers = new ArrayList<ReconciliationObject<User>>(
                0);
        for (ReconciliationObject<User> user : users) {
            if (newUser.getPrincipal().equals(user.getPrincipal())) {
                newUsers.add(newUser);
            } else {
                newUsers.add(user);
            }
        }
        updateCSV(users, managedSys, attrMapList, User.class, UserFields.class,
                false, source);
    }

    @Override
    public Map<String, ReconciliationResultField> convertToMap(
            List<AttributeMapEntity> attrMap, ReconciliationObject<User> obj) {
        return super.convertToMap(attrMap, obj, UserFields.class);
    }

    @Override
    public List<ReconciliationObject<User>> getObjects(
            ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
            CSVSource source) throws Exception {
        return getObjectList(managedSys, attrMapList, User.class,
                UserFields.class, source);
    }

    @Override
    public String getFileName(ManagedSysEntity mngSys, CSVSource source) {
        return super.getFileName(mngSys, source);
    }

    @Override
    public String objectToString(List<String> head,
            Map<String, ReconciliationResultField> obj) {
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
    public String objectToString(List<String> head,
            List<AttributeMapEntity> attrMapList, ReconciliationObject<User> u) {
        return this.objectToString(head, this.convertToMap(attrMapList, u));
    }

    @Override
    public Map<String, ReconciliationResultField> matchFields(
            List<AttributeMapEntity> attrMap, ReconciliationObject<User> u,
            ReconciliationObject<User> o) {

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
    public User getObjectByReconResltFields(
            List<ReconciliationResultField> header,
            List<ReconciliationResultField> objFieds, boolean onlyKeyField)
            throws InstantiationException, IllegalAccessException {
        return this.reconRowToObject(User.class, UserFields.class, header,
                objFieds, null, onlyKeyField);
    }

    @Override
    public User addObjectByReconResltFields(
            List<ReconciliationResultField> header,
            List<ReconciliationResultField> objFieds, User user)
            throws InstantiationException, IllegalAccessException {
        return this.reconRowToObject(User.class, UserFields.class, header,
                objFieds, user, false);
    }

    @Override
    public String getObjectSimlpeClass() {
        return this.getObjectSimlpeClass(User.class);
    }
}
