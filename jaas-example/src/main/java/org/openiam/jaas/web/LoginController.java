package org.openiam.jaas.web;

import org.openiam.jaas.handler.DefaultCallbackHandler;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginController extends BaseController {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");


            DefaultCallbackHandler cbh = new DefaultCallbackHandler();
            cbh.setUserName(username);
            cbh.setPassword(password);
            LoginContext lc = new LoginContext("Jaas", cbh);
            lc.login();

            session.setAttribute(Constants.LOGIN_CONTEXT, lc);
            session.setAttribute(Constants.USER_SUBJECT, lc.getSubject());

            response.sendRedirect(request.getContextPath()+"/secure/info");

        } catch (LoginException e) {
            session.setAttribute(Constants.ERROR_MESSAGE, e.getMessage());
            response.sendRedirect(request.getContextPath()+"/login-error");
        }
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String jspPage = "/login.jsp";
        dispatch(jspPage, request, response);
    }

}
