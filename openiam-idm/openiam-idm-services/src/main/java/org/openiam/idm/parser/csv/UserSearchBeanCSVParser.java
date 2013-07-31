package org.openiam.idm.parser.csv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.am.srvc.constants.UserFields;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.user.dto.User;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service("userSearchCSVParser")
public class UserSearchBeanCSVParser extends
        AbstractCSVParser<UserSearchBean, UserFields> implements
        CSVParser<UserSearchBean> {

    @Override
    public ReconciliationObject<UserSearchBean> toReconciliationObject(
            UserSearchBean pu, List<AttributeMapEntity> attrMap) {
        return this.toReconciliationObject(pu, attrMap, UserFields.class);
    }

    @Override
    protected void putValueInDTO(UserSearchBean user, UserFields field,
            String objValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        switch (field) {
        case createDate:
            try {
                user.setCreateDate(sdf.parse(objValue));
            } catch (Exception e) {
                user.setCreateDate(null);
            }
            break;
        case employeeId:
            user.setEmployeeId(objValue);
            break;
        case firstName:
            user.setFirstName(objValue);
            break;
        case lastName:
            user.setLastName(objValue);
            break;
        case locationCd:
            user.setLocationCd(objValue);
            break;
        case classification:
            user.setClassification(objValue);
            break;
        case uid:
        case userId:
            user.setUserId(objValue);
            break;
        case userTypeInd:
            user.setUserTypeInd(objValue);
            break;
        case userNotes:
        case emailAddresses:
        case userAttributes:
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
        case showInSearch:
            try {
                user.setShowInSearch(Integer.valueOf(objValue));
            } catch (Exception e) {
                user.setShowInSearch(null);
            }
            break;
        case DEFAULT:
            break;
        default:
            break;
        }

    }

    @Override
    protected String putValueIntoString(UserSearchBean user, UserFields field) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String objValue = "";
        switch (field) {
        case createDate:
            objValue = user.getCreateDate() == null ? "" : toString(sdf
                    .format(user.getCreateDate()));
            break;
        case employeeId:
            objValue = toString(user.getEmployeeId());
            break;
        case firstName:
            objValue = toString(user.getFirstName());
            break;
        case lastName:
            objValue = toString(user.getLastName());
            break;
        case locationCd:
            objValue = toString(user.getLocationCd());
            break;
        case classification:
            objValue = toString(user.getClassification());
            break;
        case userId:
            objValue = toString(user.getUserId());
            break;
        case userTypeInd:
            objValue = toString(user.getUserTypeInd());
            break;
        case startDate:
            objValue = user.getStartDate() == null ? "" : toString(sdf
                    .format(user.getStartDate()));
            break;
        case lastDate:
            objValue = user.getLastDate() == null ? "" : toString(sdf
                    .format(user.getLastDate()));
            break;
        case showInSearch:
            objValue = toString(user.getShowInSearch());
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
    public void add(ReconciliationObject<UserSearchBean> newObject,
            ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
            CSVSource source) throws Exception {//
    }

    @Override
    public void delete(String principal, ManagedSysEntity managedSys,
            List<AttributeMapEntity> attrMapList, CSVSource source)
            throws Exception {//
    }

    @Override
    public void update(ReconciliationObject<UserSearchBean> newUser,
            ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
            CSVSource source) throws Exception {
        //
    }

    @Override
    public Map<String, ReconciliationResultField> convertToMap(
            List<AttributeMapEntity> attrMap,
            ReconciliationObject<UserSearchBean> obj) {
        return super.convertToMap(attrMap, obj, UserFields.class);
    }

    @Override
    public List<ReconciliationObject<UserSearchBean>> getObjects(
            ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
            CSVSource source) throws Exception {
        return getObjectList(managedSys, attrMapList, UserSearchBean.class,
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
                stb.append("");
            } else if (CollectionUtils.isEmpty(obj.get(h).getValues())
                    && obj.get(h).getValues().size() == 1) {
                stb.append(obj.get(h).getValues().get(0));
            } else if (CollectionUtils.isEmpty(obj.get(h).getValues())
                    && obj.get(h).getValues().size() > 1) {

                for (String value : obj.get(h).getValues()) {
                    stb.append("[");
                    stb.append(value);
                    stb.append("]");
                }
            }
            // stb.append(obj.get(h.trim()) == null ? "" : );
            stb.append(",");
        }
        stb.deleteCharAt(stb.length() - 1);
        return stb.toString();
    }

    @Override
    public String objectToString(List<String> head,
            List<AttributeMapEntity> attrMapList,
            ReconciliationObject<UserSearchBean> u) {
        return this.objectToString(head, this.convertToMap(attrMapList, u));
    }

    @Override
    public Map<String, ReconciliationResultField> matchFields(
            List<AttributeMapEntity> attrMap,
            ReconciliationObject<UserSearchBean> u,
            ReconciliationObject<UserSearchBean> o) {

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
        return this.getObjectSimlpeClass(UserSearchBean.class);
    }

    @Override
    public UserSearchBean getObjectByReconResltFields(
            List<ReconciliationResultField> header,
            List<ReconciliationResultField> objFieds, boolean onlyKeyField)
            throws InstantiationException, IllegalAccessException {
        return this.reconRowToObject(UserSearchBean.class, UserFields.class,
                header, objFieds, null, onlyKeyField);
    }

    @Override
    public UserSearchBean addObjectByReconResltFields(
            List<ReconciliationResultField> header,
            List<ReconciliationResultField> objFieds, UserSearchBean user)
            throws InstantiationException, IllegalAccessException {
        return this.reconRowToObject(UserSearchBean.class, UserFields.class,
                header, objFieds, user, false);
    }

}
