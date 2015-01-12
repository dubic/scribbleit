<%-- 
    Document   : account
    Created on : Jan 12, 2015, 1:11:33 PM
    Author     : dubem
--%>
<div class="account">
    <div class="row edit-row">
        <div class="col-md-2 label-col"><label>First Name</label></div>
        <div class="col-md-4">
            <span ng-bind="user.firstname" ng-show="editing !== 1"></span>
            <div class="row" ng-show="editing === 1">
                <div class="col-md-8"><input type="text" class="form-control" ng-model="user.firstname"/></div>
                <div class="col-md-1"><button class="btn btn-default" ng-click="editing = -1"><i class="icon-remove"></i></button></div>
            </div>
        </div>
        <div class="col-md-2"><i class="icon-edit edit-icons" ng-click="editing = 1" title="edit"></i></div>
    </div>
    <!--LASTNAME-->
    <div class="row edit-row">
        <div class="col-md-2 label-col"><label>Last Name</label></div>
        <div class="col-md-4">
            <span ng-bind="user.lastname" ng-show="editing !== 2"></span>
            <div class="row" ng-show="editing === 2">
                <div class="col-md-8"><input type="text" class="form-control" ng-model="user.lastname"/></div>
                <div class="col-md-1"><button class="btn btn-default" ng-click="editing = -2"><i class="icon-remove"></i></button></div>
            </div>
        </div>
        <div class="col-md-2"><i class="icon-edit edit-icons" ng-click="editing = 2" title="edit"></i></div>
    </div>
    <!--EMAIL-->
    <div class="row edit-row">
        <div class="col-md-2 label-col"><label>Email</label></div>
        <div class="col-md-4"><span ng-bind="user.email"></span></div>
    </div>
</div>
