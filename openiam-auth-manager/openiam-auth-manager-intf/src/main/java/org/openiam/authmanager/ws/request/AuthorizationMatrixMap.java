package org.openiam.authmanager.ws.request;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationMatrixMap", propOrder = {
	"entries"
})
public class AuthorizationMatrixMap {

private List<AuthorizationMatrixMapEntry> entries;
	
	public List<AuthorizationMatrixMapEntry> getEntries() {
		return entries;
	}
	
	public void setEntries(List<AuthorizationMatrixMapEntry> entries) {
		this.entries = entries;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "AuthorizationMatrixMapEntry", propOrder = {
    	"key",
    	"values"
    })
    public static class AuthorizationMatrixMapEntry {
		private String key;
		private Set<String> values;
		
		public AuthorizationMatrixMapEntry() {} 
		
		public AuthorizationMatrixMapEntry(final String key, final Set<String> values) {
			this.key = key;
			this.values = values;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Set<String> getValues() {
			return values;
		}

		public void setValues(Set<String> values) {
			this.values = values;
		}
		
		
	}
}
