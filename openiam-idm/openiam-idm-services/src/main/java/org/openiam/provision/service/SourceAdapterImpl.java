package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.sysprop.dto.SystemPropertyDto;
import org.openiam.idm.srvc.sysprop.service.SystemPropertyService;
import org.openiam.provision.dto.srcadapter.SourceAdapterInfoResponse;
import org.openiam.provision.dto.srcadapter.SourceAdapterRequest;
import org.openiam.provision.dto.srcadapter.SourceAdapterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zaporozhec on 10/29/15.
 */
@WebService(endpointInterface = "org.openiam.provision.service.SourceAdapter", targetNamespace = "http://www.openiam.org/service/provision", portName = "SourceAdapterServicePort", serviceName = "SourceAdapterService")
@Component("sourceAdapter")
public class SourceAdapterImpl implements SourceAdapter {

    @Autowired
    private MetadataWebService metadataWS;
    @Autowired
    private SystemPropertyService systemPropertyService;

    @Autowired
    private ManagedSystemWebService managedSysService;
    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    protected AuditLogService auditLogService;
    @Autowired
    @Qualifier("sourceAdapterDispatcher")
    private SourceAdapterDispatcher sourceAdapterDispatcher;

    @Override
    public SourceAdapterResponse perform(SourceAdapterRequest request) {
        List<SystemPropertyDto> propertyDtos = systemPropertyService.getByType("SOURCE_ADAPTER_PROP");
        if (CollectionUtils.isNotEmpty(propertyDtos)) {
            for (SystemPropertyDto systemPropertyDto : propertyDtos) {
                if ("MODE".equalsIgnoreCase(systemPropertyDto.getName())) {
                    request.setMode(systemPropertyDto.getValue());
                    continue;
                }
                if ("PRE_PROCESSOR".equalsIgnoreCase(systemPropertyDto.getName())) {
                    request.setPathToPreProcessor(systemPropertyDto.getValue());
                    continue;
                }
            }
        }
        SourceAdapterResponse response = new SourceAdapterResponse();
        if ("DISABLE".equalsIgnoreCase(request.getMode())) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setError("Source adapter is disabled");
            return response;
        }
        response.setStatus(ResponseStatus.SUCCESS);
        long time = System.currentTimeMillis();
        sourceAdapterDispatcher.pushToQueue(request);
        response.setError("Processing time= " + (System.currentTimeMillis() - time) + "ms");
        if ("SIMULATION".contentEquals(request.getMode())) {
            response.setError(response.getError() + " Source adapter in simulation mode! OpenIAM will receive the data and log it, but nothing will be changed");
        }
        return response;
    }

    @Override
    public SourceAdapterInfoResponse info() {
        SourceAdapterInfoResponse response = new SourceAdapterInfoResponse();
        MetadataTypeSearchBean metadataTypeSearchBean = new MetadataTypeSearchBean();
        metadataTypeSearchBean.setDeepCopy(false);
        metadataTypeSearchBean.setActive(true);
        List<String> notes = new ArrayList<String>();
        notes.add(this.getKeyNote());
        notes.add(this.getWarningNote());
        notes.add(this.getManagedSystems());
        notes.add(this.getNote(metadataTypeSearchBean, MetadataTypeGrouping.ADDRESS));
        notes.add(this.getNote(metadataTypeSearchBean, MetadataTypeGrouping.AFFILIATIONS));
        notes.add(this.getNote(metadataTypeSearchBean, MetadataTypeGrouping.EMAIL));
        notes.add(this.getNote(metadataTypeSearchBean, MetadataTypeGrouping.PHONE));
        notes.add(this.getNote(metadataTypeSearchBean, MetadataTypeGrouping.USER_TYPE));
        response.setNotes(notes);
        return response;
    }

    private String getManagedSystems() {
        StringBuilder sb = new StringBuilder("Available Managed System Ids (for principals) \n");
        List<ManagedSysDto> managedSysDtos = managedSysService.getAllManagedSys();
        if (CollectionUtils.isNotEmpty(managedSysDtos)) {
            for (ManagedSysDto managedSysDto : managedSysDtos) {
                if ("ACTIVE".equals(managedSysDto.getStatus())) {
                    sb.append("id:");
                    sb.append(managedSysDto.getId());
                    sb.append("/Name:");
                    sb.append(managedSysDto.getName());
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    private String getKeyNote() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<key>,<requestor>,<user-supervisor> entities are working with name/value attributes\n");
        sb.append("This means that user will be found by this key. Available key values are:\n");
        sb.append("'user_id' - find by internal user Id\n");
        sb.append("'email' - find by primary email\n");
        sb.append("'employee_id' - find by employee Id\n");
        return sb.toString();
    }

    private String getWarningNote() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<skipWarnings> entity provide ability to skip all validation warnings and perform request\n");
        sb.append("It may be wrong operation or role/group/role with unavailable names\n");
        sb.append("In this case only wrong parts will be ignored\n");
        return sb.toString();
    }

    private String getNote(MetadataTypeSearchBean metadataTypeSearchBean, MetadataTypeGrouping name) {
        metadataTypeSearchBean.setGrouping(name);
        List<MetadataType> types = metadataWS.findTypeBeans(metadataTypeSearchBean, 0, Integer.MAX_VALUE, null);
        StringBuilder sb = new StringBuilder();
        sb.append("\nAvailable types for ");
        sb.append(name.name());
        sb.append("\n");
        if (CollectionUtils.isNotEmpty(types)) {
            for (MetadataType type : types) {
                sb.append("Description:");
                sb.append(type.getDescription());
                sb.append("/Value:");
                sb.append(type.getId());
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
