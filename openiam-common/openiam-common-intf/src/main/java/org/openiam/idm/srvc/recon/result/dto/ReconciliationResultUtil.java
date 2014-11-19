package org.openiam.idm.srvc.recon.result.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapUtil;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;

public class ReconciliationResultUtil {

    public static ReconciliationResultRow setHeaderInReconciliationResult(
            List<AttributeMap> attrMapList) {
        // Fill header
        ReconciliationResultRow headerRow = new ReconciliationResultRow();
        headerRow.setCaseReconciliation(ReconciliationResultCase.HEADER);
        List<ReconciliationResultField> fieldSet = new ArrayList<ReconciliationResultField>();
        for (AttributeMap head : attrMapList) {
            ReconciliationResultField field = new ReconciliationResultField();
            field.setValues(Arrays.asList(head.getAttributeName(),
                    AttributeMapUtil.getAttributeIDMFieldName(head)));
            fieldSet.add(field);
            if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(head.getMapForObjectType()))
                field.setKeyField(true);
        }
        headerRow.setFields(fieldSet);
        return headerRow;
    }
}
