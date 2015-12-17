package org.openiam.idm.srvc.report.dto;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.domain.ReportCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportParamMetaTypeEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * This DTO used in reporting system to transferring parameters of criteria for DataSetBuilder via WS
 *
 * @author vitaly.yakunin
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportParamMetaTypeDto", propOrder = {
        "isMultiple"
})
@DozerDTOCorrespondence(ReportParamMetaTypeEntity.class)
public class ReportParamMetaTypeDto extends KeyNameDTO {

    private Boolean isMultiple;


    public ReportParamMetaTypeDto() {
    }

    public ReportParamMetaTypeDto(String id, String name, Boolean isMultiple) {
        setId(id);
        setName(name);
        this.isMultiple = isMultiple;
    }

    public Boolean getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(Boolean isMultiple) {
        this.isMultiple = isMultiple;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((isMultiple == null) ? 0 : isMultiple.hashCode());
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
		ReportParamMetaTypeDto other = (ReportParamMetaTypeDto) obj;
		if (isMultiple == null) {
			if (other.isMultiple != null)
				return false;
		} else if (!isMultiple.equals(other.isMultiple))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReportParamMetaTypeDto [isMultiple=" + isMultiple + ", name_="
				+ name_ + ", id=" + id + ", objectState=" + objectState
				+ ", requestorSessionID=" + requestorSessionID
				+ ", requestorUserId=" + requestorUserId + ", requestorLogin="
				+ requestorLogin + ", requestClientIP=" + requestClientIP + "]";
	}

    
}
