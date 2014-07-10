package org.openiam.service.integration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.AbstractAttributeDTO;
import org.openiam.base.AbstractMetadataTypeDTO;
import org.openiam.base.AdminResourceDTO;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class AbstractAttributeServiceTest<T extends AbstractMetadataTypeDTO, S extends AbstractKeyNameSearchBean<T, String>, A extends AbstractAttributeDTO> extends
					  AbstractMetadataTypeServiceTest<T,S> {
	
	protected abstract A createAttribute(T t);
	protected abstract Set<A> createAttributeSet();
	protected abstract void setAttributes(final T t, final Set<A> attributes);
	protected abstract Set<A> getAttributes(final T t);
	
	@Test
	public void attributeClusterTest() {
		T instance = newInstance();
		instance.setName(getRandomName());
		Response response = save(instance);
		Assert.assertTrue(response.isSuccess(), String.format("Could not save entity.  %s", response));
		instance = get((String)response.getResponseValue());
		
		final Set<A> attributes = createAttributeSet();
		final A attribute = createAttribute(instance);
		attribute.setName(getRandomName());
		attribute.setValue(getRandomName());
		attributes.add(attribute);
		setAttributes(instance, attributes);
		response = save(instance);
		Assert.assertTrue(response.isSuccess(), String.format("Could not save entity.  %s", response));
		
		T instance1 = get((String)response.getResponseValue());
		T instance2 = get((String)response.getResponseValue());
		Assert.assertTrue(CollectionUtils.isNotEmpty(getAttributes(instance)), "Attributes were empty");
		Assert.assertEquals(getAttributes(instance1), getAttributes(instance2));
		setAttributes(instance, null);
		response = save(instance);
		Assert.assertTrue(response.isSuccess(), String.format("Could not save entity.  %s", response));
		instance1 = get((String)response.getResponseValue());
		instance2 = get((String)response.getResponseValue());
		Assert.assertTrue(CollectionUtils.isEmpty(getAttributes(instance)), "Attributes were not empty");
		Assert.assertEquals(getAttributes(instance1), getAttributes(instance2));
		
		delete(instance1);
	}
}
