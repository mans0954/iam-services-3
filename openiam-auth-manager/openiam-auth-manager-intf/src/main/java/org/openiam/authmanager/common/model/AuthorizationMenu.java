package org.openiam.authmanager.common.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.openiam.authmanager.model.MenuEntitlementType;
import org.openiam.authmanager.util.AuthorizationConstants;
import org.openiam.idm.srvc.res.dto.ResourceProp;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationMenu", propOrder = {
        "id",
        "name",
        "url",
        "displayName",
        "displayOrder",
        "icon",
        "isPublic",
        "firstChild",
        "nextSibling",
        "entitlementTypeList",
        "isVisible"
})
public class AuthorizationMenu implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("url")
	private String url;
	
	@JsonProperty("displayName")
	private String displayName;
	
	@JsonProperty("order")
	private Integer displayOrder;
	
	@JsonProperty("icon")
	private String icon;
	
	@JsonProperty("isPublic")
	private boolean isPublic = false;
	
	@JsonProperty("visible")
	private boolean isVisible = true;
	
	/* XMLTransient b/c otherwould would cause infinite loop */
	@XmlTransient
	@JsonIgnore
	private AuthorizationMenu parent;
	
	@JsonProperty("firstChild")
	private AuthorizationMenu firstChild;
	
	@JsonProperty("nextSibling")
	private AuthorizationMenu nextSibling;
	
	/* this property's sole purpose is informational - it should NOT be stored on the backend */
	@JsonProperty("urlParams")
	@XmlTransient
	private String urlParams;
	
	@JsonProperty("entitlementType")
	private Set<MenuEntitlementType> entitlementTypeList;
	
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
	
	public String getIcon() {
		return icon;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getUrlParams() {
		return urlParams;
	}

	public void setUrlParams(String urlParams) {
		this.urlParams = urlParams;
	}
	
	public Set<MenuEntitlementType> getEntitlementTypeList() {
		return entitlementTypeList;
	}

	public void afterPropertiesSet(final List<ResourceProp> resourcePropertyList) {
		if(resourcePropertyList != null) {
			for(final ResourceProp prop : resourcePropertyList) {
				if(displayName == null && 
				   StringUtils.equalsIgnoreCase(prop.getName(), AuthorizationConstants.MENU_ITEM_DISPLAY_NAME_PROPERTY)) {
					displayName = StringUtils.trimToNull(prop.getPropValue());
				}
				if(icon == null && StringUtils.equalsIgnoreCase(prop.getName(), AuthorizationConstants.MENU_ITEM_ICON_PROPERTY)) {
					icon = StringUtils.trimToNull(prop.getPropValue());
				}
				
				if(StringUtils.equals(prop.getName(), AuthorizationConstants.MENU_ITEM_IS_VISIBLE)) {
					isVisible = StringUtils.equalsIgnoreCase("true", prop.getPropValue());
				}
			}
		}
	}
	
	public boolean getIsVisible() {
		return isVisible;
	}
	
	public boolean getIsPublic() {
		return isPublic;
	}
	
	public void setIsPublic(final boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	public void addEntitlementType(final MenuEntitlementType entitlementType) {
		if(entitlementType != null) {
			if(entitlementTypeList == null) {
				this.entitlementTypeList = new HashSet<MenuEntitlementType>();
			}
			this.entitlementTypeList.add(entitlementType);
		}
	}

	@Override
	public String toString() {
		return String
				.format("AuthorizationMenu [id=%s, name=%s, url=%s, displayName=%s, icon: %s, isPublic: %s, parent=%s, firstChild=%s, nextSibling=%s]",
						id, name, url, displayName, icon, isPublic,
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
		if(firstChild != null && other.firstChild != null) {
			if(firstChild.getId() != null && other.firstChild.getId() == null) {
				return false;
			} else if(firstChild.getId() == null && other.firstChild.getId() != null) {
				return false;
			} else if(!firstChild.getId().equals(other.firstChild.getId())) {
				return false;
			}
		}
		
		if(nextSibling == null && other.nextSibling != null) {
			return false;
		}
		if(nextSibling != null && other.nextSibling == null) {
			return false;
		}
		if(nextSibling != null && other.nextSibling != null) {
			if(nextSibling.getId() != null && other.nextSibling.getId() == null) {
				return false;
			} else if(nextSibling.getId() == null && other.nextSibling.getId() != null) {
				return false;
			} else if(!nextSibling.getId().equals(other.nextSibling.getId())) {
				return false;
			}
		}
		
		if(parent == null && other.parent != null) {
			return false;
		}
		if(parent != null && other.parent == null) {
			return false;
		}
		if(parent != null && other.parent != null) {
			if(parent.getId() != null && other.parent.getId() == null) {
				return false;
			} else if(parent.getId() == null && other.parent.getId() != null) {
				return false;
			} else if(!parent.getId().equals(other.parent.getId())) {
				return false;
			}
		}
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;

		if (icon == null) {
			if (other.icon != null)
				return false;
		} else if (!icon.equals(other.icon))
			return false;
		
		if(isPublic != other.isPublic) {
			return false;
		}
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
		menu.isVisible = isVisible;
		return menu;
	}
}
