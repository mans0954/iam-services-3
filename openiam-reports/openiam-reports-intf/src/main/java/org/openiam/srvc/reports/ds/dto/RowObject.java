package org.openiam.srvc.reports.ds.dto;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.openiam.base.ws.PropertyMapAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RowObject", propOrder = {
        "columns"
})
public class RowObject {

    @XmlJavaTypeAdapter(PropertyMapAdapter.class)
	private Map<String, String> columns = new HashMap();

    	/**
	 * Add a parameter to the context
	 * @param columnName
	 * @param columnValue
	 */
	public void addColumnValue(String columnName, String columnValue) {
		columns.put(columnName, columnValue);
	}
	/**
	 * Retrieve a parameter from the context
	 * @param columnName
	 * @return column value
	 */
	public String getColumnValue(String columnName) {
		return (columns.get(columnName));
	}

    public Map<String, String> getColumns() {
		return new HashMap<String, String>(columns);
	}

	public void setColumns(final Map<String, String> columns) {
		this.columns = new HashMap<String, String>(columns);
	}

}

