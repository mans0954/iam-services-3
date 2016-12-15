package org.openiam.idm.srvc.report.dto;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportSubCriteriaParamDto", propOrder = {
		"rscpId",
        "reportId",
        "name",
        "value",
        "type"
        
})
@DozerDTOCorrespondence(ReportSubCriteriaParamEntity.class)
public class ReportSubCriteriaParamDto extends KeyDTO {

	private String rscpId;
    private String reportId;
    private String name;
    private String value;
    private String type;
    

    public ReportSubCriteriaParamDto() {
    }

    public ReportSubCriteriaParamDto(String id, String type,String reportId, String name, String value) {
    	this.setId(id);
    	this.type=type;
        this.reportId = reportId;
        this.name = name;
        this.value = value;
        
    }
    
    

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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
    
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

   

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportSubCriteriaParamDto that = (ReportSubCriteriaParamDto) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (reportId != null ? !reportId.equals(that.reportId) : that.reportId != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return !(rscpId != null ? !rscpId.equals(that.rscpId) : that.rscpId != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (reportId != null ? reportId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (rscpId != null ? rscpId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReportCriteriaParamDto{" +
                "id='" + id + '\'' +
                ", reportId='" + reportId + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\''  +  ", type='" + type + '\''  +  ", rscpId='" + rscpId + '\''  +
                '}';
    }

	public String getRscpId() {
		return rscpId;
	}

	public void setRscpId(String rscpId) {
		this.rscpId = rscpId;
	}

	
}
