package org.openiam.idm.srvc.report.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.dto.ReportParamMetaTypeDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "REPORT_PARAMETER_METATYPE")
@DozerDTOCorrespondence(ReportParamMetaTypeDto.class)
public class ReportParamMetaTypeEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "PARAM_METATYPE_ID")
    private String id;

    @Column(name = "PARAM_METATYPE_NAME")
    private String name;

    @Column(name = "MULTIPLE_SELECT")
    @Type(type = "yes_no")
    private boolean multipleSelect;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getMultipleSelect() {
        return multipleSelect;
    }

    public void setMultipleSelect(boolean multiple) {
        multipleSelect = multiple;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportParamMetaTypeEntity that = (ReportParamMetaTypeEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (multipleSelect != that.multipleSelect) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (multipleSelect ? 1231 : 1237);
        return result;
    }

    @Override
    public String toString() {
        return "ReportParamMetaTypeEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", multipleSelect=" + multipleSelect +
                '}';
    }

}
