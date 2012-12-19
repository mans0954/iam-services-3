package org.openiam.jaas.web;

import org.openiam.jaas.handler.DefaultCallbackHandler;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginController extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException
    {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");


            DefaultCallbackHandler cbh = new DefaultCallbackHandler();
            cbh.setUserName(username);
            cbh.setPassword(password);
            LoginContext lc = new LoginContext("Jaas", cbh);
            lc.login();

            HttpSession session = request.getSession(true);

            session.setAttribute("loginContext", lc);
            session.setAttribute("userSubject", lc.getSubject());

            response.sendRedirect(request.getContextPath()+"/secure/info");

        } catch (LoginException e) {
            response.sendRedirect(request.getContextPath()+"/login-error");
        }
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String jspPage = "/login.jsp";
        dispatch(jspPage, request, response);
    }

    protected void dispatch(String jsp, HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException {
        if (jsp != null) {
            RequestDispatcher rd = request.getRequestDispatcher(jsp);
            rd.forward(request, response);
        }
    }
}
