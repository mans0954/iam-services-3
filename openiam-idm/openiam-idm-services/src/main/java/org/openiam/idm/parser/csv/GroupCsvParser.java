package org.openiam.idm.parser.csv;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.am.srvc.constants.GroupFields;
import org.openiam.am.srvc.constants.UserFields;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.user.dto.User;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("groupCsvParser")
public class GroupCsvParser extends AbstractCSVParser<Group, GroupFields> implements CSVParser<Group> {
    @Override
    protected void putValueInDTO(Group group, GroupFields field, String objValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        switch (field) {
            case grpId:
                group.setGrpId(objValue);
                break;
            case grpName:
                group.setGrpName(objValue);
                break;
            case createDate:
                try {
                    group.setCreateDate(sdf.parse(objValue));
                } catch (Exception e) {
                    group.setCreateDate(null);
                }
                break;
            case createdBy:
                group.setCreatedBy(objValue);
                break;
            case companyId:
                group.setCompanyId(objValue);
                break;
            case ownerId:
                group.setOwnerId(objValue);
                break;
            case description:
                group.setDescription(objValue);
                break;
            case status:
                group.setStatus(objValue);
                break;
            case lastUpdate:
                try {
                    group.setLastUpdate(sdf.parse(objValue));
                } catch (Exception e) {
                    group.setLastUpdate(null);
                }
                break;
            case lastUpdatedBy:
                group.setLastUpdatedBy(objValue);
                break;
            case metadataTypeId:
                group.setMetadataTypeId(objValue);
                break;
            case internalGroupId:
                group.setInternalGroupId(objValue);
                break;
            default:
                break;
        }
    }

    @Override
    protected String putValueIntoString(Group group, GroupFields field) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String objValue = "";
        switch (field) {
            case grpId:
                objValue = group.getGrpId();
                break;
            case grpName:
                objValue = group.getGrpName();
                break;
            case createDate:
                objValue = group.getLastUpdate() == null ? "" : toString(sdf
                        .format(group.getLastUpdate()));
                break;
            case createdBy:
                objValue = group.getCreatedBy();
                break;
            case companyId:
                objValue = group.getCompanyId();
                break;
            case ownerId:
                objValue = group.getOwnerId();
                break;
            case description:
                objValue = group.getDescription();
                break;
            case status:
                objValue = group.getStatus();
                break;
            case lastUpdate:
                objValue = group.getLastUpdate() == null ? "" : toString(sdf
                        .format(group.getLastUpdate()));
                break;
            case lastUpdatedBy:
                objValue = group.getLastUpdatedBy();
                break;
            case metadataTypeId:
                objValue = group.getMetadataTypeId();
                break;
            case internalGroupId:
                objValue = group.getInternalGroupId();
                break;
            default:
                break;
        }
        return objValue;
    }

    @Override
    public ReconciliationObject<Group> toReconciliationObject(Group group, List<AttributeMapEntity> attrMap) {
        return this.toReconciliationObject(group, attrMap, GroupFields.class);
    }

    @Override
    public List<ReconciliationObject<Group>> getObjects(ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        return getObjectList(managedSys, attrMapList, Group.class, GroupFields.class, source);
    }

    @Override
    public void update(ReconciliationObject<Group> newGroup, ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        List<ReconciliationObject<Group>> groups = this.getObjects(managedSys,
                attrMapList, source);
        List<ReconciliationObject<Group>> newGroups = new ArrayList<ReconciliationObject<Group>>(
                0);
        for (ReconciliationObject<Group> gr : groups) {
            if (newGroup.getPrincipal().equals(gr.getPrincipal())) {
                newGroups.add(newGroup);
            } else {
                newGroups.add(gr);
            }
        }
        updateCSV(groups, managedSys, attrMapList, Group.class, GroupFields.class,
                false, source);
    }

    @Override
    public void delete(String principal, ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        List<ReconciliationObject<Group>> groups = this.getObjects(managedSys,
                attrMapList, source);
        Iterator<ReconciliationObject<Group>> iter = groups.iterator();
        while (iter.hasNext()) {
            ReconciliationObject<Group> group = iter.next();
            if (principal != null) {
                if (principal.equals(group.getPrincipal())) {
                    iter.remove();
                }
            }
        }
        updateCSV(groups, managedSys, attrMapList, Group.class, GroupFields.class,
                false, source);
    }

    @Override
    public void add(ReconciliationObject<Group> newObject, ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        appendObjectToCSV(newObject, managedSys, attrMapList, Group.class,
                GroupFields.class, true, source);
    }

    @Override
    public Map<String, ReconciliationResultField> convertToMap(List<AttributeMapEntity> attrMap, ReconciliationObject<Group> obj) {
        return super.convertToMap(attrMap, obj, GroupFields.class);
    }

    @Override
    public String objectToString(List<String> head, Map<String, ReconciliationResultField> obj) {
        StringBuilder stb = new StringBuilder();
        for (String h : head) {
            stb.append(obj.get(h.trim()) == null ? "" : obj.get(h));
            stb.append(",");
        }
        stb.deleteCharAt(stb.length() - 1);
        return stb.toString();
    }

    @Override
    public String objectToString(List<String> head,
                                 List<AttributeMapEntity> attrMapList, ReconciliationObject<Group> u) {
        return this.objectToString(head, this.convertToMap(attrMapList, u));
    }

    @Override
    public Group getObjectByReconResltFields(List<ReconciliationResultField> header, List<ReconciliationResultField> objFieds, boolean onlyKeyField) throws InstantiationException, IllegalAccessException {
        return this.reconRowToObject(Group.class, GroupFields.class, header,
                objFieds, null, onlyKeyField);
    }

    @Override
    public Map<String, ReconciliationResultField> matchFields(List<AttributeMapEntity> attrMap,
                                           ReconciliationObject<Group> u, ReconciliationObject<Group> o) {
        Map<String, ReconciliationResultField> res = new HashMap<String, ReconciliationResultField>(0);
        Map<String, ReconciliationResultField> one = this.convertToMap(attrMap, u);
        Map<String, ReconciliationResultField> two = this.convertToMap(attrMap, o);
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
        return this.getObjectSimlpeClass(Group.class);
    }

    @Override
    public Group addObjectByReconResltFields(List<ReconciliationResultField> header, List<ReconciliationResultField> objFieds, Group user) throws InstantiationException, IllegalAccessException {
        return this.reconRowToObject(Group.class, GroupFields.class, header,
                objFieds, user, false);
    }
}
