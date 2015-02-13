<%-- 
    Document   : activation-outcome
    Created on : Feb 8, 2015, 10:20:52 AM
    Author     : dubem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Scribbles</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jsp:include page="include/head.jsp"/>
    </head>
    <body>
        <header>
            <div class="middle-page" >
                <div class="row">
                    <div class="col-md-3">
                        <a class="" href="/scribbleit">
                            <img src="/scribbleit/resources/images/logo_hollow.png" style="height: 54px;margin-top: -15px;" alt="Scribbles"/>
                        </a>

                    </div>
                    <div class="col-md-6 pop-container">

                    </div>
                    <div class="col-md-3">
                        <!--<button class="btn" ng-click="testDialog()">close</button>-->
                        <div class="pull-right">
                            <a href="/scribbleit/#/login" class="margin-right-10">login</a>
                            <a href="/scribbleit/#/signup">sign up</a>
                        </div>
                    </div>
                </div>
            </div>
        </header>
        
        <%
            boolean lerr = (Boolean) request.getAttribute("linkError");
            boolean lexp = (Boolean) request.getAttribute("linkExpired");
            boolean err = (Boolean) request.getAttribute("error");
        %>
        <div class="middle-page">
            <div class="row">
                <div class="col-md-12 well well-lg margin-top-10">
                    <%if (lerr) {%>
                    <p>The Link you clicked does not exist</p>
                         
                    <%}%>

                    <%if (lexp) {%>
                    <p>The Link you clicked has expired</p>
                    
                    <%}%>
                    <%if (err == true && lerr == false && lexp == false) {%>
                    <p>Service unavailable. please try again later.</p>
                    
                    <%}%>

                    <%if (err == false) {%>
                    <p>Welcome to Scribbles. your account has been activated. you can proceed to login</p>
                    <%}%>
                </div>
            </div>
        </div>
    </body>
    <footer>
        <jsp:include page="include/foot.jsp"/>
    </footer>
    <!--    <script>
                        $(document).mouseup(function(e) {
                            var container = $(".pop-container");
    
                            if (!container.is(e.target) // if the target of the click isn't the container...
                                    && container.has(e.target).length === 0) // ... nor a descendant of the container
                            {
                                $(".pop").slideUp();
                            }
                        });
        </script>-->
</html>
