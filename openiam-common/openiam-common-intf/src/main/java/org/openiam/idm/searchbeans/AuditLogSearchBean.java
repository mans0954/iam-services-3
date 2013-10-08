package org.openiam.idm.searchbeans;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.continfo.dto.Address;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuditLogSearchBean", propOrder = {
	"from",
	"to"
})
public class AuditLogSearchBean extends AbstractSearchBean<IdmAuditLog, String> implements SearchBean<IdmAuditLog, String> {

	private Date from;
	private Date to;
	
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
	
	
}
