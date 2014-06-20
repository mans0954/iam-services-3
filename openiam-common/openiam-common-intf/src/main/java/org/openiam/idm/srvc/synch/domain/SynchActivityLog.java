package org.openiam.idm.srvc.synch.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SYNCH_ACTIVITY_LOG")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SynchActivityLog implements java.io.Serializable {

	private static final long serialVersionUID = -6485653279902427825L;
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="ACTIVITY_LOG_ID", length=32, nullable = false)
	private String activityLogId;
    @Column(name="LOG_TYPE",length=100, nullable = false)
	private String logType;
    @Column(name="OBJECT_ID", length=32, nullable = false)
	private String objectId;
    @Column(name="LOG_START_TIME",length =10)
    @Temporal(TemporalType.DATE)
	private Date logStartTime;
    @Column(name="STATUS",length=20)
	private String status;

	public SynchActivityLog() {
	}

	public SynchActivityLog(String activityLogId, String logType,
			String objectId) {
		this.activityLogId = activityLogId;
		this.logType = logType;
		this.objectId = objectId;
	}

	public SynchActivityLog(String activityLogId, String logType,
			String objectId, Date logStartTime, String status) {
		this.activityLogId = activityLogId;
		this.logType = logType;
		this.objectId = objectId;
		this.logStartTime = logStartTime;
		this.status = status;
	}

	public String getActivityLogId() {
		return this.activityLogId;
	}

	public void setActivityLogId(String activityLogId) {
		this.activityLogId = activityLogId;
	}

	public String getLogType() {
		return this.logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Date getLogStartTime() {
		return this.logStartTime;
	}

	public void setLogStartTime(Date logStartTime) {
		this.logStartTime = logStartTime;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
