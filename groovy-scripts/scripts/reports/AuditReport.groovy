import groovy.sql.*
import org.openiam.core.dto.reports.ReportRow
import org.openiam.core.dto.reports.ReportRow.ReportColumn
import org.openiam.base.id.UUIDGen
import org.openiam.core.dto.reports.ReportDataDto
import org.openiam.core.dto.reports.ReportTable;


def db='jdbc:mysql://localhost:3306/openiam'
def user='idmuser'
def password='idmuser'
def driver='com.mysql.jdbc.Driver'
// connect to the db
def sql = Sql.newInstance(db,user, password, driver)

//Registration Groovy script with datasource for AuditReport in Database
//
GroovyRowResult groovyRowScript = sql.firstRow("select count(*) as count from REPORT_INFO where report_name='AUDIT_REPORT'");
if(((Integer)groovyRowScript.get('count')) == 0) {
    sql.call("insert into REPORT_INFO(report_info_id, report_name, groovy_script_path, report_file_path, params, required_params) values('"+UUIDGen.getUUID()+"', 'AUDIT_REPORT', 'reports/AuditReport.groovy', 'reports/AuditReport.rptdesign', 'ACTION_ID,ACTION_DATETIME_START,ACTION_DATETIME_END', 'ACTION_ID,ACTION_DATETIME_START,ACTION_DATETIME_END'); ")
}

//def action = 'AUTHENTICATION';
//def actionStartDate = '2012-08-29 23:28:44';
//def actionEndDate = '2012-10-29 23:28:44';

org.openiam.core.dto.reports.ReportTable reportTable = new org.openiam.core.dto.reports.ReportTable();
reportTable.setName("AuditReportResultSet");

String query = 'SELECT IDM_AUDIT_LOG.LOG_ID, IDM_AUDIT_LOG.OBJECT_TYPE_ID, IDM_AUDIT_LOG.OBJECT_ID,'+
  'IDM_AUDIT_LOG.ACTION_ID, IDM_AUDIT_LOG.ACTION_STATUS, IDM_AUDIT_LOG.REASON,'+
  'IDM_AUDIT_LOG.REASON_DETAIL, IDM_AUDIT_LOG.ACTION_DATETIME,'+
  'IDM_AUDIT_LOG.OBJECT_NAME, IDM_AUDIT_LOG.RESOURCE_NAME, IDM_AUDIT_LOG.USER_ID,'+
  'IDM_AUDIT_LOG.SERVICE_ID, IDM_AUDIT_LOG.LOGIN_ID, IDM_AUDIT_LOG.HOST,'+
  'IDM_AUDIT_LOG.CLIENT_ID, IDM_AUDIT_LOG.REQ_URL, IDM_AUDIT_LOG.LINKED_LOG_ID,'+
  'IDM_AUDIT_LOG.LINK_SEQUENCE, IDM_AUDIT_LOG.ORIG_OBJECT_STATE,'+
  'IDM_AUDIT_LOG.NEW_OBJECT_STATE, IDM_AUDIT_LOG.SRC_SYSTEM_ID,'+
  'IDM_AUDIT_LOG.TARGET_SYSTEM_ID, IDM_AUDIT_LOG.REQUEST_ID, IDM_AUDIT_LOG.SESSION_ID,'+
  'IDM_AUDIT_LOG.CUSTOM_ATTRNAME1, IDM_AUDIT_LOG.CUSTOM_ATTRNAME2,'+
  'USERS.FIRST_NAME, USERS.MIDDLE_INIT, USERS.LAST_NAME, USERS.DEPT_CD,'+
  'USERS.DEPT_NAME FROM IDM_AUDIT_LOG, USERS WHERE IDM_AUDIT_LOG.USER_ID = USERS.USER_ID AND IDM_AUDIT_LOG.ACTION_ID = \''+ACTION_ID+
  '\' AND IDM_AUDIT_LOG.ACTION_DATETIME >= \''+ACTION_DATETIME_START+'\' AND IDM_AUDIT_LOG.ACTION_DATETIME <= \''+ACTION_DATETIME_END+'\'';


sql.eachRow(query) { a ->
            ReportRow row = new ReportRow();
            row.getColumn().add(new ReportColumn('LOG_ID',a.LOG_ID));
			row.getColumn().add(new ReportColumn('OBJECT_TYPE_ID',a.OBJECT_TYPE_ID));
			row.getColumn().add(new ReportColumn('OBJECT_ID',a.OBJECT_ID));
			row.getColumn().add(new ReportColumn('ACTION_ID',a.ACTION_ID));
			row.getColumn().add(new ReportColumn('ACTION_STATUS',a.ACTION_STATUS));
			row.getColumn().add(new ReportColumn('REASON',a.REASON));
			row.getColumn().add(new ReportColumn('REASON_DETAIL',a.REASON_DETAIL));
			row.getColumn().add(new ReportColumn('ACTION_DATETIME',a.ACTION_DATETIME.toString()));
			row.getColumn().add(new ReportColumn('OBJECT_NAME',a.OBJECT_NAME));
			row.getColumn().add(new ReportColumn('RESOURCE_NAME',a.RESOURCE_NAME));
			row.getColumn().add(new ReportColumn('USER_ID',a.USER_ID));
			row.getColumn().add(new ReportColumn('SERVICE_ID',a.SERVICE_ID));
			row.getColumn().add(new ReportColumn('LOGIN_ID',a.LOGIN_ID));
			row.getColumn().add(new ReportColumn('HOST',a.HOST));
			row.getColumn().add(new ReportColumn('CLIENT_ID',a.CLIENT_ID));
			row.getColumn().add(new ReportColumn('REQ_URL',a.REQ_URL));
			row.getColumn().add(new ReportColumn('LINKED_LOG_ID',a.LINKED_LOG_ID));
			row.getColumn().add(new ReportColumn('LINK_SEQUENCE',a.LINK_SEQUENCE.toString()));
			row.getColumn().add(new ReportColumn('ORIG_OBJECT_STATE',a.ORIG_OBJECT_STATE));
			row.getColumn().add(new ReportColumn('NEW_OBJECT_STATE',a.NEW_OBJECT_STATE));
			row.getColumn().add(new ReportColumn('SRC_SYSTEM_ID',a.SRC_SYSTEM_ID));
			row.getColumn().add(new ReportColumn('TARGET_SYSTEM_ID',a.TARGET_SYSTEM_ID));
			row.getColumn().add(new ReportColumn('REQUEST_ID',a.REQUEST_ID));
			row.getColumn().add(new ReportColumn('SESSION_ID',a.SESSION_ID));
			row.getColumn().add(new ReportColumn('CUSTOM_ATTRNAME1',a.CUSTOM_ATTRNAME1));
			row.getColumn().add(new ReportColumn('CUSTOM_ATTRNAME2',a.CUSTOM_ATTRNAME2));
			row.getColumn().add(new ReportColumn('FIRST_NAME',a.FIRST_NAME));
			row.getColumn().add(new ReportColumn('MIDDLE_INIT',a.MIDDLE_INIT));
			row.getColumn().add(new ReportColumn('LAST_NAME',a.LAST_NAME));
			row.getColumn().add(new ReportColumn('DEPT_CD',a.DEPT_CD));
			row.getColumn().add(new ReportColumn('DEPT_NAME',a.DEPT_NAME));
            reportTable.getRow().add(row);
}
ReportDataDto reportDataDto = new ReportDataDto();
List<ReportTable> reportTables = new ArrayList<ReportTable>();
reportTables.add(reportTable);
reportDataDto.setTables(reportTables)
output = reportDataDto;
println('Groovy script AuditReport.groovy has been executed');

