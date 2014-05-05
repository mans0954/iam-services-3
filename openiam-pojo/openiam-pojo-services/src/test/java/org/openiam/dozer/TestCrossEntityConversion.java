package org.openiam.dozer;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.RandomStringUtils;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.CollectionUtils;
import org.testng.annotations.Test;

@ContextConfiguration("classpath:test-application-context.xml")
public class TestCrossEntityConversion extends AbstractTestNGSpringContextTests {

    @Autowired
    private ResourceDozerConverter resourceConverter;

    @Test
    public void testShallowResourceConversion() {
	final ResourceEntity entity = createSimpleResourceEntity();
	final Resource resource = resourceConverter.convertToDTO(entity, false);
	confirmSimple(resource, entity);
	confirmEmptyCollections(resource);
	final ResourceEntity convertedEntity = resourceConverter.convertToEntity(resource, false);
	confirmSimple(resource, convertedEntity);
	confirmEmptyCollections(convertedEntity);
    }

    @Test
    public void testDeepResouceConversion() {
	final ResourceEntity entity = createDeepResourceEntity();
	final Resource resource = resourceConverter.convertToDTO(entity, true);
	confirmSimple(resource, entity);
	confirmCollections(resource, entity);
	final ResourceEntity converted = resourceConverter.convertToEntity(resource, true);
	confirmCollections(resource, converted);
    }

    private void confirmCollections(final Resource resource, final ResourceEntity entity) {
	Assert.assertEquals(resource.getParentResources().size(), entity.getParentResources().size());
	Assert.assertEquals(resource.getChildResources().size(), entity.getChildResources().size());
	Assert.assertEquals(resource.getResourceProps().size(), entity.getResourceProps().size());
    }

    private void confirmEmptyCollections(final ResourceEntity entity) {
	Assert.assertTrue(CollectionUtils.isEmpty(entity.getChildResources()));
	Assert.assertTrue(CollectionUtils.isEmpty(entity.getParentResources()));
	Assert.assertTrue(CollectionUtils.isEmpty(entity.getResourceProps()));
    }

    private void confirmEmptyCollections(final Resource resource) {
	Assert.assertTrue(CollectionUtils.isEmpty(resource.getChildResources()));
	Assert.assertTrue(CollectionUtils.isEmpty(resource.getParentResources()));
	Assert.assertTrue(CollectionUtils.isEmpty(resource.getResourceProps()));
    }

    private void confirmSimple(final Resource resource, final ResourceEntity entity) {
	Assert.assertEquals(resource.getDescription(), entity.getDescription());
	Assert.assertEquals(resource.getDisplayOrder(), entity.getDisplayOrder());
	Assert.assertEquals(resource.getIsPublic(), entity.getIsPublic());
	// Assert.assertEquals(resource.getIsSSL(), entity.getIsSSL());
	Assert.assertEquals(resource.getMinAuthLevel(), entity.getMinAuthLevel());
	Assert.assertEquals(resource.getName(), entity.getName());
	Assert.assertEquals(resource.getId(), entity.getId());
	Assert.assertEquals(resource.getURL(), entity.getURL());
	confirm(resource.getResourceType(), entity.getResourceType());
    }

    private void confirm(final ResourceType resourceType, final ResourceTypeEntity entity) {
	Assert.assertEquals(resourceType.getDescription(), entity.getDescription());
	Assert.assertEquals(resourceType.getProcessName(), entity.getProcessName());
	Assert.assertEquals(resourceType.getId(), entity.getId());
    }

    private ResourceEntity createDeepResourceEntity() {
	final ResourceEntity entity = createSimpleResourceEntity();
	final Set<ResourceEntity> childResources = new HashSet<ResourceEntity>();
	childResources.add(createSimpleResourceEntity());
	childResources.add(createSimpleResourceEntity());
	childResources.add(createSimpleResourceEntity());
	childResources.add(createSimpleResourceEntity());
	childResources.add(createSimpleResourceEntity());
	entity.setChildResources(childResources);

	final Set<ResourceEntity> parentResources = new HashSet<ResourceEntity>();
	parentResources.add(createSimpleResourceEntity());
	parentResources.add(createSimpleResourceEntity());
	parentResources.add(createSimpleResourceEntity());
	parentResources.add(createSimpleResourceEntity());
	parentResources.add(createSimpleResourceEntity());
	parentResources.add(createSimpleResourceEntity());
	entity.setParentResources(parentResources);

	final Set<ResourcePropEntity> resourceProps = new HashSet<ResourcePropEntity>();
	resourceProps.add(createResourcePropEntity());
	resourceProps.add(createResourcePropEntity());
	resourceProps.add(createResourcePropEntity());
	resourceProps.add(createResourcePropEntity());
	resourceProps.add(createResourcePropEntity());
	resourceProps.add(createResourcePropEntity());
	entity.setResourceProps(resourceProps);

	return entity;
    }

    private ResourcePropEntity createResourcePropEntity() {
	final ResourcePropEntity entity = new ResourcePropEntity();
	entity.setElement(createMetadataElementEntity());
	entity.setName(rs(2));
	entity.setValue(rs(2));
	// entity.setResourceId(rs(2));
	entity.setId(rs(2));
	return entity;
    }

    private ResourceEntity createSimpleResourceEntity() {
	final ResourceEntity entity = new ResourceEntity();
	entity.setDescription(rs(2));
	entity.setDisplayOrder(3);
	entity.setIsPublic(true);
	// entity.setIsSSL(true);
	entity.setMinAuthLevel(rs(2));
	entity.setName(rs(2));
	entity.setId(rs(2));
	entity.setResourceType(createResourceTypeEntity());
	entity.setURL(rs(2));
	return entity;
    }

    private ResourceTypeEntity createResourceTypeEntity() {
	final ResourceTypeEntity entity = new ResourceTypeEntity();
	entity.setDescription(rs(2));
	entity.setProcessName(rs(2));
	entity.setId(rs(2));
	return entity;
    }

    private MetadataElementEntity createMetadataElementEntity() {
        final MetadataElementEntity entity = new MetadataElementEntity();
        entity.setDescription(rs(2));
        entity.setAttributeName(rs(2));
        entity.setId(rs(2));
        return entity;
    }

    private String rs(final int size) {
	return RandomStringUtils.randomAlphanumeric(size);
    }
}
