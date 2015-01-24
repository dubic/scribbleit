<%-- 
    Document   : new-header
    Created on : Jan 17, 2015, 1:19:05 PM
    Author     : dubem
--%>

<header ng-controller="headerCtrl">

    <div class="middle-page">
        <div class="row">
            <div class="col-md-3">
                <a class="" href="/scribbleit">Scribbles</a>
            </div>
            <div class="col-md-6">
                <div class="">
                    <input placeholder="Search for jokes,proverbs,quotes..." class="form-control" ng-class="{'spinner':searching}" ng-model="keyword" ng-change="search()" type="text"/>
                </div>
                <div class="search-resuts bordered pop full-width">
                    <div class="group" ng-repeat="g in searched">
                        <div class="head"><span ng-bind="g.name"></span><span class="pull-right badge badge-default" ng-bind="g.count|number"></span></div>
                        <a href="">
                            <div class="list" ng-repeat="l in g.lists">
                                <!--<div class="title">one bright summer</div>-->
                                <div class="body" ng-bind="l"></div>
                            </div>
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="pull-right">
                    <a href="#/login" class="margin-right-10">login</a>
                    <a href="#/signup">sign up</a>
                </div>
            </div>

        </div>


    </div>
</header>