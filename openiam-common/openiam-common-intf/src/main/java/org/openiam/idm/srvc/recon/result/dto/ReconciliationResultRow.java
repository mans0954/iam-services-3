package org.openiam.idm.srvc.recon.result.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * 
 * @author zaporozhec
 * 
 */
public class ReconciliationResultRow implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int rowId;

    private ReconciliationResultCase caseReconciliation;
    private List<ReconciliationResultField> fields = new ArrayList<ReconciliationResultField>();
    private ReconciliationResultAction action;

    public ReconciliationResultAction getAction() {
        return action;
    }

    public void setAction(ReconciliationResultAction action) {
        this.action = action;
    }

    public ReconciliationResultCase getCaseReconciliation() {
        return caseReconciliation;
    }

    public void setCaseReconciliation(
            ReconciliationResultCase caseReconciliation) {
        this.caseReconciliation = caseReconciliation;
    }

    public List<ReconciliationResultField> getFields() {
        return fields;
    }

    public void setFields(List<ReconciliationResultField> fields) {
        this.fields = fields;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReconciliationResultRow other = (ReconciliationResultRow) obj;
        if (rowId != other.rowId)
            return false;
        return true;
    }

    public String toCSV() {
        StringBuilder build = new StringBuilder();
        build.append("\"");
        build.append(this.caseReconciliation.getValue());
        build.append("\"");
        build.append(',');
        if (!CollectionUtils.isEmpty(fields)) {
            for (ReconciliationResultField field : fields) {
                build.append(field.toCSV());
                build.append(',');
            }
        }
        build.deleteCharAt(build.length() - 1);
        build.append('\n');
        return build.toString();
    }

    public String toHTML() {
        StringBuilder tr = new StringBuilder();
        if (caseReconciliation == null) {
            return "<tr></tr>";

        }
        tr.append("<tr>");
        tr.append("<td  style='background-color:"
                + caseReconciliation.getColor() + "'>");
        tr.append(caseReconciliation.getValue());
        tr.append("</td>");
        if (!CollectionUtils.isEmpty(fields)) {
            for (ReconciliationResultField field : fields) {
                tr.append(field.toHTML());
            }
        }
        tr.append("</tr>");
        return tr.toString();
    }
}
