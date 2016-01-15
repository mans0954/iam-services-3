package org.openiam.idm.searchbeans;

import org.openiam.base.BaseIdentity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractLanguageSearchBean", propOrder = {
        "languageId",
        "referenceType"
})
public abstract class AbstractLanguageSearchBean<T, KeyType> extends AbstractSearchBean<T, KeyType> {

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AbstractLanguageSearchBean<?, ?> that = (AbstractLanguageSearchBean<?, ?>) o;

        if (languageId != null ? !languageId.equals(that.languageId) : that.languageId != null) return false;
        return !(referenceType != null ? !referenceType.equals(that.referenceType) : that.referenceType != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (languageId != null ? languageId.hashCode() : 0);
        result = 31 * result + (referenceType != null ? referenceType.hashCode() : 0);
        return result;
    }
}
