package org.openiam.base.response;

import org.openiam.am.srvc.dto.jdbc.AuthorizationMenu;
import org.openiam.base.ws.Response;

/**
 * Created by alexander on 14/09/16.
 */
public class AuthorizationMenuResponse extends Response {
    private AuthorizationMenu menu;

    public AuthorizationMenu getMenu() {
        return menu;
    }

    public void setMenu(AuthorizationMenu menu) {
        this.menu = menu;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthorizationMenuResponse{");
        sb.append(super.toString());
        sb.append(", menu=").append(menu);
        sb.append('}');
        return sb.toString();
    }
}
