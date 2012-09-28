package org.openiam.idm.srvc.menu.dto;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PermissionId", propOrder = {
        "menuId",
        "roleId"
})
@Embeddable
public class PermissionId implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2388899255222778128L;
    private String menuId;
    private String roleId;

    public PermissionId() {

    }

    public PermissionId(String menuId, String roleId) {
        this.menuId = menuId;
        this.roleId = roleId;
    }

    @Column(name="MENU_ID",length=32,nullable=false)
    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    @Column(name="ROLE_ID",length=32,nullable=false)
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((menuId == null) ? 0 : menuId.hashCode());
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
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
		PermissionId other = (PermissionId) obj;
		if (menuId == null) {
			if (other.menuId != null)
				return false;
		} else if (!menuId.equals(other.menuId))
			return false;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("PermissionId [menuId=%s, roleId=%s]", menuId,
				roleId);
	}

    
}
