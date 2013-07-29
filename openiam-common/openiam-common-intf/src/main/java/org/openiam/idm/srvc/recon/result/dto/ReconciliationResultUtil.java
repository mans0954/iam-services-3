package org.openiam.idm.srvc.recon.result.dto;

import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapUtil;

public class ReconciliationResultUtil {

    public static ReconciliationResultRow setHeaderInReconciliationResult(
            List<AttributeMapEntity> attrMapList) {
        // Fill header
        ReconciliationResultRow headerRow = new ReconciliationResultRow();
        headerRow.setCaseReconciliation(ReconciliationResultCase.HEADER);
        List<ReconciliationResultField> fieldSet = new ArrayList<ReconciliationResultField>();
        for (AttributeMapEntity head : attrMapList) {
            ReconciliationResultField field = new ReconciliationResultField();
            field.setValues(Arrays.asList(head.getAttributeName(),
                    AttributeMapUtil.getAttributeIDMFieldName(head)));
            fieldSet.add(field);
            if ("PRINCIPAL".equalsIgnoreCase(head.getMapForObjectType()))
                field.setKeyField(true);
        }
        headerRow.setFields(fieldSet);
        return headerRow;
    }
}
