package org.openiam.idm.searchbeans;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
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
    "userVsTargetAndFlag"
})
public class AuditLogSearchBean extends AbstractSearchBean<IdmAuditLogEntity, String> implements SearchBean<IdmAuditLogEntity, String> {

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


    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(userId != null ? userId : "")
                .append(from != null ? from.hashCode() : "")
                .append(to != null ? to.hashCode() : "")
                .append(action != null ? action : "")
                .append(result != null ? result : "")
                .append(managedSysId != null ? managedSysId : "")
                .append(source != null ? source : "")
                .append(targetId != null ? targetId : "")
                .append(targetType != null ? targetType : "")
                .append(parentId != null ? parentId : "")
                .append(secondaryTargetId != null ? secondaryTargetId : "")
                .append(secondaryTargetType != null ? secondaryTargetType : "")
                .append(userVsTargetAndFlag != null ? userVsTargetAndFlag : "")
                .append(getKey() != null ? getKey() : "")
                .toString();    }
}
