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

	private List<AuthorizationMatrixEntry> entries;
	
	public List<AuthorizationMatrixEntry> getEntries() {
		return entries;
	}
	
	
	
	public void setEntries(List<AuthorizationMatrixEntry> entries) {
		this.entries = entries;
	}



	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "AuthorizationMatrixEntry", propOrder = {
    	"key",
    	"values"
    })
    public static class AuthorizationMatrixEntry {
		private String key;
		private Set<String> values;
		
		public AuthorizationMatrixEntry() {} 
		
		public AuthorizationMatrixEntry(final String key, final Set<String> values) {
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
