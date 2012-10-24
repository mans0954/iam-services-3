package org.openiam.authmanager.common.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.openiam.authmanager.util.AuthorizationConstants;
import org.openiam.idm.srvc.res.dto.ResourceProp;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationMenu", propOrder = {
        "id",
        "name",
        "url",
        "displayName",
        "displayOrder",
        "isPublic",
        "firstChild",
        "nextSibling"
})
public class AuthorizationMenu implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String url;
	private String displayName;
	private Integer displayOrder;
	private boolean isPublic = false;
	
	/* XMLTransient b/c otherwould would cause infinite loop */
	@XmlTransient
	private AuthorizationMenu parent;
	private AuthorizationMenu firstChild;
	private AuthorizationMenu nextSibling;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public AuthorizationMenu getParent() {
		return parent;
	}
	
	public void setParent(AuthorizationMenu parent) {
		this.parent = parent;
	}
	
	public AuthorizationMenu getFirstChild() {
		return firstChild;
	}
	
	public void setFirstChild(AuthorizationMenu firstChild) {
		this.firstChild = firstChild;
	}
	
	public AuthorizationMenu getNextSibling() {
		return nextSibling;
	}
	
	public void setNextSibling(AuthorizationMenu nextSibling) {
		this.nextSibling = nextSibling;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public void afterPropertiesSet(final List<ResourceProp> resourcePropertyList) {
		if(resourcePropertyList != null) {
			for(final ResourceProp prop : resourcePropertyList) {
				if(displayName == null && 
				   StringUtils.equalsIgnoreCase(prop.getName(), AuthorizationConstants.MENU_ITEM_DISPLAY_NAME_PROPERTY)) {
					displayName = StringUtils.trimToNull(prop.getPropValue());
				}
				if(isPublic == false && 
				   StringUtils.equalsIgnoreCase(prop.getName(), AuthorizationConstants.MENU_ITEM_IS_PUBLIC_PROPERTY) &&  
				   StringUtils.equalsIgnoreCase(Boolean.TRUE.toString(), prop.getPropValue())) {
					isPublic = true;
				}
			}
		}
	}
	
	public boolean getIsPublic() {
		return isPublic;
	}

	@Override
	public String toString() {
		return String
				.format("AuthorizationMenu [id=%s, name=%s, url=%s, displayName=%s, isPublic: %s, parent=%s, firstChild=%s, nextSibling=%s]",
						id, name, url, displayName, isPublic,
						(parent != null) ? parent.getId() : null, 
						(firstChild != null) ? firstChild.getId() : null, 
						(nextSibling != null) ? nextSibling.getId() : null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthorizationMenu other = (AuthorizationMenu) obj;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (displayOrder == null) {
			if (other.displayOrder != null)
				return false;
		} else if (!displayOrder.equals(other.displayOrder))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		
		if(firstChild == null && other.firstChild != null) {
			return false;
		}
		if(firstChild != null && other.firstChild == null) {
			return false;
		}
		if(firstChild != null && other.firstChild != null && !firstChild.getId().equals(other.firstChild.getId())) {
			return false;
		}
		
		if(nextSibling == null && other.nextSibling != null) {
			return false;
		}
		if(nextSibling != null && other.nextSibling == null) {
			return false;
		}
		if(nextSibling != null && other.nextSibling != null && !nextSibling.getId().equals(other.nextSibling.getId())) {
			return false;
		}
		
		if(parent == null && other.parent != null) {
			return false;
		}
		if(parent != null && other.parent == null) {
			return false;
		}
		if(parent != null && other.parent != null && !parent.getId().equals(other.parent.getId())) {
			return false;
		}
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
	public String toString(final int level) {
		final String ls = System.getProperty("line.separator");
		String tab = ""; for(int i = 0; i < level; i++) { tab += "  "; }
		final StringBuilder sb = new StringBuilder();
		sb.append(tab).append("Level: ").append(level).append(ls);
		sb.append(tab).append("Menu: ").append(this).append(ls);
		
		if(getFirstChild() != null) {
			sb.append(getFirstChild().toString(level + 1));
		}
		
		if(getNextSibling() != null) {
			sb.append(getNextSibling().toString(level));
		}
		return sb.toString();
	}
	
	/* designed to avoid using Dozer reflection when getting a menu tree */
	public AuthorizationMenu copy() {
		final AuthorizationMenu menu = new AuthorizationMenu();
		menu.setId(getId());
		menu.setName(getName());
		menu.setDisplayOrder(getDisplayOrder());
		menu.setUrl(getUrl());
		menu.displayName = displayName;
		menu.isPublic = isPublic;
		return menu;
	}
}
