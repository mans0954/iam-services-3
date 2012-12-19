<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<h1>Warning: Authorised Users Only</h1>
<form action="login" method="post">
    <table>
        <tr>
            <td>User Name</td>
            <td><input type="text" name="username"></td>
        </tr>
        <tr>
            <td>Password</td>
            <td><input type="password" name="password"></td>
        </tr>
        <tr>
            <td align="right">
                <input type="submit" value="login">
            </td>
            <td align="left">
                <input type="reset">
            </td>
        </tr>
    </table>
</form>
</body>
</html>