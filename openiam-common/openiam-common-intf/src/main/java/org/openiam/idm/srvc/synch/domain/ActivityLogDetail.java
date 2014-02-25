package org.openiam.idm.srvc.synch.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "ACTIVITY_LOG_DETAIL")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ActivityLogDetail implements java.io.Serializable {
	private static final long serialVersionUID = -8378687671721851836L;
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="ACTIVITY_LOG_DETAIL_ID", length=32, nullable = false)
	private String activityLogDetailId;
    @Column(name="ACTIVITY_LOG_ID", length=32, nullable = false)
	private String activityLogId;
    @Column(name="MSG",length=100)
	private String msg;
    @Column(name="STATUS",length=20)
	private String status;
    @Lob
    @Column(name="DATA_ROW")
	private String dataRow;

	public ActivityLogDetail() {
	}

	public ActivityLogDetail(String activityLogDetailId, String activityLogId) {
		this.activityLogDetailId = activityLogDetailId;
		this.activityLogId = activityLogId;
	}

	public ActivityLogDetail(String activityLogDetailId, String activityLogId,
			String msg, String status, String dataRow) {
		this.activityLogDetailId = activityLogDetailId;
		this.activityLogId = activityLogId;
		this.msg = msg;
		this.status = status;
		this.dataRow = dataRow;
	}

	public String getActivityLogDetailId() {
		return this.activityLogDetailId;
	}

	public void setActivityLogDetailId(String activityLogDetailId) {
		this.activityLogDetailId = activityLogDetailId;
	}

	public String getActivityLogId() {
		return this.activityLogId;
	}

	public void setActivityLogId(String activityLogId) {
		this.activityLogId = activityLogId;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDataRow() {
		return this.dataRow;
	}

	public void setDataRow(String dataRow) {
		this.dataRow = dataRow;
	}

}
