package org.openiam.service.integration.entitlements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.service.integration.AbstractAttributeServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ResourceServiceTest extends AbstractAttributeServiceTest<Resource, ResourceSearchBean, ResourceProp> {

	@Autowired
	@Qualifier("resourceServiceClient")
    protected ResourceDataService resourceDataService;
	
	@Override
	protected ResourceProp createAttribute(Resource t) {
		final ResourceProp attribute = new ResourceProp();
		attribute.setResourceId(t.getId());
		return attribute;
	}

	@Override
	protected Set<ResourceProp> createAttributeSet() {
		return new HashSet<>();
	}

	@Override
	protected void setAttributes(Resource t, Set<ResourceProp> attributes) {
		t.setResourceProps(attributes);
	}

	@Override
	protected Set<ResourceProp> getAttributes(Resource t) {
		return t.getResourceProps();
	}

	@Override
	protected Resource newInstance() {
		final Resource resource = new Resource();
		resource.setResourceType(resourceDataService.findResourceTypes(null, 0, 1, null).get(0));
		return resource;
	}

	@Override
	protected ResourceSearchBean newSearchBean() {
		return new ResourceSearchBean();
	}

	@Override
	protected Response save(Resource t) {
		return resourceDataService.saveResource(t, null);
	}

	@Override
	protected Response delete(Resource t) {
		return resourceDataService.deleteResource(t.getId(), null);
	}

	@Override
	protected Resource get(String key) {
		return resourceDataService.getResource(key, null);
	}

	@Override
	public List<Resource> find(ResourceSearchBean searchBean, int from, int size) {
		return resourceDataService.findBeans(searchBean, from, size, null);
	}

}
