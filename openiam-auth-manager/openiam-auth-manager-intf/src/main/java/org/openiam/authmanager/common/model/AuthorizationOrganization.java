package org.openiam.authmanager.common.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.openiam.authmanager.common.xref.AbstractGroupXref;
import org.openiam.authmanager.common.xref.AbstractOrgXref;
import org.openiam.authmanager.common.xref.AbstractResourceXref;
import org.openiam.authmanager.common.xref.AbstractRoleXref;
import org.openiam.authmanager.common.xref.OrgGroupXref;
import org.openiam.authmanager.common.xref.OrgOrgXref;
import org.openiam.authmanager.common.xref.OrgResourceXref;
import org.openiam.authmanager.common.xref.OrgRoleXref;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationOrganization", propOrder = {
})
public class AuthorizationOrganization extends AbstractAuthorizationEntity implements Serializable  {

	private String adminResourceId;
	
	@XmlTransient
	private Set<OrgOrgXref> parentOrganizations = null;
	
	@XmlTransient
	private Set<OrgRoleXref> roles = null;
	
	@XmlTransient
	private Set<OrgResourceXref> resources = null;
	
	@XmlTransient
	private Set<OrgGroupXref> groups = null;
	
	private AuthorizationOrganization() {}
	
	public AuthorizationOrganization(final OrganizationEntity entity, final int bitIdx) {
		super.setBitSetIdx(bitIdx);
		super.setDescription(entity.getDescription());
		super.setId(entity.getId());
		super.setName(entity.getName());
		super.setStatus(entity.getStatus());
		this.adminResourceId = (entity.getAdminResource() != null) ? entity.getAdminResource().getId() : null;
	}
	
	public String getAdminResourceId() {
		return adminResourceId;
	}

	public void setAdminResourceId(String adminResourceId) {
		this.adminResourceId = adminResourceId;
	}
	
	public Set<AbstractResourceXref> getResources() {
		Set<AbstractResourceXref> retval = null;
		if(resources != null) {
			retval = new HashSet<AbstractResourceXref>(resources);
		}
		return retval;
	}
	
	public Set<AbstractGroupXref> getGroups() {
		Set<AbstractGroupXref> retval = null;
		if(groups != null) {
			retval = new HashSet<AbstractGroupXref>(groups);
		}
		return retval;
	}
	
	public Set<AbstractRoleXref> getRoles() {
		Set<AbstractRoleXref> retval = null;
		if(roles != null) {
			retval = new HashSet<AbstractRoleXref>(roles);
		}
		return retval;
	}
	
	public void addParentOrganization(final OrgOrgXref organization) {
		if(parentOrganizations == null) {
			parentOrganizations = new HashSet<OrgOrgXref>();
		}
		parentOrganizations.add(organization);
	}
	
	public void addRole(final OrgRoleXref role) {
		if(roles == null) {
			roles = new HashSet<OrgRoleXref>();
		}
		roles.add(role);
	}
	
	public void addGroup(final OrgGroupXref group) {
		if(groups == null) {
			groups = new HashSet<OrgGroupXref>();
		}
		groups.add(group);
	}
	
	public void addResource(final OrgResourceXref resource) {
		if(resources == null) {
			resources = new HashSet<OrgResourceXref>();
		}
		resources.add(resource);
	}
	
	@Override
	public void compile() {
		
	}

	public AuthorizationOrganization shallowCopy() {
		final AuthorizationOrganization copy = new AuthorizationOrganization();
		super.makeCopy(copy);
		return copy;
	}
	
	public Set<AbstractOrgXref> visitOrganizations(final Set<AuthorizationOrganization> visitedSet) {
		final Set<AbstractOrgXref> compiledList = new HashSet<AbstractOrgXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(parentOrganizations != null) {
				for(final AbstractOrgXref xref : parentOrganizations) {
					compiledList.add(xref);
					compiledList.addAll(xref.getOrganization().visitOrganizations(visitedSet));
				}
			}
		}
		return compiledList;
	}
	
	public Set<AbstractRoleXref> visitRoles(final Set<AuthorizationOrganization> visitedSet) {
		final Set<AbstractRoleXref> compiledList = new HashSet<AbstractRoleXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			
			if(parentOrganizations != null) {
				parentOrganizations.forEach(xref -> {
					compiledList.addAll(xref.getOrganization().visitRoles(visitedSet));
				});
			}
			
			if(roles != null) {
				roles.forEach(xref -> {
					compiledList.add(xref);
					compiledList.addAll(xref.getRole().visitRoles(new HashSet<AuthorizationRole>()));
				});
			}
		}
		return compiledList;
	}
	
	public Set<AbstractGroupXref> visitGroups(final Set<AuthorizationOrganization> visitedSet) {
		final Set<AbstractGroupXref> compiledList = new HashSet<AbstractGroupXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			
			if(parentOrganizations != null) {
				parentOrganizations.forEach(xref -> {
					compiledList.addAll(xref.getOrganization().visitGroups(visitedSet));
				});
			}
			
			if(roles != null) {
				roles.forEach(xref -> {
					compiledList.addAll(xref.getRole().visitGroups(new HashSet<AuthorizationRole>()));
				});
			}
			
			if(groups != null) {
				groups.forEach(xref -> {
					compiledList.add(xref);
					compiledList.addAll(xref.getGroup().visitGroups(new HashSet<AuthorizationGroup>()));
				});
			}
		}
		return compiledList;
	}
	
	public Set<AbstractResourceXref> visitResources(final Set<AuthorizationOrganization> visitedSet) {
		final Set<AbstractResourceXref> compiledList = new HashSet<AbstractResourceXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			
			if(parentOrganizations != null) {
				parentOrganizations.forEach(xref -> {
					compiledList.addAll(xref.getOrganization().visitResources(visitedSet));
				});
			}
			
			if(roles != null) {
				roles.forEach(xref -> {
					compiledList.addAll(xref.getRole().visitResources(new HashSet<AuthorizationRole>()));
				});
			}
			
			if(groups != null) {
				groups.forEach(xref -> {
					compiledList.addAll(xref.getGroup().visitResources(new HashSet<AuthorizationGroup>()));
				});
			}
			
			if(resources != null) {
				resources.forEach(xref -> {
					compiledList.add(xref);
					compiledList.addAll(xref.getResource().visitResources(new HashSet<AuthorizationResource>()));
				});
			}
		}
		return compiledList;
	}
}
