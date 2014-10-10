package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthResourceAMAttribute", propOrder = {
        "reflectionKey",
        "metaValues"
})
@DozerDTOCorrespondence(AuthResourceAMAttributeEntity.class)
public class AuthResourceAMAttribute extends KeyNameDTO {
    private String reflectionKey;
    private Set<URIPatternMetaValue> metaValues;

    public String getReflectionKey() {
        return reflectionKey;
    }

    public void setReflectionKey(String reflectionKey) {
        this.reflectionKey = reflectionKey;
    }

    public Set<URIPatternMetaValue> getMetaValues() {
		return metaValues;
	}

	public void setMetaValues(Set<URIPatternMetaValue> metaValues) {
		this.metaValues = metaValues;
	}

}
