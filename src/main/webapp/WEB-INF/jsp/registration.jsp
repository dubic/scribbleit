<%-- 
    Document   : registration
    Created on : Dec 30, 2014, 10:19:23 AM
    Author     : dubem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Scribbles</title>
        <jsp:include page="include/head.jsp"/>
        <link href="assets/css/pages/login.css" rel="stylesheet" type="text/css"/>
    </head>
    <body class="login" ng-app="regApp">
        <div class="content" style="min-width: 40%;" ui-view>

        </div>
    </body>
    <footer>
        <jsp:include page="include/foot.jsp"/>
        <script src="/scribbleit/resources/js/reg/app.js" type="text/javascript"></script> 
        <script src="/scribbleit/resources/js/reg/services.js" type="text/javascript"></script> 
        <script src="/scribbleit/resources/js/reg/controllers.js" type="text/javascript"></script> 
    </footer>
</html>
