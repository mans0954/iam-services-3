package org.openiam.idm.searchbeans;

import org.openiam.base.BaseIdentity;
import org.openiam.idm.srvc.meta.dto.MetadataType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractLanguageSearchBean", propOrder = {
        "languageId",
        "referenceType"
})
public abstract class AbstractLanguageSearchBean<T, KeyType> extends AbstractKeyNameSearchBean<MetadataType, String> {

    protected String languageId;
    protected String referenceType;

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((languageId == null) ? 0 : languageId.hashCode());
		result = prime * result
				+ ((referenceType == null) ? 0 : referenceType.hashCode());
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
		AbstractLanguageSearchBean other = (AbstractLanguageSearchBean) obj;
		if (languageId == null) {
			if (other.languageId != null)
				return false;
		} else if (!languageId.equals(other.languageId))
			return false;
		if (referenceType == null) {
			if (other.referenceType != null)
				return false;
		} else if (!referenceType.equals(other.referenceType))
			return false;
		return true;
	}


}
