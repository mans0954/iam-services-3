package org.openiam.idm.srvc.recon.result.dto;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTML;

import org.springframework.util.CollectionUtils;

/**
 * 
 * @author zaporozhec
 * 
 */
public class ReconciliationResultField implements java.io.Serializable {
    private static final String CONFLICT_COLOR = "#ff4455";
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int displayOrder;
    List<String> values = new ArrayList<String>();

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String toCSV() {
        StringBuilder row = new StringBuilder();
        row.append("\"");
        if (!CollectionUtils.isEmpty(values)) {

            for (String val : values) {
                row.append(val);
                row.append("/");
            }
        }
        row.deleteCharAt(row.length() - 1);
        row.append("\"");
        return row.toString();
    }

    public String toHTML() {
        StringBuilder td = new StringBuilder();
        if (CollectionUtils.isEmpty(values)) {
            return "<td></td>";
        }
        if (values.size() == 1) {
            td.append("<td>");
            td.append(values.get(0));
            td.append("</td>");
        } else if (values.size() > 1) {
            td.append("<td style='background-color:" + CONFLICT_COLOR + "'>");
            for (String val : values) {
                td.append(val);
                td.append("\\");
            }
            td.deleteCharAt(td.length() - 1);
            td.append("</td>");
        }
        return td.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReconciliationResultField other = (ReconciliationResultField) obj;
        if (displayOrder != other.displayOrder)
            return false;
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
            return false;
        return true;
    }
}
