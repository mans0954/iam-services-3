package org.openiam.idm.srvc.recon.result.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;

/**
 * 
 * @author zaporozhec
 * 
 */
public class ReconciliationResultBean implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String objectType;
    private List<ReconciliationResultRow> rows;
    private ReconciliationResultRow header;
    private int counter = 0;

    public ReconciliationResultBean() {
        super();
        rows = new ArrayList<ReconciliationResultRow>();
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public List<ReconciliationResultRow> getRows() {
        return rows;
    }

    public void setRows(List<ReconciliationResultRow> rows) {
        counter = 0;
        if (rows != null)
            for (ReconciliationResultRow row : rows) {
                row.setRowId(counter++);
            }
        this.rows = rows;
    }

    public void addRow(ReconciliationResultRow row) {
        row.setRowId(counter++);
        rows.add(row);
    }

    public String toHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>");
        html.append("<title>Reconciliation result</title>");
        html.append("<style>");
        html.append("td {");
        html.append("font-size:14px;");
        html.append("}");

        html.append(".legend {");
        html.append("width:100%;");
        html.append("height:80px;");
        html.append("}");

        html.append(".legend-item {");
        html.append("width:10%;");
        html.append("height:100%;");
        html.append("padding:4px;");
        html.append("margin:3px;");
        html.append("float:left;");
        html.append("}");

        html.append(".clear {");
        html.append("clear:both;");
        html.append("}");

        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div>");
        html.append("<div style='position:fixed;top:0px;background-color:#ffffff'>");
        html.append("<h2>");
        html.append("Reconciliation result: ");
        html.append(Calendar.getInstance().getTime().toString());
        html.append("</h2>");
        html.append("<div class='legend'>");
        for (int i = 1; i < ReconciliationResultCase.values().length; i++)
            html.append(this.legendItem(ReconciliationResultCase.values()[i]));
        html.append("<div class='clear' />");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("<table style='margin-top:170px;' width='100%' border='1' cellspacing='0' cellpadding='0'>");
        for (ReconciliationResultRow row : rows) {
            html.append(row.toHTML());
        }
        html.append("</table>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    public String toCSV() {
        StringBuilder csv = new StringBuilder();
        for (ReconciliationResultRow row : rows) {
            csv.append(row.toCSV());
        }
        return csv.toString();
    }

    private String legendItem(ReconciliationResultCase a) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='legend-item' style='background-color:"
                + a.getColor() + ";'>");
        html.append("<p>");
        html.append(a.getValue());
        html.append("</p>");
        html.append("</div>");
        return html.toString();
    }

    public ReconciliationResultRow getHeader() {
        return header;
    }

    public void setHeader(ReconciliationResultRow header) {
        this.header = header;
    }
}
