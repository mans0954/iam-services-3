package reports

import org.openiam.idm.searchbeans.AuditLogSearchBean
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity
import org.openiam.idm.srvc.audit.service.AuditLogService

import org.openiam.idm.srvc.report.service.ReportDataSetBuilder
import org.openiam.idm.srvc.report.dto.ReportDataDto

import java.text.SimpleDateFormat
import org.openiam.idm.srvc.report.dto.ReportTable
import org.openiam.idm.srvc.report.dto.ReportRow
import org.openiam.idm.srvc.report.dto.ReportRow.ReportColumn

class AuditReport implements ReportDataSetBuilder {
    private org.springframework.context.ApplicationContext context

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws org.springframework.beans.BeansException {
        context = applicationContext;
    }

    @Override
    ReportDataDto getReportData(Map<String, String> reportParams) {
        def String action;
        def String startDate = reportParams.get("ACTION_DATETIME_START");
        def String endDate = reportParams.get("ACTION_DATETIME_END");

        def SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date eDate;
        java.util.Date sDate;

        // get the report parameters
        if (reportParams != null) {
            action = reportParams.get("ACTION_ID");
            sDate = dateFormat.parse(startDate);
            eDate = dateFormat.parse(endDate);
        }

        AuditLogService auditLogService = context.getBean(AuditLogService.class);

        AuditLogSearchBean logSearchBean = new AuditLogSearchBean();
        if (startDate != null) {
            logSearchBean.setFrom(sDate);
        }
        if (endDate != null) {
            logSearchBean.setTo(eDate);
        }
        if (action != null) {
            logSearchBean.setAction(action);
        }
        int count = auditLogService.count(logSearchBean);
        List<IdmAuditLogEntity> auditLogEntity = auditLogService.findBeans(logSearchBean, 0, count > 1000 ? 1000 : count);


        ReportTable reportTable = new ReportTable();
        reportTable.setName("AuditReportTable1");

        for(IdmAuditLogEntity a : auditLogEntity) {
            addRow(a, reportTable, "")
            for(IdmAuditLogEntity ch : a.getChildLogs()) {
                addRow(ch, reportTable, a.getAction());
            }
        }

        ReportDataDto reportDataDto = new ReportDataDto();
        List<ReportTable> reportTables = new ArrayList<ReportTable>();
        reportTables.add(reportTable);
        reportDataDto.setTables(reportTables);
        return reportDataDto;
    }

    private void addRow(IdmAuditLogEntity a, ReportTable reportTable, String parentActionId) {
        ReportRow row = new ReportRow();
        row.getColumn().add(new ReportColumn('LOG_ID', a.getId()));
        row.getColumn().add(new ReportColumn('OBJECT_TYPE_ID', ""));
        row.getColumn().add(new ReportColumn('OBJECT_ID', ""));
        row.getColumn().add(new ReportColumn('ACTION_ID', a.getAction()));
        row.getColumn().add(new ReportColumn('PARENT_ACTION_ID', parentActionId));
        row.getColumn().add(new ReportColumn('ACTION_STATUS', a.getResult()));
        row.getColumn().add(new ReportColumn('REASON', ""));
        row.getColumn().add(new ReportColumn('REASON_DETAIL', ""));
        row.getColumn().add(new ReportColumn('ACTION_DATETIME', a.getTimestamp().toString()));
        row.getColumn().add(new ReportColumn('OBJECT_NAME', ""));
        row.getColumn().add(new ReportColumn('RESOURCE_NAME', ""));
        row.getColumn().add(new ReportColumn('USER_ID', a.getUserId()));
        row.getColumn().add(new ReportColumn('SERVICE_ID', ""));
        row.getColumn().add(new ReportColumn('LOGIN_ID', a.getPrincipal()));
        row.getColumn().add(new ReportColumn('HOST', a.getNodeIP()));
        row.getColumn().add(new ReportColumn('CLIENT_ID', a.getClientIP()));
        row.getColumn().add(new ReportColumn('REQ_URL', ""));
        row.getColumn().add(new ReportColumn('LINKED_LOG_ID', ""));
        row.getColumn().add(new ReportColumn('LINK_SEQUENCE', ""));
        row.getColumn().add(new ReportColumn('ORIG_OBJECT_STATE', ""));
        row.getColumn().add(new ReportColumn('NEW_OBJECT_STATE', ""));
        row.getColumn().add(new ReportColumn('SRC_SYSTEM_ID', ""));
        row.getColumn().add(new ReportColumn('TARGET_SYSTEM_ID', a.getManagedSysId()));
        row.getColumn().add(new ReportColumn('REQUEST_ID', ""));
        row.getColumn().add(new ReportColumn('SESSION_ID', a.getSessionID()));
        row.getColumn().add(new ReportColumn('CUSTOM_ATTRNAME1', ""));
        row.getColumn().add(new ReportColumn('CUSTOM_ATTRNAME2', ""));
        row.getColumn().add(new ReportColumn('FIRST_NAME', ""));
        row.getColumn().add(new ReportColumn('MIDDLE_INIT', ""));
        row.getColumn().add(new ReportColumn('LAST_NAME', ""));
        row.getColumn().add(new ReportColumn('DEPT_CD', ""));
        row.getColumn().add(new ReportColumn('DEPT_NAME', ""));
        //a.getCustomRecords()
        reportTable.getRow().add(row);
    }

}
