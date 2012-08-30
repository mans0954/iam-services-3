
//import org.openiam.idm.groovy.helper.ServiceHelper;

import java.util.Map;
import org.openiam.idm.srvc.synch.service.generic.ObjectHandler;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupSearch;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.grp.ws.GroupListResponse;
import org.openiam.base.ws.ResponseStatus;
import java.util.ResourceBundle;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;



public class GroupHandler implements ObjectHandler {
	
	GroupDataWebService grpService = groupService()
		
	
	public Object populateObject(LineObject rowObj) {
		
		println("GroupHandler called.");
		
		Attribute attrVal = null;
		
		Map<String,Attribute> columnMap =  rowObj.getColumnMap();
	
		
		
		Group grp = new Group();
		
		
		attrVal = columnMap.get("GROUP_NAME");
		if (attrVal != null) {
			grp.grpName = attrVal.getValue();
		}	
				
		attrVal = columnMap.get("DESCRIPTION");
		if (attrVal != null) {
			grp.description = attrVal.getValue();
		}	
		
		
		attrVal = columnMap.get("OPERATION");
		if (attrVal != null) {
			if ('D'.equalIgnorCase(attrVal.getValue())) {
				grp.operation = AttributeOperationEnum.DELETE;
			}
		}	
				
		attrVal = columnMap.get("PARENT_GROUP");
		if (attrVal != null) {
			Group parentGroupObj = getGroup(attrVal.getValue());
			if (parentGroupObj != null) {
				grp.parentGrpId = parentGroupObj.grpId;
			}
			
		}	
		
		
		attrVal = columnMap.get("EXTERNAL_GROUP_NAME");
		if (attrVal != null) {
			grp.internalGroupId = attrVal.getValue();
		}	
		
		attrVal = columnMap.get("GROUP_TYPE");
		if (attrVal != null) {
			grp.groupClass = attrVal.getValue();
		}
		
		return grp;	
		
		
		
	
	}
	
	public Object existingObject(Object obj) {
		Group grp = (Group) obj;
		return getGroup(grp.grpName);
	}
	
	
	Group getGroup(String name) {
		GroupSearch search = new GroupSearch();
		search.grpName = name;
		GroupListResponse resp = grpService.groupSearch(search);
		if (resp.status == ResponseStatus.FAILURE) {
			return null;			
		}
		return resp.groupList.get(0);
		
	}
	
	static GroupDataWebService groupService() {
	 ResourceBundle res = ResourceBundle.getBundle("datasource");
 	 String BASE_URL =  res.getString("openiam.service_host") + res.getString("openiam.idm.ws.path");
 
	
		String serviceUrl = BASE_URL + "/GroupDataWebService"
		String port ="GroupDataWebServicePort"
		String nameSpace = "urn:idm.openiam.org/srvc/grp/service"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				GroupDataWebService.class);
	}
	
	
}


