package org.openiam.idm.srvc.meta.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateTypeField;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.meta.dto.TemplateRequest;


@WebService(targetNamespace = "urn:idm.openiam.org/srvc/meta/ws", name = "MetadataElementTemplateWebService")
public interface MetadataElementTemplateWebService {

	 @WebMethod
	 List<MetadataElementPageTemplate> findBeans(
			 @WebParam(name = "searchBean", targetNamespace = "") MetadataElementPageTemplateSearchBean searchBean,
			 @WebParam(name = "from", targetNamespace = "") int from,
			 @WebParam(name = "size", targetNamespace = "") int size);
	 
	 @WebMethod
	 int count(@WebParam(name = "searchBean", targetNamespace = "") MetadataElementPageTemplateSearchBean searchBean);
	 
	 @WebMethod
	 Response save(final @WebParam(name = "template", targetNamespace = "") MetadataElementPageTemplate template);
	 
	 @WebMethod
	 Response delete(final @WebParam(name = "templateId", targetNamespace = "") String templateId);

	 @WebMethod
	 PageTempate getTemplate(final @WebParam(name = "template", targetNamespace = "") TemplateRequest request);
	 
	 @WebMethod
	 MetadataTemplateType getTemplateType(final @WebParam(name = "id", targetNamespace = "") String id);
	 
	 @WebMethod
	 List<MetadataTemplateType> findTemplateTypes(final @WebParam(name = "searchBean", targetNamespace = "") MetadataTemplateTypeSearchBean searchBean,
												  final @WebParam(name = "from", targetNamespace = "") int from,
												  final @WebParam(name = "size", targetNamespace = "") int size);
	 
	 @WebMethod
	 List<MetadataTemplateTypeField> findUIFIelds(final @WebParam(name = "searchBean", targetNamespace = "") MetadataTemplateTypeFieldSearchBean searchBean,
												  final @WebParam(name = "from", targetNamespace = "") int from,
												  final @WebParam(name = "size", targetNamespace = "") int size);
    @WebMethod
	int countUIFields(final @WebParam(name = "searchBean", targetNamespace = "") MetadataTemplateTypeFieldSearchBean searchBean);
}
