package org.openiam.idm.srvc.report.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.dto.ReportParamTypeDto;

import javax.persistence.*;

@Entity
@Table(name = "REPORT_PARAMETER_TYPE")
@DozerDTOCorrespondence(ReportParamTypeDto.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "RCPT_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "TYPE_NAME"))
})
public class ReportParamTypeEntity extends AbstractKeyNameEntity {
    
    @Column(name = "TYPE_DESCRIPTION")
    private String description;

    public ReportParamTypeEntity() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
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
		ReportParamTypeEntity other = (ReportParamTypeEntity) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReportParamTypeEntity [description=" + description + ", name="
				+ name + ", id=" + id + "]";
	}

   
}
