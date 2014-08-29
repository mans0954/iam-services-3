package org.openiam.am.srvc.uriauth.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.dto.URIPatternMetaType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternRuleToken", propOrder = {
	"metaType",
	"valueList"
})
public class URIPatternRuleToken implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private URIPatternMetaType metaType;
	private List<URIPatternRuleValue> valueList;
	
	private URIPatternRuleToken() {}

	public URIPatternRuleToken(final URIPatternMetaType metaType) {
		this();
		this.metaType = metaType;
	}
	
	public void addValue(final String key, final String value, final boolean propagate) {
		if(key != null && value != null) {
			if(valueList == null) {
				valueList = new LinkedList<URIPatternRuleValue>();
			}
			valueList.add(new URIPatternRuleValue(key, value, propagate));
		}
	}

	public URIPatternMetaType getMetaType() {
		return metaType;
	}

	public List<URIPatternRuleValue> getValueList() {
		return valueList;
	}

	@Override
	public String toString() {
		return String.format("URIPatternRuleToken [metaType=%s, valueList=%s]",
				metaType, valueList);
	}
	
	
}
