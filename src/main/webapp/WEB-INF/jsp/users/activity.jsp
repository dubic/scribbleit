<%-- 
    Document   : activity
    Created on : Jan 11, 2015, 6:35:42 PM
    Author     : dubem
--%>

<div class="activity">
    <h4>Posts Activity</h4>
    <div class="posts">
        <div class="row" ng-repeat="p in Activity.posts">
            <div class="col-md-12 well well-sm">
                <div style="border-bottom: 1px #c0c0c0 solid" class="margin-bottom-10"><span class="date bold margin-right-10" ng-bind="p.date"></span>
                    <span class="title" ng-bind="p.title">one bright summer morning</span></div>
                <div class="text" ng-bind="p.text"></div>
                <a href="" ng-show="p.readMore">Read More</a>
            </div>
        </div>
    </div>
</div>
