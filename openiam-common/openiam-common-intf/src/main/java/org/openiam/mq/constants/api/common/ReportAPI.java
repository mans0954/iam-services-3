package org.openiam.mq.constants.api.common;

import org.openiam.mq.constants.api.OpenIAMAPI;

/**
 * Created by aduckardt on 2016-12-09.
 */
public enum ReportAPI implements OpenIAMAPI {
    GetReportUrl, GetReports, GetReportCount, Save, Validate, SaveReportParam, GetReportParameters, GetReport, DeleteReportParam, DeleteReport, GetReportParameterTypes, GetReportParameterMetaTypes, GetAllReportCriteriaParam, GetSubCriteriaParam, DeleteSubCriteriaParam, GetReportsByUserId, GetReportParametersByReportId, GetReportParametersByReportName, GetReportByName, CountSubCriteriaParam, SaveSubCriteriaParam, ExecuteQueue

}
