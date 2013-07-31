package org.openiam.idm.srvc.recon.result.dto;

import java.util.Comparator;

public class ReconcliationFieldComparatorByField implements
        Comparator<ReconciliationResultRow> {
    private int fieldIndex;
    private String orderBy;

    @Override
    public int compare(ReconciliationResultRow o1, ReconciliationResultRow o2) {
        if (o1 == null && o2 == null)
            return 0;
        if (o1 != null && o2 == null)
            return 1;
        if (o1 == null && o2 != null)
            return -1;
        if (o1 != null && o2 != null) {
            String value1 = o1.getFields().get(fieldIndex).getValues().get(0);
            String value2 = o2.getFields().get(fieldIndex).getValues().get(0);
            int result = value1.compareToIgnoreCase(value2);
            if ("ASC".equals(orderBy)) {
                return result;
            }
            if ("DECR".equals(orderBy)) {
                return -1 * result;
            }
        }
        return 0;
    }

    public ReconcliationFieldComparatorByField(int fieldIndex, String orderBy) {
        super();
        this.fieldIndex = fieldIndex;
        this.orderBy = orderBy;
    }
}
