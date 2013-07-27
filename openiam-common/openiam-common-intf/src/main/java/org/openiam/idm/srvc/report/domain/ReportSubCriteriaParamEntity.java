package org.openiam.idm.srvc.report.domain;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;

import javax.persistence.*;


@Entity
@Table(name = "REPORT_SUB_CRITERIA_PARAM")
@DozerDTOCorrespondence(ReportSubCriteriaParamDto.class)
public class ReportSubCriteriaParamEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "RCP_ID")
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_SUB_ID", referencedColumnName = "REPORT_SUB_ID", insertable = true, updatable = false) 
    private ReportSubscriptionEntity report;

    @Column(name = "PARAM_NAME")
    private String name;

    @Column(name = "PARAM_VALUE")
    private String value;

    public ReportSubCriteriaParamEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReportSubscriptionEntity getReport() {
        return report;
    }

    public void setReport(ReportSubscriptionEntity report) {
        this.report = report;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportSubCriteriaParamEntity that = (ReportSubCriteriaParamEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (report != null ? !report.equals(that.report) : that.report != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (report != null ? report.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReportCriteriaParamEntity{" +
                "id='" + id + '\'' +
                ", report=" + report +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
