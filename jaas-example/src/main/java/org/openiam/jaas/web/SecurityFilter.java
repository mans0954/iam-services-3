package org.openiam.jaas.web;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SecurityFilter implements Filter {


    public void init(FilterConfig config){
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest){
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            HttpSession session = httpRequest.getSession(true);

            if(httpRequest.getRequestedSessionId() != null
               && httpRequest.isRequestedSessionIdValid() && session.getAttribute(Constants.USER_SUBJECT)!=null){
                chain.doFilter(request, response);
            } else {
                LoginContext lc = (LoginContext) session.getAttribute(Constants.LOGIN_CONTEXT);

                if(lc!=null){
                    try {
                        lc.logout();
                    } catch (LoginException e) {
                        e.printStackTrace();
                    }
                }

                session.removeAttribute(Constants.USER_SUBJECT);
                session.removeAttribute(Constants.LOGIN_CONTEXT);
                session.invalidate();
               ((HttpServletResponse)response).sendRedirect(httpRequest.getContextPath()+"/login");
            }
        }
    }

    public void destroy() {
        // do nothing
    }
}
