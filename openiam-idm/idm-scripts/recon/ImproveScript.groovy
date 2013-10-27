package org.openiam.idm.srvc.csv;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.org.dto.Organization;

public class ImproveScript implements
		org.openiam.idm.srvc.recon.service.CSVImproveScript {
	public int execute(String path, List<Organization> orgList) {
		Map<String, String> orgMap = this.ogrListToMap(orgList);
		// FIELD NAME TO FIX
		String formattedName = "FORMATTED_NM";
		// TRY TO OPEN FILE
		File file = new File(path);
		// IF FILE NOT EXIST - VERY BAD. RETURN -1
		if (!file.exists()) {
			return -1;
		}
		// PARSE FILE AS CSV
		org.apache.commons.csv.CSVParser parser;
		String[][] fromParse = null;
		try {
			parser = new org.apache.commons.csv.CSVParser(new FileReader(file));
			fromParse = parser.getAllValues();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// CSV ROWS LIST. EXAMPLE : {1,4,5,6,6\n}
		List<String> rows = new ArrayList<String>();
		// NUMBER OF COLUMS FOR FIXING
		int colEmployee = 0;
		int col = 0;

		// FLAG TO MARK "IS formattedName FIELD EXIST IN CSV FILE"
		boolean isFind = false;
		// Check is already fixed CSV?
		int fields = 0;
		for (String names : fromParse[0]) {
			if (names.equals(formattedName)
					|| names.equals(formattedName + "_2")
					|| names.equals(formattedName + "_3")) {
				fields++;
			}
			if (fields == 3)
				return 0;
		}
		// END CHECK-------------------------------------------------------

		// Fix header
		StringBuilder nameStr = new StringBuilder();
		int deptColumns = 0;
		boolean deptIsFinded = false;
		for (String names : fromParse[0]) {
			if ("EMPLOYEE_ID".equals(names)) {
				colEmployee++;
			}

			if ("HOME_DEPT_CD".equals(names.trim())) {
				deptIsFinded = true;
			}

			nameStr.append(names);
			nameStr.append(',');
			if (names.equals(formattedName)) {
				nameStr.append(names + "_2");
				nameStr.append(',');
				nameStr.append(names + "_3");
				nameStr.append(',');
				isFind = true;
			}
			if (!isFind)
				col++;

			if (!deptIsFinded)
				deptColumns++;
		}
		nameStr.deleteCharAt(nameStr.length() - 1);
		nameStr.append('\n');
		rows.add(nameStr.toString());
		for (int i = 1; i < fromParse.length; i++) {
			nameStr = new StringBuilder();
			for (int j = 0; j < fromParse[i].length; j++) {
				if (j == colEmployee) {
					nameStr.append(fromParse[i][j].replaceFirst("^0*", ""));
					nameStr.append(',');
					continue;
				}

				if (j == deptColumns) {
					String newVal = orgMap.get(fromParse[i][j].replaceFirst("^0*", ""));
					if (newVal == null) {
						newVal = "";
					}
					nameStr.append(newVal);
					nameStr.append(',');
					continue;
				}
				if (j == col) {
					String[] names = fromParse[i][j].split(",");
					if (names.length == 2) {
						nameStr.append(names[0].trim());
						nameStr.append(',');
						String[] sec_name = names[1].trim().split(" ");
						if (sec_name.length == 2) {
							nameStr.append(sec_name[0].trim());
							nameStr.append(',');
							nameStr.append(sec_name[1].trim());
						} else {
							nameStr.append(names[1].trim());
							nameStr.append(',');
						}
					} else {
						nameStr.append(fromParse[i][j]);
						nameStr.append(',');
					}
					nameStr.append(',');
				} else {
					nameStr.append(fromParse[i][j].replace(",", " "));
					nameStr.append(',');
				}
			}
			nameStr.deleteCharAt(nameStr.length() - 1);
			nameStr.append('\n');
			rows.add(nameStr.toString());
		}

		// OVERWRITE FILE
		StringBuilder fileStr = new StringBuilder();
		for (String row : rows) {
			fileStr.append(row);
		}
		FileWriter fw;
		try {
			fw = new FileWriter(path);
			fw.write(fileStr.toString());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ---------------------------------
		return 0;
	}

	private Map<String, String> ogrListToMap(List<Organization> orgList) {
		Map<String, String> result = new HashMap<String, String>();
		if (CollectionUtils.isEmpty(orgList)) {
			return result;
		}
		for (Organization org : orgList) {
			result.put(org.getInternalOrgId(), org.getInternalOrgName());
		}
		return result;
	}
}
