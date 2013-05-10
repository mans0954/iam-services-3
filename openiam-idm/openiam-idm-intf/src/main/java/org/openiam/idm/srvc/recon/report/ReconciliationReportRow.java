package org.openiam.idm.srvc.recon.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;

public class ReconciliationReportRow {
	private static final String HEADER_COLOR = "#a3a3a3";
	private static final String SUB_HEADER_COLOR = "#c3c3c3";
	private static final String CONFLICT_COLOR = "#ff4455";
	/**
	 * 
	 */

	private String htmlRow = "";
	private String csvRow = "";

	public ReconciliationReportRow(List<AttributeMapEntity> attrMapList)
			throws Exception {
		super();
		List<String> header = ReconciliationReport.getHeader(attrMapList);
		htmlRow = generateHTMLHeader(header);
		csvRow = generateCSVHeader(header);
	}

	private String generateHTMLHeader(List<String> header) throws Exception {
		StringBuilder build = new StringBuilder();

		if (CollectionUtils.isEmpty(header)) {
			throw new Exception("wrongHeader");
		}
		build.append("<tr style='background-color:" + HEADER_COLOR + "'>");
		build.append("<td rowSpan='2' align='center'>");
		build.append("Case");
		build.append("</td>");
		build.append("<td align='center' colSpan='" + header.size() + "'>");
		build.append("Fields name");
		build.append("</td>");
		build.append("</tr>");
		build.append("<tr style='background-color:" + HEADER_COLOR + "'>");
		for (String str : header) {
			build.append("<td>");
			build.append(str);
			build.append("</td>");
		}
		build.append("</tr>");
		return build.toString();
	}

	private String generateCSVHeader(String header) throws Exception {
		StringBuilder build = new StringBuilder();
		build.append("\"CASE\",");
		build.append("\"");
		build.append(header.replace(",", "\",\""));
		build.append("\"");
		build.append('\n');
		return build.toString();
	}

	private String generateCSVHeader(List<String> heads) throws Exception {
		StringBuilder header = new StringBuilder();
		for (String head : heads) {
			header.append(head);
			header.append(",");
		}
		header.deleteCharAt(header.length() - 1);
		return generateCSVHeader(header.toString());
	}

	public ReconciliationReportRow(String sepatatorText, int colSpan)
			throws Exception {
		StringBuilder build = new StringBuilder();
		build.append("<tr style='background-color:" + SUB_HEADER_COLOR + "'>");
		build.append("<td colSpan='" + colSpan + "' align='center'>");
		build.append(sepatatorText);
		build.append("</td>");
		htmlRow = build.toString();
	}

	public ReconciliationReportRow(String preffix,
			ReconciliationReportResults result, String values) throws Exception {
		super();
		htmlRow = generateHTMLRow("", preffix, result, values);
		csvRow = generateCSVRow("", preffix, result, values);
	}

	public ReconciliationReportRow(String login, String preffix,
			ReconciliationReportResults result, String values) throws Exception {
		super();
		htmlRow = generateHTMLRow(login, preffix, result, values);
		csvRow = generateCSVRow(login, preffix, result, values);
	}

	private String generateCSVRow(String login, String preffix,
			ReconciliationReportResults result, String values) throws Exception {
		StringBuilder build = new StringBuilder();
		build.append("\"");
		build.append(preffix);
		build.append(result.getValue());
		if (!StringUtils.isEmpty(login)) {
			build.append(" Login: " + login);
		}
		build.append("\"");
		build.append(',');
		build.append("\"");
		build.append(values.replace(",", "\",\""));
		build.append("\"");
		build.append('\n');
		return build.toString();
	}

	private String generateHTMLRow(String login, String preffix,
			ReconciliationReportResults result, String values) throws Exception {
		StringBuilder build = new StringBuilder();
		List<String> vals = new ArrayList<String>(Arrays.asList(values
				.split(",")));
		if (result == null || StringUtils.isEmpty(values)
				|| values.split(",").length < 1) {
			throw new Exception("wrong data");
		}
		// Check diffs
		StringBuilder td = new StringBuilder();
		for (String str : vals) {
			if (str.contains("][")) {
				result = ReconciliationReportResults.MATCH_FOUND_DIFFERENT;
				td.append("<td style='background-color:" + CONFLICT_COLOR
						+ "'>");
			} else
				td.append("<td>");
			td.append(str);
			td.append("</td>");
		}

		if (values.charAt(values.length() - 1) == ',')
			vals.add("&nbsp;");
		build.append("<tr>");
		build.append("<td style='width:200px;background-color:"
				+ result.getColor() + ";'>");
		build.append(preffix);
		build.append(result.getValue());
		if (ReconciliationReportResults.MATCH_FOUND.equals(result)
				|| ReconciliationReportResults.MATCH_FOUND_DIFFERENT
						.equals(result)
				|| ReconciliationReportResults.NOT_EXIST_IN_RESOURCE
						.equals(result)) {
			build.append("\n<b>Login: " + login + "</b>");
		}
		build.append("</td>");
		build.append(td);
		build.append("</tr>");
		return build.toString();
	}

	public String toHTML() {
		return htmlRow;
	}

	public String toCSV() {
		return csvRow;
	}
}
