package org.openiam.authmanager.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationResource", propOrder = {
        "resourceTypeId",
        "inheritFromParent",
        "isPublic",
        "risk"
})
public class AuthorizationResource extends AbstractAuthorizationEntity implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	@XmlTransient
	private Set<AuthorizationResource> parentResources;

    private String resourceTypeId;

	private boolean inheritFromParent = true;
	
	private boolean isPublic = false;

    private String risk;
	
	/*
	private BitSet linearBitSet = new BitSet();
	*/
	
	public AuthorizationResource() {
		
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

	public void addParentResoruce(final AuthorizationResource resource) {
		if(parentResources == null) {
			parentResources = new HashSet<AuthorizationResource>();
		}
		parentResources.add(resource);
	}

    public String getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(String resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public Set<AuthorizationResource> getParentResources() {
		return parentResources;
	}

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
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
	
	public Set<AuthorizationResource> visitResources(final Set<AuthorizationResource> visitedSet) {
		final Set<AuthorizationResource> compiledResourceBitSet = new HashSet<AuthorizationResource>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(inheritFromParent) {
				if(parentResources != null) {
					for(final AuthorizationResource parent : parentResources) {
						compiledResourceBitSet.add(parent);
						compiledResourceBitSet.addAll(parent.visitResources(visitedSet));
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
