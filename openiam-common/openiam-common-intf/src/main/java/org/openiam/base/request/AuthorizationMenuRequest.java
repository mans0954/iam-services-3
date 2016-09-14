package org.openiam.base.request;

import org.openiam.am.srvc.dto.jdbc.AuthorizationMenu;

/**
 * Created by alexander on 14/09/16.
 */
public class AuthorizationMenuRequest extends BaseServiceRequest {
    private AuthorizationMenu menu;

    public AuthorizationMenu getMenu() {
        return menu;
    }

    public void setMenu(AuthorizationMenu menu) {
        this.menu = menu;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthorizationMenuSaveRequest{");
        sb.append(super.toString());
        sb.append(", menu=").append(menu);
        sb.append('}');
        return sb.toString();
    }
}
