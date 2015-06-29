package org.openiam.authmanager.common.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.openiam.authmanager.common.xref.AbstractResourceXref;
import org.openiam.authmanager.common.xref.ResourceResourceXref;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationResource", propOrder = {
        "resourceTypeId",
        "inheritFromParent",
        "isPublic",
        "risk",
        "adminResourceId",
        "coorelatedName"
})
public class AuthorizationResource extends AbstractAuthorizationEntity implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	@XmlTransient
	private Set<ResourceResourceXref> parentResources;

    private String resourceTypeId;

	private boolean inheritFromParent = true;
	
	private boolean isPublic = false;

    private String risk;

    private String adminResourceId;

    private String coorelatedName;
	/*
	private BitSet linearBitSet = new BitSet();
	*/
	
	private AuthorizationResource() {
		
	}
	
	public AuthorizationResource(final AuthorizationMenu menu) {
		super.setId(menu.getId());
	}
	
	public AuthorizationResource(final ResourceEntity entity, final int bitIdx) {
		super.setBitSetIdx(bitIdx);
		super.setDescription(entity.getDescription());
		super.setId(entity.getId());
		super.setName(entity.getName());
		this.adminResourceId = (entity.getAdminResource() != null) ? entity.getAdminResource().getId() : null;
		this.isPublic = entity.getIsPublic();
		this.coorelatedName = entity.getCoorelatedName();
		this.risk = (entity.getRisk() != null) ? entity.getRisk().name() : null;
		this.resourceTypeId = (entity.getResourceType() != null) ? entity.getResourceType().getId() : null;
	}
		
	public boolean isInheritFromParent() {
		return inheritFromParent;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void addParentResoruce(final ResourceResourceXref entity) {
		if(parentResources == null) {
			parentResources = new HashSet<ResourceResourceXref>();
		}
		parentResources.add(entity);
	}

    public String getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(String resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getAdminResourceId() {
        return adminResourceId;
    }

    public void setAdminResourceId(String adminResourceId) {
        this.adminResourceId = adminResourceId;
    }

    public String getCoorelatedName() {
        return coorelatedName;
    }

    public void setCoorelatedName(String coorelatedName) {
        this.coorelatedName = coorelatedName;
    }

    /**
	 * Compiles this Resource against it's Resource Membership
	 */
	@Override
	public void compile() {
		/*
		final Set<Resource> compiledSet = visitResources(new HashSet<Resource>());
		for(final Resource resource : compiledSet) {
			linearBitSet.set(new Integer(resource.getBitSetIdx()));
		}
		*/
	}
	
	public Set<AbstractResourceXref> visitResources(final Set<AuthorizationResource> visitedSet) {
		final Set<AbstractResourceXref> compiledResourceBitSet = new HashSet<AbstractResourceXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(inheritFromParent) {
				if(parentResources != null) {
					for(final ResourceResourceXref xref : parentResources) {
						compiledResourceBitSet.add(xref);
						compiledResourceBitSet.addAll(xref.getResource().visitResources(visitedSet));
					}
				}
			}
		}
		return compiledResourceBitSet;
	}
	
	public AuthorizationResource shallowCopy() {
		final AuthorizationResource copy = new AuthorizationResource();
		super.makeCopy(copy);
		return copy;
	}
}
