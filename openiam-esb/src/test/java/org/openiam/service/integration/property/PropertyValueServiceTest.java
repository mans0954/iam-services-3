package org.openiam.service.integration.property;


import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.property.domain.PropertyType;
import org.openiam.property.dto.PropertyValue;
import org.openiam.service.integration.AbstractServiceTest;
import org.testng.Assert;
import org.testng.annotations.Test;


public class PropertyValueServiceTest extends AbstractServiceTest {
	
	@Test
	public void testGetAll() {
		Assert.assertTrue(CollectionUtils.isNotEmpty(propertyValuerServiceClient.getAll()));
		propertyValuerServiceClient.getAll().forEach(dto -> {
			if(dto.isMultilangual()) {
				Assert.assertTrue(MapUtils.isNotEmpty(dto.getInternationalizedValues()));
			}
		});
	}
	
	@Test(threadPoolSize = 20, invocationCount = 100000, enabled=false)
	public void stressTest() {
		Assert.assertTrue(StringUtils.isNotBlank(propertyValuerServiceClient.getCachedValue("org.openiam.ui.user.fullname.compose.rule")));
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
			Response response = propertyValuerServiceClient.save(dtoList);
			Assert.assertNotNull(response);
			Assert.assertTrue(response.isFailure());
			Assert.assertEquals(response.getErrorCode(), ResponseCode.PROPERTY_I18_VALUE_MISSING);
		}
		
		doCheck = false;
		dtoList = propertyValuerServiceClient.getAll();
		for(final PropertyValue dto : dtoList) {
			if(dto.isMultilangual() && !dto.isEmptyValueAllowed()) {
				doCheck = true;
				dto.getInternationalizedValues().get("1").setValue(null);
			}
		}
		
		if(doCheck) {
			Response response = propertyValuerServiceClient.save(dtoList);
			Assert.assertNotNull(response);
			Assert.assertTrue(response.isFailure());
			Assert.assertEquals(response.getErrorCode(), ResponseCode.PROPERTY_VALUE_REQUIRED);
		}
		
		doCheck = false;
		dtoList = propertyValuerServiceClient.getAll();
		for(final PropertyValue dto : dtoList) {
			if(!dto.isMultilangual()) {
				if(!dto.isEmptyValueAllowed()) {
					dto.setValue(null);
				}
			}
		}
		if(doCheck) {
			Response response = propertyValuerServiceClient.save(dtoList);
			Assert.assertNotNull(response);
			Assert.assertTrue(response.isFailure());
			Assert.assertEquals(response.getErrorCode(), ResponseCode.PROPERTY_VALUE_REQUIRED);
		}
		
		doCheck = false;
		dtoList = propertyValuerServiceClient.getAll();
		for(final PropertyValue dto : dtoList) {
			if(!dto.isMultilangual()) {
				if(dto.getType().equals(PropertyType.Boolean)) {
					doCheck = true;
					dto.setValue(getRandomName());
				}
			}
		}
		
		if(doCheck) {
			Response response = propertyValuerServiceClient.save(dtoList);
			Assert.assertNotNull(response);
			Assert.assertTrue(response.isFailure());
			Assert.assertEquals(response.getErrorCode(), ResponseCode.PROPERTY_TYPE_INVALID);
		}
	}
	
	@Test
	public void testSave() {
		List<PropertyValue> dtoList1 = propertyValuerServiceClient.getAll();
		Response response = propertyValuerServiceClient.save(dtoList1);
		Assert.assertNotNull(response);
		Assert.assertTrue(response.isSuccess());
		
		List<PropertyValue> dtoList2 = propertyValuerServiceClient.getAll();
		Assert.assertEquals(dtoList1, dtoList2);
	}
}
