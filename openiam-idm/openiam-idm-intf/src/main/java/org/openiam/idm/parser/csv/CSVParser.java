package org.openiam.idm.parser.csv;

import java.util.List;
import java.util.Map;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;

public interface CSVParser<T> {
	ReconciliationObject<T> toReconciliationObject(T pu,
			List<AttributeMapEntity> attrMap);

	List<ReconciliationObject<T>> getObjects(ManagedSysEntity managedSys,
			List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception;

	void update(ReconciliationObject<T> newUser, ManagedSysEntity managedSys,
			List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception;

	void delete(String principal, ManagedSysEntity managedSys,
			List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception;

	void add(ReconciliationObject<T> newObject, ManagedSysEntity managedSys,
			List<AttributeMapEntity> attrMapList, CSVSource source) throws Exception;

	Map<String, String> convertToMap(List<AttributeMapEntity> attrMap,
			ReconciliationObject<T> obj);

	public String getFileName(ManagedSysEntity mngSys, CSVSource source);

	String objectToString(List<String> head, Map<String, String> obj);

	String objectToString(List<String> head, List<AttributeMapEntity> attrMapList,
			ReconciliationObject<T> u);

	Map<String, String> matchFields(List<AttributeMapEntity> attrMap,
			ReconciliationObject<T> u, ReconciliationObject<T> o);
}
