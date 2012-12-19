<%--
  Created by IntelliJ IDEA.
  User: alexander
  Date: 19.12.12
  Time: 0:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
<head>
    <title></title>
</head>
<body>
<h1>${requestScope.userName}, welcome to secure zone</h1>

<h2>User roles:</h2>

    <c:choose>
        <c:when test="${! empty requestScope.userRoles}">
            <ul>
            <c:forEach var="role" items="${requestScope.userRoles}">
                <li>${role.name}</li>
            </c:forEach>
            </ul>
        </c:when>
        <c:otherwise>
            <h3>There are no role assigned to user.</h3>
        </c:otherwise>
    </c:choose>

</body>
</html>