package org.openiam.idm.searchbeans;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by aduckardt on 2016-12-14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReconConfigSearchBean", propOrder = {
        "reportName"
})
public class ReportSearchBean extends AbstractSearchBean<ReportInfoDto, String> {
    private String reportName;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getKey(){
        if(CollectionUtils.isNotEmpty(this.getKeySet())){
            return this.getKeySet().iterator().next();
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ReportSearchBean{");
        sb.append(super.toString());
        sb.append(",                 reportName='").append(reportName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
