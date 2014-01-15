package reports

import org.openiam.base.OrderConstants
import org.openiam.idm.searchbeans.AuditLogSearchBean
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity
import org.openiam.idm.srvc.audit.service.AuditLogService

import org.openiam.idm.srvc.report.service.ReportDataSetBuilder
import org.openiam.idm.srvc.report.dto.ReportDataDto

import java.text.DateFormat
import java.text.SimpleDateFormat
import org.openiam.idm.srvc.report.dto.ReportTable
import org.openiam.idm.srvc.report.dto.ReportRow
import org.openiam.idm.srvc.report.dto.ReportRow.ReportColumn

class ReconciliationReport implements ReportDataSetBuilder {
    private org.springframework.context.ApplicationContext context
    private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws org.springframework.beans.BeansException {
        context = applicationContext;
    }

    @Override
    ReportDataDto getReportData(Map<String, String> reportParams) {
        def String action = "RECONCILIATION";
        def String managedSysID = reportParams.get("MANAGED_SYS_ID");

        AuditLogService auditLogService = context.getBean(AuditLogService.class);

        AuditLogSearchBean logSearchBean = new AuditLogSearchBean();
        logSearchBean.setAction(action);
        logSearchBean.setManagedSysId(managedSysID);
        logSearchBean.setSortBy("timestamp");
        logSearchBean.setOrderBy(OrderConstants.DESC);

        List<IdmAuditLogEntity> auditLogEntity = auditLogService.findBeans(logSearchBean, 0, 1);

        ReportTable reportTable = new ReportTable();
        reportTable.setName("ReconciliationReportTable1");

        ReportTable reportPropertyTable = new ReportTable();
        reportPropertyTable.setName("ReconciliationReportTable2");

        for(IdmAuditLogEntity a : auditLogEntity) {
            addRow(a, reportTable, "")
            for(IdmAuditLogCustomEntity ch : a.getCustomRecords()) {
                addRow(ch, reportPropertyTable);
            }
        }

        ReportDataDto reportDataDto = new ReportDataDto();
        List<ReportTable> reportTables = new ArrayList<ReportTable>();
        reportTables.add(reportTable);
        reportTables.add(reportPropertyTable);
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
        row.getColumn().add(new ReportColumn('ACTION_DATETIME', dateFormat.format(a.getTimestamp())));
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
        reportTable.getRow().add(row);
    }

    private void addRow(IdmAuditLogCustomEntity a, ReportTable reportTable) {
        ReportRow row = new ReportRow();
        row.getColumn().add(new ReportColumn('LOG_ID', a.getId()));
        row.getColumn().add(new ReportColumn('ACTION_DATETIME', a.getTimestamp().toString()));
        row.getColumn().add(new ReportColumn('FORMATTED_DATETIME', dateFormat.format(new Date(a.getTimestamp()))));
        row.getColumn().add(new ReportColumn('LOG_CUSTOM_KEY', a.getKey()));
        row.getColumn().add(new ReportColumn('LOG_CUSTOM_VALUE', a.getValue()));
        reportTable.getRow().add(row);
    }

}