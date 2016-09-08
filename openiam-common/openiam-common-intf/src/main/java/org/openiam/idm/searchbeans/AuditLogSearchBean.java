package org.openiam.idm.searchbeans;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.Tuple;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuditLogSearchBean", propOrder = {
	"from",
	"to",
    "action",
    "actions",
    "managedSysId",
    "source",
    "userId",
    "targetId",
    "targetType",
    "parentId",
    "result",
    "secondaryTargetId",
    "secondaryTargetType",
    "authProviderId",
    "contentProviderId",
    "uriPatternId",
    "userVsTargetAndFlag",
    "attributes"
})
public class AuditLogSearchBean extends AbstractSearchBean<IdmAuditLogEntity, String> {

	private String authProviderId;
    private String contentProviderId;
    private String uriPatternId;
	private String userId;
	private Date from;
	private Date to;
    private String action;
	private String[] actions;
    private String result;
    private String managedSysId;
    private String source;
    private String targetId;
    private String targetType;
    private String parentId;
    private String secondaryTargetId;
    private String secondaryTargetType;
    private Boolean userVsTargetAndFlag;
    private List<Tuple<String, String>> attributes;

    public AuditLogSearchBean() {
        userVsTargetAndFlag = false;
    }

    public void setParentOnly(){
        parentId = "null";
    }

    public boolean isParentOnly(){
        return StringUtils.isNotEmpty(parentId) && parentId.equals("null");
    }

	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String[] getActions() {
        return actions;
    }

    public void setActions(String[] actions) {
        this.actions = actions;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getSecondaryTargetId() {
        return secondaryTargetId;
    }

    public void setSecondaryTargetId(String secondaryTargetId) {
        this.secondaryTargetId = secondaryTargetId;
    }

    public String getSecondaryTargetType() {
        return secondaryTargetType;
    }

    public void setSecondaryTargetType(String secondaryTargetType) {
        this.secondaryTargetType = secondaryTargetType;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

	public String getAuthProviderId() {
		return authProviderId;
	}

	public void setAuthProviderId(String authProviderId) {
		this.authProviderId = authProviderId;
	}

	public String getContentProviderId() {
		return contentProviderId;
	}

	public void setContentProviderId(String contentProviderId) {
		this.contentProviderId = contentProviderId;
	}

	public String getUriPatternId() {
		return uriPatternId;
	}

	public void setUriPatternId(String uriPatternId) {
		this.uriPatternId = uriPatternId;
	}

    public Boolean getUserVsTargetAndFlag() {
        return userVsTargetAndFlag;
    }

    public void setUserVsTargetAndFlag(Boolean userVsTargetAndFlag) {
        this.userVsTargetAndFlag = userVsTargetAndFlag;
    }

	public List<Tuple<String, String>> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Tuple<String, String>> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttribute(final String key, final String value) {
		if(this.attributes == null) {
			this.attributes = new LinkedList<Tuple<String,String>>();
		}
		if(StringUtils.isNotBlank(key)) {
			final Tuple<String, String> tuple = new Tuple<String, String>();
			tuple.setKey(key);
			if(StringUtils.isNotBlank(value)) {
				tuple.setValue(value);
			}
			this.attributes.add(tuple);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + Arrays.hashCode(actions);
		result = prime * result
				+ ((authProviderId == null) ? 0 : authProviderId.hashCode());
		result = prime
				* result
				+ ((contentProviderId == null) ? 0 : contentProviderId
						.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		result = prime
				* result
				+ ((secondaryTargetId == null) ? 0 : secondaryTargetId
						.hashCode());
		result = prime
				* result
				+ ((secondaryTargetType == null) ? 0 : secondaryTargetType
						.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result
				+ ((targetId == null) ? 0 : targetId.hashCode());
		result = prime * result
				+ ((targetType == null) ? 0 : targetType.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result
				+ ((uriPatternId == null) ? 0 : uriPatternId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime
				* result
				+ ((userVsTargetAndFlag == null) ? 0 : userVsTargetAndFlag
						.hashCode());
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
		AuditLogSearchBean other = (AuditLogSearchBean) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (!Arrays.equals(actions, other.actions))
			return false;
		if (authProviderId == null) {
			if (other.authProviderId != null)
				return false;
		} else if (!authProviderId.equals(other.authProviderId))
			return false;
		if (contentProviderId == null) {
			if (other.contentProviderId != null)
				return false;
		} else if (!contentProviderId.equals(other.contentProviderId))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (secondaryTargetId == null) {
			if (other.secondaryTargetId != null)
				return false;
		} else if (!secondaryTargetId.equals(other.secondaryTargetId))
			return false;
		if (secondaryTargetType == null) {
			if (other.secondaryTargetType != null)
				return false;
		} else if (!secondaryTargetType.equals(other.secondaryTargetType))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (targetId == null) {
			if (other.targetId != null)
				return false;
		} else if (!targetId.equals(other.targetId))
			return false;
		if (targetType == null) {
			if (other.targetType != null)
				return false;
		} else if (!targetType.equals(other.targetType))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		if (uriPatternId == null) {
			if (other.uriPatternId != null)
				return false;
		} else if (!uriPatternId.equals(other.uriPatternId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (userVsTargetAndFlag == null) {
			if (other.userVsTargetAndFlag != null)
				return false;
		} else if (!userVsTargetAndFlag.equals(other.userVsTargetAndFlag))
			return false;
		return true;
	}

    
}
