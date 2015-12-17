package org.openiam.idm.srvc.report.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.dto.ReportParamMetaTypeDto;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "REPORT_PARAMETER_METATYPE")
@DozerDTOCorrespondence(ReportParamMetaTypeDto.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "PARAM_METATYPE_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "PARAM_METATYPE_NAME"))
})
public class ReportParamMetaTypeEntity extends AbstractKeyNameEntity {

    @Column(name = "IS_MULTIPLE")
    @Type(type = "yes_no")
    private boolean isMultiple;

    public boolean getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(boolean isMultiple) {
        this.isMultiple = isMultiple;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isMultiple ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportParamMetaTypeEntity other = (ReportParamMetaTypeEntity) obj;
		if (isMultiple != other.isMultiple)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReportParamMetaTypeEntity [isMultiple=" + isMultiple + "]";
	}

    
}
