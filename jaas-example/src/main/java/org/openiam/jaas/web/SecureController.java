package org.openiam.jaas.web;

import org.openiam.jaas.group.UserRoleGroup;
import org.openiam.jaas.principal.UserIdentity;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class SecureController extends BaseController {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String jspPage = "/secure/info.jsp";
        HttpSession session = request.getSession(true);

        Subject subject  = (Subject)session.getAttribute(Constants.USER_SUBJECT);

        if(subject!=null){
            Set<UserRoleGroup> userRoles = subject.getPrincipals(UserRoleGroup.class);
            request.setAttribute("userRoles", userRoles);

            Iterator it = subject.getPrincipals(UserIdentity.class).iterator();
            if(it.hasNext()) {
                UserIdentity p = (UserIdentity)it.next();
                request.setAttribute("userName", p.getName());
            }
        }

        dispatch(jspPage, request, response);
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException  {
        doPost(request, response);
    }

}
