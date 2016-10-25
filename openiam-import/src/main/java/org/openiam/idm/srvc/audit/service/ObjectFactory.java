
package org.openiam.idm.srvc.audit.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openiam.idm.srvc.audit.service package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AddLogsResponse_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "addLogsResponse");
    private final static QName _Count_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "count");
    private final static QName _FindBeansResponse_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "findBeansResponse");
    private final static QName _GetIdsResponse_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "getIdsResponse");
    private final static QName _GetLogRecordResponse_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "getLogRecordResponse");
    private final static QName _GetIds_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "getIds");
    private final static QName _CountResponse_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "countResponse");
    private final static QName _AddLogResponse_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "addLogResponse");
    private final static QName _GetLogRecord_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "getLogRecord");
    private final static QName _AddLog_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "addLog");
    private final static QName _AddLogs_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "addLogs");
    private final static QName _FindBeans_QNAME = new QName("urn:idm.openiam.org/srvc/audit/service", "findBeans");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openiam.idm.srvc.audit.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CountResponse }
     * 
     */
    public CountResponse createCountResponse() {
        return new CountResponse();
    }

    /**
     * Create an instance of {@link AddLogResponse }
     * 
     */
    public AddLogResponse createAddLogResponse() {
        return new AddLogResponse();
    }

    /**
     * Create an instance of {@link GetLogRecordResponse }
     * 
     */
    public GetLogRecordResponse createGetLogRecordResponse() {
        return new GetLogRecordResponse();
    }

    /**
     * Create an instance of {@link GetIds }
     * 
     */
    public GetIds createGetIds() {
        return new GetIds();
    }

    /**
     * Create an instance of {@link GetIdsResponse }
     * 
     */
    public GetIdsResponse createGetIdsResponse() {
        return new GetIdsResponse();
    }

    /**
     * Create an instance of {@link FindBeansResponse }
     * 
     */
    public FindBeansResponse createFindBeansResponse() {
        return new FindBeansResponse();
    }

    /**
     * Create an instance of {@link Count }
     * 
     */
    public Count createCount() {
        return new Count();
    }

    /**
     * Create an instance of {@link AddLogsResponse }
     * 
     */
    public AddLogsResponse createAddLogsResponse() {
        return new AddLogsResponse();
    }

    /**
     * Create an instance of {@link AddLog }
     * 
     */
    public AddLog createAddLog() {
        return new AddLog();
    }

    /**
     * Create an instance of {@link FindBeans }
     * 
     */
    public FindBeans createFindBeans() {
        return new FindBeans();
    }

    /**
     * Create an instance of {@link AddLogs }
     * 
     */
    public AddLogs createAddLogs() {
        return new AddLogs();
    }

    /**
     * Create an instance of {@link GetLogRecord }
     * 
     */
    public GetLogRecord createGetLogRecord() {
        return new GetLogRecord();
    }

    /**
     * Create an instance of {@link AuditLogTarget }
     * 
     */
    public AuditLogTarget createAuditLogTarget() {
        return new AuditLogTarget();
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link SortParam }
     * 
     */
    public SortParam createSortParam() {
        return new SortParam();
    }

    /**
     * Create an instance of {@link AuditLogSearchBean }
     * 
     */
    public AuditLogSearchBean createAuditLogSearchBean() {
        return new AuditLogSearchBean();
    }

    /**
     * Create an instance of {@link IdmAuditLog }
     * 
     */
    public IdmAuditLog createIdmAuditLog() {
        return new IdmAuditLog();
    }

    /**
     * Create an instance of {@link IdmAuditLogCustom }
     * 
     */
    public IdmAuditLogCustom createIdmAuditLogCustom() {
        return new IdmAuditLogCustom();
    }

    /**
     * Create an instance of {@link EsbErrorToken }
     * 
     */
    public EsbErrorToken createEsbErrorToken() {
        return new EsbErrorToken();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddLogsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "addLogsResponse")
    public JAXBElement<AddLogsResponse> createAddLogsResponse(AddLogsResponse value) {
        return new JAXBElement<AddLogsResponse>(_AddLogsResponse_QNAME, AddLogsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Count }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "count")
    public JAXBElement<Count> createCount(Count value) {
        return new JAXBElement<Count>(_Count_QNAME, Count.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindBeansResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "findBeansResponse")
    public JAXBElement<FindBeansResponse> createFindBeansResponse(FindBeansResponse value) {
        return new JAXBElement<FindBeansResponse>(_FindBeansResponse_QNAME, FindBeansResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetIdsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "getIdsResponse")
    public JAXBElement<GetIdsResponse> createGetIdsResponse(GetIdsResponse value) {
        return new JAXBElement<GetIdsResponse>(_GetIdsResponse_QNAME, GetIdsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLogRecordResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "getLogRecordResponse")
    public JAXBElement<GetLogRecordResponse> createGetLogRecordResponse(GetLogRecordResponse value) {
        return new JAXBElement<GetLogRecordResponse>(_GetLogRecordResponse_QNAME, GetLogRecordResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetIds }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "getIds")
    public JAXBElement<GetIds> createGetIds(GetIds value) {
        return new JAXBElement<GetIds>(_GetIds_QNAME, GetIds.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "countResponse")
    public JAXBElement<CountResponse> createCountResponse(CountResponse value) {
        return new JAXBElement<CountResponse>(_CountResponse_QNAME, CountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddLogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "addLogResponse")
    public JAXBElement<AddLogResponse> createAddLogResponse(AddLogResponse value) {
        return new JAXBElement<AddLogResponse>(_AddLogResponse_QNAME, AddLogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLogRecord }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "getLogRecord")
    public JAXBElement<GetLogRecord> createGetLogRecord(GetLogRecord value) {
        return new JAXBElement<GetLogRecord>(_GetLogRecord_QNAME, GetLogRecord.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddLog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "addLog")
    public JAXBElement<AddLog> createAddLog(AddLog value) {
        return new JAXBElement<AddLog>(_AddLog_QNAME, AddLog.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddLogs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "addLogs")
    public JAXBElement<AddLogs> createAddLogs(AddLogs value) {
        return new JAXBElement<AddLogs>(_AddLogs_QNAME, AddLogs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindBeans }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/audit/service", name = "findBeans")
    public JAXBElement<FindBeans> createFindBeans(FindBeans value) {
        return new JAXBElement<FindBeans>(_FindBeans_QNAME, FindBeans.class, null, value);
    }

}
