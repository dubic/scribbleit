<%-- 
    Document   : account
    Created on : Jan 12, 2015, 1:11:33 PM
    Author     : dubem
--%>
<div class="account">
    <div class="row edit-row">
        <div class="col-md-2 label-col"><label>First Name</label></div>
        <div class="col-md-4">
            <span ng-bind="Account.firstname" ng-show="editing !== 1"></span>
            <div class="row" ng-show="editing === 1">
                <div class="col-md-8"><input type="text" class="form-control" ng-model="Account.firstname"/></div>
                <div class="col-md-1"><button class="btn btn-default" ng-click="editing = -1"><i class="icon-remove"></i></button></div>
            </div>
        </div>
        <div class="col-md-2"><i class="icon-edit edit-icons" ng-click="editing = 1" title="edit"></i></div>
    </div>
    <!--LASTNAME-->
    <div class="row edit-row">
        <div class="col-md-2 label-col"><label>Last Name</label></div>
        <div class="col-md-4">
            <span ng-bind="Account.lastname" ng-show="editing !== 2"></span>
            <div class="row" ng-show="editing === 2">
                <div class="col-md-8"><input type="text" class="form-control" ng-model="Account.lastname"/></div>
                <div class="col-md-1"><button class="btn btn-default" ng-click="editing = -2"><i class="icon-remove"></i></button></div>
            </div>
        </div>
        <div class="col-md-2"><i class="icon-edit edit-icons" ng-click="editing = 2" title="edit"></i></div>
    </div>
    <!--EMAIL-->
    <div class="row edit-row">
        <div class="col-md-2 label-col"><label>Email</label></div>
        <div class="col-md-4"><span ng-bind="Account.email"></span></div>
    </div>
    <!--SCREEN NAME-->
    <div class="row edit-row">
        <div class="col-md-2 label-col"><label>Screen Name</label></div>
        <div class="col-md-4"><span ng-bind="Account.screenName"></span></div>
    </div>
    <!--SCREEN NAME-->
    <div class="row edit-row">
        <div class="col-md-2 label-col"><label>Date Joined</label></div>
        <div class="col-md-4"><span ng-bind="Account.date"></span></div>
    </div>
</div>
