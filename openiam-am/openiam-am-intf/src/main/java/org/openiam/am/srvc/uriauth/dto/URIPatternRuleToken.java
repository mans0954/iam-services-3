package org.openiam.am.srvc.uriauth.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.dto.AbstractMeta;
import org.openiam.am.srvc.dto.URIPatternMetaType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternRuleToken", propOrder = {
	"metaType",
	"valueList",
	"contentType",
	"cacheable",
	"cacheTTL"
})
public class URIPatternRuleToken implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private URIPatternMetaType metaType;
	private String contentType;
	private List<URIPatternRuleValue> valueList;
	private boolean cacheable;
	private Integer cacheTTL;
	
	private URIPatternRuleToken() {}

	public URIPatternRuleToken(final URIPatternMetaType metaType, final AbstractMeta meta) {
		this();
		this.metaType = metaType;
		this.contentType = meta.getContentType();
	}
	
	public void addValue(final String key, final String value, final boolean propagate, final boolean propagateOnError, final boolean fetchedValue) {
		if(key != null && value != null) {
			if(valueList == null) {
				valueList = new LinkedList<URIPatternRuleValue>();
			}
			valueList.add(new URIPatternRuleValue(key, value, propagate, propagateOnError, fetchedValue));
		}
	}

	public URIPatternMetaType getMetaType() {
		return metaType;
	}

	public List<URIPatternRuleValue> getValueList() {
		return valueList;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}

	public Integer getCacheTTL() {
		return cacheTTL;
	}

	public void setCacheTTL(Integer cacheTTL) {
		this.cacheTTL = cacheTTL;
	}

	@Override
	public String toString() {
		return "URIPatternRuleToken [metaType=" + metaType + ", contentType="
				+ contentType + ", valueList=" + valueList + "]";
	}

	
}
