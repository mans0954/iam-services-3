package org.openiam.idm.srvc.policy.dto;

import org.apache.commons.lang.StringUtils;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * <code>Policy</code> represents a policy object that is used by the policy service.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Policy", propOrder = {"userId", "contentProviderId"})
@DozerDTOCorrespondence(PolicyEntity.class)
public class PasswordPolicyAssocSearchBean implements java.io.Serializable {

    private static final long serialVersionUID = 5733143745301294956L;
    private String userId;
    private String contentProviderId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

	public String getContentProviderId() {
		return contentProviderId;
	}

	public void setContentProviderId(String contentProviderId) {
		this.contentProviderId = contentProviderId;
	}

    
}
