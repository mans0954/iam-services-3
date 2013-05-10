package org.openiam.idm.srvc.recon.report;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;

public class ReconciliationReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ReconciliationReportRow> report = new ArrayList<ReconciliationReportRow>();

	public List<ReconciliationReportRow> getReport() {
		return report;
	}

	public void setReport(List<ReconciliationReportRow> report) {
		this.report = report;
	}

	public String toCSV() {
		StringBuilder csv = new StringBuilder();
		for (ReconciliationReportRow row : report) {
			csv.append(row.toCSV());
		}
		return csv.toString();
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
		for (ReconciliationReportResults a : ReconciliationReportResults
				.values()) {
			html.append(this.legendItem(a));
		}
		html.append("<div class='clear' />");
		html.append("</div>");
		html.append("</div>");
		html.append("</div>");
		html.append("<table style='margin-top:170px;' width='100%' border='1' cellspacing='0' cellpadding='0'>");
		for (ReconciliationReportRow row : report) {
			html.append(row.toHTML());
		}
		html.append("</table>");
		html.append("</body>");
		html.append("</html>");

		return html.toString();
	}

	private String legendItem(ReconciliationReportResults a) {
		StringBuilder html = new StringBuilder();
		html.append("<div class='legend-item' style='background-color:"
				+ a.getColor() + ";'>");
		html.append("<p>");
		html.append(a.getValue());
		html.append("</p>");
		html.append("</div>");
		return html.toString();
	}

	public void save(String pathToCSV, ManagedSysEntity mSys, boolean isHTML)
			throws IOException {
		StringBuilder sb = new StringBuilder(pathToCSV);
		sb.append("report_");
		sb.append(mSys.getResourceId());
		if (isHTML)
			sb.append(".html");
		else
			sb.append(".csv");
		FileWriter fw = new FileWriter(sb.toString());
		if (isHTML)
			fw.append(this.toHTML());
		else
			fw.append(this.toCSV());

		fw.flush();
		fw.close();
	}

	public void save(String pathToCSV, ManagedSysEntity mSys)
			throws IOException {
		this.save(pathToCSV, mSys, true);
		this.save(pathToCSV, mSys, false);
	}

	public static List<String> getHeader(List<AttributeMapEntity> attrMapList) {
		// Fill header
		List<String> hList = new ArrayList<String>(0);
		for (AttributeMapEntity map : attrMapList) {
			hList.add(map.getAttributeName());
		}
		return hList;
	}
}
