<%-- 
    Document   : login
    Created on : Dec 30, 2014, 11:10:14 AM
    Author     : dubem
--%>

<div class="login" style="min-width: 40%;margin-top: 42px;">
    <aside class="content">
        <form>
            <h3 class="form-title">Login to your account</h3>
            <div class="alert {{a.class}}" ng-repeat="a in alerts" ng-click="alerts.splice($index, 1)">{{a.msg}}</div>
            <div class="form-group">
                <!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
                <label class="control-label visible-ie8 visible-ie9">Username</label>
                <div class="input-icon">
                    <i class="icon-user"></i>
                    <!--<input class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="Username" name="username"/>-->
                    <input type="email" class="form-control" placeholder="email" name="lemail" autocomplete="off"/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label visible-ie8 visible-ie9">Password</label>
                <div class="input-icon">
                    <i class="icon-lock"></i>
                    <input type="password" class="form-control" placeholder="password" name="lpassword" />
                </div>
            </div>
            <div class="form-actions">
<!--                <label class="checkbox">
                    <input type="checkbox" name="remember" value="1"/> Remember me
                </label>-->
                <input type="submit" class="btn btn-info pull-right" value="Login" ng-click="login()"/>

                <span id="loginLdr" class="ajax-loader pull-right" style="padding: 10px 0px;"><img src="assets/img/loading.gif"/></span>
            </div>
            <div class="forget-password">
                <h4>Forgot your password ?</h4>
                <p>no worries, click <a href="#/forgot-password"  id="forget-password">here</a>
                    to reset your password.
                </p>
            </div>
            <div class="create-account">
                <p>
                    Don't have an account yet ?&nbsp; 
                    <a href="#/signup" id="register-btn" >Create an account</a>
                </p>
            </div>

        </form>
    </aside>
</div>