package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.lang.dto.Language;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LanguageSearchBean", propOrder = {
	"code"
})
public class LanguageSearchBean extends AbstractSearchBean<Language, String> implements SearchBean<Language, String>, Serializable {

	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LanguageSearchBean other = (LanguageSearchBean) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("LanguageSearchBean [code=%s, toString()=%s]",
				code, super.toString());
	}
	
	
}
