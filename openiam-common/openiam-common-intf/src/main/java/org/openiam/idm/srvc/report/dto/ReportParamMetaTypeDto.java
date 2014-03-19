package org.openiam.idm.srvc.report.dto;

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
        "id",
        "name",
        "multipleSelect"
})
@DozerDTOCorrespondence(ReportParamMetaTypeEntity.class)
public class ReportParamMetaTypeDto {

    private String id;
    private String name;
    private Boolean multipleSelect;


    public ReportParamMetaTypeDto() {
    }

    public ReportParamMetaTypeDto(String id, String name, Boolean multipleSelect) {
        this.id = id;
        this.name = name;
        this.multipleSelect = multipleSelect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMultipleSelect() {
        return multipleSelect;
    }

    public void setMultipleSelect(Boolean multipleSelect) {
        this.multipleSelect = multipleSelect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportParamMetaTypeDto that = (ReportParamMetaTypeDto) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (multipleSelect != null ? !multipleSelect.equals(that.multipleSelect) : that.multipleSelect != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (multipleSelect != null ? multipleSelect.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return "ReportCriteriaParamDto [id=" + id + ", name=" + name
                + ", multipleSelect=" + multipleSelect + "]";
	}

}
