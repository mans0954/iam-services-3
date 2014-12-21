package org.openiam.service.integration.property;


import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.property.ws.PropertyValueWebService;
import org.openiam.property.domain.PropertyType;
import org.openiam.property.domain.PropertyValueEntity;
import org.openiam.property.dto.PropertyValue;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;


public class PropertyValueServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("propertyValuerServiceClient")
	private PropertyValueWebService propertyValuerServiceClient;
	
	@Test
	public void testGetAll() {
		Assert.assertTrue(CollectionUtils.isNotEmpty(propertyValuerServiceClient.getAll()));
	}
	
	@Test
	public void testErrors() {
		boolean doCheck = false;
		List<PropertyValue> dtoList = propertyValuerServiceClient.getAll();
		for(final PropertyValue dto : dtoList) {
			if(dto.isMultilangual()) {
				doCheck = true;
				dto.setInternationalizedValues(null);
			}
		}
		
		if(doCheck) {
			Response response = propertyValuerServiceClient.save(dtoList, null);
			Assert.assertNotNull(response);
			Assert.assertTrue(response.isFailure());
			Assert.assertEquals(response.getErrorCode(), ResponseCode.PROPERTY_I18_VALUE_MISSING);
		}
		
		doCheck = false;
		dtoList = propertyValuerServiceClient.getAll();
		for(final PropertyValue dto : dtoList) {
			if(dto.isMultilangual() && !dto.isEmptyValueAllowed()) {
				doCheck = true;
				dto.getInternationalizedValues().get("0").setValue(null);
			}
		}
		
		if(doCheck) {
			Response response = propertyValuerServiceClient.save(dtoList, null);
			Assert.assertNotNull(response);
			Assert.assertTrue(response.isFailure());
			Assert.assertEquals(response.getErrorCode(), ResponseCode.PROPERTY_I18_VALUE_MISSING);
		}
		
		doCheck = false;
		for(final PropertyValue dto : dtoList) {
			if(!dto.isMultilangual()) {
				if(!dto.isEmptyValueAllowed()) {
					dto.setValue(null);
				}
			}
		}
		if(doCheck) {
			Response response = propertyValuerServiceClient.save(dtoList, null);
			Assert.assertNotNull(response);
			Assert.assertTrue(response.isFailure());
			Assert.assertEquals(response.getErrorCode(), ResponseCode.PROPERTY_VALUE_REQUIRED);
		}
		
		doCheck = false;
		for(final PropertyValue dto : dtoList) {
			if(!dto.isMultilangual()) {
				if(dto.getType().equals(PropertyType.BOOLEAN)) {
					doCheck = true;
					dto.setValue(getRandomName());
				}
			}
		}
		
		if(doCheck) {
			Response response = propertyValuerServiceClient.save(dtoList, null);
			Assert.assertNotNull(response);
			Assert.assertTrue(response.isFailure());
			Assert.assertEquals(response.getErrorCode(), ResponseCode.PROPERTY_TYPE_INVALID);
		}
	}
	
	@Test
	public void testSave() {
		List<PropertyValue> dtoList1 = propertyValuerServiceClient.getAll();
		Response response = propertyValuerServiceClient.save(dtoList1, null);
		Assert.assertNotNull(response);
		Assert.assertTrue(response.isSuccess());
		
		List<PropertyValue> dtoList2 = propertyValuerServiceClient.getAll();
		Assert.assertEquals(dtoList1, dtoList2);
	}
}
