package org.openiam.jaas.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ErrorController extends BaseController {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
          doGet(request,response);
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String jspPage = "/login-error.jsp";

        HttpSession session = request.getSession(true);
        String errorMessage = (String)session.getAttribute(Constants.ERROR_MESSAGE);
        if(errorMessage!=null && !errorMessage.isEmpty()){
            if("RESULT_INVALID_LOGIN".equals(errorMessage)){
                errorMessage="Invalid principal name";
            }  else if("RESULT_INVALID_PASSWORD".equals(errorMessage)){
                errorMessage="Password is invalid";
            }  else if("RESULT_INVALID_USER_STATUS".equals(errorMessage)){
                errorMessage="User is not active";
            }  else if("RESULT_LOGIN_DISABLED".equals(errorMessage)){
                errorMessage="Account is disabled";
            }  else if("RESULT_LOGIN_LOCKED".equals(errorMessage)){
                errorMessage="Account is blocked";
            }  else if("RESULT_PASSWORD_EXPIRED".equals(errorMessage)){
                errorMessage="Password is expired";
            }  else if("RESULT_INVALID_TOKEN".equals(errorMessage)){
                errorMessage="Invalid token";
            }  else  {
                errorMessage="Service error. Please try again later";
            }
        }
        request.setAttribute(Constants.ERROR_MESSAGE, errorMessage);
        dispatch(jspPage, request, response);
    }


}
