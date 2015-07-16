package org.openiam.authmanager.ws.request;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationMatrixMapToSet", propOrder = {
	"entries"
})
public class AuthorizationMatrixMapToSet {

	private List<AuthorizationMatrixMapToSetEntry> entries;
	
	public List<AuthorizationMatrixMapToSetEntry> getEntries() {
		return entries;
	}
	
	
	
	public void setEntries(List<AuthorizationMatrixMapToSetEntry> entries) {
		this.entries = entries;
	}



	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "AuthorizationMatrixMapToSetEntry", propOrder = {
    	"key",
    	"values"
    })
    public static class AuthorizationMatrixMapToSetEntry {
		private String key;
		
		@XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
		private Map<String, Set<String>> values;
		
		public AuthorizationMatrixMapToSetEntry() {} 
		
		public AuthorizationMatrixMapToSetEntry(final String key, final Map<String, Set<String>> values) {
			this.key = key;
			this.values = values;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Map<String, Set<String>> getValues() {
			return values;
		}

		public void setValues(Map<String, Set<String>> values) {
			this.values = values;
		}
		
		
	}
}
