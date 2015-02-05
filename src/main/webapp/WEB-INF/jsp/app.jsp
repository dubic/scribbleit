<%-- 
    Document   : app
    Created on : Jan 6, 2015, 3:48:09 PM
    Author     : dubem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html ng-app="AppHome">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jsp:include page="include/head.jsp"/>
    </head>
    <body>
        <jsp:include page="include/new-header.jsp"/>

        <div id="page-notif" style="top:142px" >

            <div style="text-align: center" class="margin-bottom-10">
                <div style="display: inline-block;background: #b9cbd5;padding: 15px;box-shadow: 0px 2px 35px 2px #263138;" ng-show="loading" ng-bind="loadingMsg"></div>
            </div>
            <div style="text-align: center">
                <div style="display: inline-block;background: #b9cbd5;padding: 15px;box-shadow: 0px 2px 35px 2px #263138;" ng-show="notif" ng-bind="notif"></div>
            </div>
        </div>

        <div ui-view></div>
    </body>
    <footer>
        <jsp:include page="include/foot.jsp"/>
        <script src="/scribbleit/resources/js/home/app.js" type="text/javascript"></script> 
        <script src="/scribbleit/resources/js/home/services.js" type="text/javascript"></script> 

        <script src="/scribbleit/resources/js/home/controllers/reg-ctrl.js" type="text/javascript"></script> 
        <script src="/scribbleit/resources/js/home/controllers/header-ctrl.js" type="text/javascript"></script> 
        <script src="/scribbleit/resources/js/home/controllers/main-ctrl.js" type="text/javascript"></script> 
        <script src="/scribbleit/resources/js/home/controllers/jokes-ctrl.js" type="text/javascript"></script> 
        <script src="/scribbleit/resources/js/home/controllers/profile-ctrl.js" type="text/javascript"></script> 
        <script src="/scribbleit/resources/js/home/controllers/prov-ctrl.js" type="text/javascript"></script> 
        <script src="/scribbleit/resources/js/home/controllers/quotes-ctrl.js" type="text/javascript"></script> 
        <script src="/scribbleit/resources/js/home/controllers.js" type="text/javascript"></script> 

        <!--<script src="/scribbleit/resources/js/home.js" type="text/javascript"></script>--> 
    </footer>
    <script>
                    $(document).mouseup(function(e) {
                        var container = $(".pop-container");

                        if (!container.is(e.target) // if the target of the click isn't the container...
                                && container.has(e.target).length === 0) // ... nor a descendant of the container
                        {
                            $(".pop").slideUp();
                        }
                    });
    </script>
</html>
