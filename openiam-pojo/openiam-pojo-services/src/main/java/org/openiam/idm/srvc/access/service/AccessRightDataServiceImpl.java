package org.openiam.idm.srvc.access.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.AccessRightDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.Collection;
import java.util.List;

@Service("accessRightWS")
@WebService(endpointInterface = "org.openiam.idm.srvc.access.service.AccessRightDataService", targetNamespace = "urn:idm.openiam.org/srvc/access/service", portName = "AccessRightDataServicePort", serviceName = "AccessRightDataService")
public class AccessRightDataServiceImpl extends AbstractBaseService implements AccessRightDataService {

    private static final Log log = LogFactory.getLog(AccessRightDataServiceImpl.class);

    @Autowired
    private AccessRightService service;

    @Autowired
    private AccessRightDozerConverter converter;

    @Autowired
    private MetadataService metadataService;

    @Override
    public Response save(final AccessRight dto, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        try {
            if (dto == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }

            if ((dto.getMetadataType1() == null && dto.getMetadataType2() != null) || (dto.getMetadataType1() != null && dto.getMetadataType2() == null)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final AccessRightEntity entity = converter.convertToEntity(dto, true);

            MetadataTypeEntity mdE1 = null;
            MetadataTypeEntity mdE2 = null;
            if (entity.getMetadataTypeEntity1() != null) {
                mdE1 = metadataService.getById(entity.getMetadataTypeEntity1().getId());
            }
            if (entity.getMetadataTypeEntity2() != null) {
                mdE2 = metadataService.getById(entity.getMetadataTypeEntity2().getId());
            }
            if (entity.getId() != null) {
                idmAuditLog.setAction(AuditAction.UPDATE_ACCESS_RIGHT.value());
                idmAuditLog.setAuditDescription(String.format("Update Access Right : %s (%s -> %s)", entity.getName(),
                        (mdE1 == null ? "" : mdE1.getDescription() + " (" + mdE1.getGrouping() + ")"),
                        (mdE2 == null ? "" : mdE2.getDescription() + " (" + mdE2.getGrouping() + ")")));
            } else {
                idmAuditLog.setAction(AuditAction.ADD_ACCESS_RIGHT.value());
                idmAuditLog.setAuditDescription(String.format("Add Access Right : %s (%s -> %s)", entity.getName(),
                        (mdE1 == null ? "" : mdE1.getDescription() + " (" + mdE1.getGrouping() + ")"),
                        (mdE2 == null ? "" : mdE2.getDescription() + " (" + mdE2.getGrouping() + ")")));
            }

            service.save(entity);
            idmAuditLog.succeed();
            response.setResponseValue(entity.getId());
        } catch (BasicDataServiceException e) {
            response.fail();
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't save or update object", e);
            response.setErrorText(e.getMessage());
            response.fail();
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    public Response delete(String id, String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        try {
            if (StringUtils.isBlank(id)) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }
            idmAuditLog.setAction(AuditAction.DELETE_ACCESS_RIGHT.value());
            AccessRightEntity ar = service.get(id);
            if (ar != null) {
                idmAuditLog.setAuditDescription(String.format("Delete Access Right : %s (id = %s )", ar.getName(), id));
            } else {
                idmAuditLog.setAuditDescription(String.format("Delete Access Right with id: %s", id));
            }
            service.delete(id);
            idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.fail();
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't save or delete object", e);
            response.setErrorText(e.getMessage());
            response.fail();
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    public AccessRight get(String id) {
        final AccessRightEntity entity = service.get(id);
        final AccessRight dto = converter.convertToDTO(entity, true);
        return dto;
    }

    @Override
    @LocalizedServiceGet
    public List<AccessRight> findBeans(final AccessRightSearchBean searchBean, final int from, final int size, final Language language) {
        final List<AccessRightEntity> entities = service.findBeans(searchBean, from, size, language);
        final List<AccessRight> dtos = converter.convertToDTOList(entities, true);
        return dtos;
    }

    @Override
    public int count(AccessRightSearchBean searchBean) {
        return service.count(searchBean);
    }

    @Override
    public List<AccessRight> getByIds(final Collection<String> ids) {
        final List<AccessRightEntity> entities = service.findByIds(ids);
        final List<AccessRight> dtos = converter.convertToDTOList(entities, true);
        return dtos;
    }
}