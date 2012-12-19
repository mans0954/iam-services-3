package org.openiam.jaas.web;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SecurityFilter implements Filter {

    private Map _roster = new HashMap();

    public void init(FilterConfig config){
        _roster.put("staff1", "Monday");
        _roster.put("staff2", "Tuesday");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest){
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            HttpSession session = httpRequest.getSession(true);

            if(httpRequest.getRequestedSessionId() != null
               && httpRequest.isRequestedSessionIdValid() && session.getAttribute("userSubject")!=null){
                chain.doFilter(request, response);
            } else {
                LoginContext lc = (LoginContext) session.getAttribute("loginContext");

                if(lc!=null){
                    try {
                        lc.logout();
                    } catch (LoginException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                session.removeAttribute("userSubject");
                session.removeAttribute("loginContext");
                session.invalidate();
               ((HttpServletResponse)response).sendRedirect(httpRequest.getContextPath()+"/login");
            }
        }
    }

    public void destroy() {
        // do nothing
    }
}
