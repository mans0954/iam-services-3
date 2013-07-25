package org.openiam.idm.srvc.recon.result.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;

public class ReconciliationResultUtil {
    public static List<String> getHeader(List<AttributeMapEntity> attrMapList) {
        // Fill header
        List<String> hList = new ArrayList<String>(0);
        for (AttributeMapEntity map : attrMapList) {
            hList.add(map.getAttributeName());
        }
        return hList;
    }

    public static ReconciliationResultRow setHeaderInReconciliationResult(
            List<AttributeMapEntity> attrMapList) {
        List<String> hList = ReconciliationResultUtil.getHeader(attrMapList);
        // Fill header
        ReconciliationResultRow headerRow = new ReconciliationResultRow();
        headerRow.setCaseReconciliation(ReconciliationReportCase.HEADER);
        List<ReconciliationResultField> fieldSet = new ArrayList<ReconciliationResultField>();
        int i = 0;
        for (String head : hList) {
            ReconciliationResultField field = new ReconciliationResultField();
            field.setValues(Arrays.asList(head));
            field.setDisplayOrder(++i);
            fieldSet.add(field);
        }
        headerRow.setFields(fieldSet);
        return headerRow;
    }
}
