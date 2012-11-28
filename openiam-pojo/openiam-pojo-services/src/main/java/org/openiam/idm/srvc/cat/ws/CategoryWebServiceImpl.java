package org.openiam.idm.srvc.cat.ws;

import javax.jws.WebService;

import org.openiam.idm.srvc.cat.dto.Category;
import org.openiam.idm.srvc.cat.service.CategoryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("categorydataWS")
@WebService(endpointInterface = "org.openiam.idm.srvc.cat.ws.CategoryWebService", targetNamespace = "urn:idm.openiam.org/srvc/cat/service", portName = "CategoryDataWebServicePort", serviceName = "CategoryDataWebService")
public class CategoryWebServiceImpl implements CategoryWebService {

    @Autowired
    CategoryDataService categoryDataService;
    
    public void addCategory(Category cat) {
        categoryDataService.addCategory(cat);
    }

    public Category[] getAllCategories(boolean nested) {
        return categoryDataService.getAllCategories(nested);
    }

    public Category getCategory(String categoryId) {
        return categoryDataService.getCategory(categoryId);
    }

    public Category[] getChildCategories(String categoryId, boolean nested) {
        return categoryDataService.getChildCategories(categoryId, nested);
    }

    public int removeCategory(String categoryId, boolean nested) {
            return categoryDataService.removeCategory(categoryId, nested);
        }

    public void updateCategory(Category cat) {
        categoryDataService.updateCategory(cat);
    }
}
	
