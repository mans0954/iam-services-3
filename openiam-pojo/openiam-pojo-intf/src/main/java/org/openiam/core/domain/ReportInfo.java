package org.openiam.core.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "report_info")
public class ReportInfo {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "report_info_id")
    private String id;

    @Column(name = "report_name")
    private String reportName;

    @Column(name = "groovy_script_path")
    private String groovyScriptPath;

    @Column(name = "report_file_path")
    private String reportFilePath;

    @Column
    private String params;

    @Column(name = "required_params")
    private String requiredParams;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getGroovyScriptPath() {
        return groovyScriptPath;
    }

    public void setGroovyScriptPath(String groovyScriptPath) {
        this.groovyScriptPath = groovyScriptPath;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getRequiredParams() {
        return requiredParams;
    }

    public void setRequiredParams(String requiredParams) {
        this.requiredParams = requiredParams;
    }

    public List<String> getRequiredParamsList() {
        return Arrays.asList(this.requiredParams.split(","));
    }

    public List<String> getParamsList() {
        return Arrays.asList(this.params.split(","));
    }

    public String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }
}