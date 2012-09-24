package org.openiam.core.domain.reports;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "report_query")
public class ReportQuery {

  @Id
  @GeneratedValue(generator="system-uuid")
  @GenericGenerator(name="system-uuid", strategy = "uuid")
  @Column(name = "report_query_id")
  private Integer id;

  @Column(name = "report_name")
  private String reportName;

  @Column(name = "query_script_path")
  private String queryScriptPath;

  @Column
  private String params;

  @Column(name = "required_params")
  private String requiredParams;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getQueryScriptPath() {
        return queryScriptPath;
    }

    public void setQueryScriptPath(String queryScriptPath) {
        this.queryScriptPath = queryScriptPath;
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
}
