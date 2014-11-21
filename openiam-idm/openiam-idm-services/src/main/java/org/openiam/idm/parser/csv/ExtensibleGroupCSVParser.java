package org.openiam.idm.parser.csv;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.am.srvc.constants.GroupFields;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.provision.type.ExtensibleGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("extensibleGroupCSVParser")
public class ExtensibleGroupCSVParser extends AbstractCSVParser<ExtensibleGroup, GroupFields> implements CSVParser<ExtensibleGroup> {

    @Override
    protected void putValueInDTO(ExtensibleGroup obj, GroupFields field, String value) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    protected String putValueIntoString(ExtensibleGroup obj, GroupFields field) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public ReconciliationObject<ExtensibleGroup> toReconciliationObject(ExtensibleGroup pu, List<AttributeMapEntity> attrMap) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public List<ReconciliationObject<ExtensibleGroup>> getObjects(ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public void update(ReconciliationObject<ExtensibleGroup> newUser, ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public void delete(String principal, ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public void add(ReconciliationObject<ExtensibleGroup> newObject, ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public Map<String, ReconciliationResultField> convertToMap(List<AttributeMapEntity> attrMap, ReconciliationObject<ExtensibleGroup> obj) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public String objectToString(List<String> head, Map<String, ReconciliationResultField> obj) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public String objectToString(List<String> head, List<AttributeMapEntity> attrMapList, ReconciliationObject<ExtensibleGroup> u) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public ExtensibleGroup getObjectByReconResltFields(List<ReconciliationResultField> header, List<ReconciliationResultField> objFieds, boolean onlyKeyField) throws InstantiationException, IllegalAccessException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public Map<String, ReconciliationResultField> matchFields(List<AttributeMapEntity> attrMap, ReconciliationObject<ExtensibleGroup> u, ReconciliationObject<ExtensibleGroup> o) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public String getObjectSimlpeClass() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public ExtensibleGroup addObjectByReconResltFields(List<ReconciliationResultField> header, List<ReconciliationResultField> objFieds, ExtensibleGroup user) throws InstantiationException, IllegalAccessException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
