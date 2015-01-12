<%-- 
    Document   : forgot-password
    Created on : Dec 30, 2014, 11:42:26 AM
    Author     : dubem
--%>

<div class="login" style="min-width: 40%;margin-top: 42px;">
    <aside class="content">
        <form  name="emailform">
            <h3 >Forget Password ?</h3>
            <p>Enter your e-mail address below to reset your password.</p>
            <div class="alert {{a.class}}" ng-repeat="a in alerts">{{a.msg}}</div>
            <div class="form-group">
                <label class="control-label visible-ie8 visible-ie9">Email</label>
                <span class="error-msg" ng-show="emailform.email.$dirty && emailform.email.$error.required">Email is required</span>
                <span class="error-msg" ng-show="emailform.email.$dirty && emailform.email.$error.email">Invalid email</span>
                <div class="input-icon">
                    <i class="icon-user"></i>
                    <input type="email" class="form-control" placeholder="email" name="email" autocomplete="off" required ng-model="User.email"/>
                </div>
            </div>
            <div class="form-actions" ng-hide="!isCollapsed">
                <button id="back-btn" class="btn btn-danger" ng-click="back()">cancel</button>
                <button type="button" class="btn green pull-right" ng-click="forgetToken()" ng-disabled="emailform.$invalid || loading" >
                    <span ng-hide="loading">Submit</span><span ng-show="loading">Saving...</span>
                </button>            
            </div>
        </form>
        <!--================================================================================================-->
        <form name="pwform" collapse="isCollapsed">
            <div class="form-group">
                <label class="control-label visible-ie8 visible-ie9">Passcode</label>
                <span class="error-msg" ng-show="pwform.code.$dirty && pwform.code.$error.required">Enter passcode</span>
                <div class="input-icon">
                    <i class="icon-lock"></i>
                    <input class="form-control" autocomplete="off" type="text" name="code" placeholder="passcode" required ng-model="User.passcode"
                           tooltip="Enter passcode sent to your email"  tooltip-trigger="focus" tooltip-placement="top">
                </div>
            </div>
            <div class="form-group">
                <label class="control-label visible-ie8 visible-ie9">New Password</label>
                <span class="error-msg" ng-show="pwform.npword.$dirty && pwform.npword.$error.required">Enter a new password</span>
                <div class="input-icon">
                    <i class="icon-lock"></i>
                    <input class="form-control" autocomplete="off" type="password" name="npword" placeholder="new password" required ng-model="User.password">
                </div>
            </div>
            <div class="form-group">
                <label class="control-label visible-ie8 visible-ie9">Re-type New Password</label>
                <span class="error-msg" ng-show="pwform.vpword.$error.match">Passwords do not match</span>
                <div class="input-icon">
                    <i class="icon-lock"></i>
                    <input class="form-control" autocomplete="off" type="password" name="vpword" placeholder="retype password" required ng-model="User.confirmPassword" match="User.password">
                </div>
            </div>
            <div class="margin-top-10 form-actions">
                <button type="button" id="back-btn" class="btn" ng-click="isCollapsed = true"> cancel</button>
                <button class="btn green pull-right" ng-click="resetPassword()" ng-disabled="loading || pwform.$invalid">
                    <span ng-hide="loading">reset password</span><span ng-show="loading">saving...</span>
                </button>
            </div>
        </form>
    </aside>
</div>