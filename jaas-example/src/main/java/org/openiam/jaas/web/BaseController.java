package org.openiam.jaas.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BaseController extends HttpServlet {

    protected void dispatch(String jsp, HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException {
        if (jsp != null) {
            RequestDispatcher rd = request.getRequestDispatcher(jsp);
            rd.forward(request, response);
        }
    }
}
