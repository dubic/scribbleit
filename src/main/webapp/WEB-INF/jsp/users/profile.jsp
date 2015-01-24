<%-- 
    Document   : profile
    Created on : Jan 10, 2015, 11:47:50 PM
    Author     : dubem
--%>

<div>
    <div class="middle-page-2">
        <div class="page-container profile" style="padding:20px">
            <div id="top-profile" class="row margin-bottom-10">
                <div class="col-md-3">
                    <div><img src="{{imagePath}}male.jpg" width="180" height="200"/></div>
                    <form enctype="multipart/form-data">
                        <div ng-if="Profile.isMe">
                            <input type="file"/>
                            <button class="btn btn-warning btn-sm">change pic</button>
                        </div>
                    </form>
                </div>
                <div class="col-md-9">
                    <div><h4 ng-bind="Profile.name"></h4></div>
                    <div class="row">
                        <div class="col-md-3">
                            <div class="dashboard-stat blue">
                                <div class="visual"></div>
                                <div class="details">
                                    <div class="number">{{Profile.jokes| number}}</div>
                                    <div class="desc">Jokes</div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="dashboard-stat blue">
                                <div class="visual">
                                    <!--                                    <i class="icon-comments"></i>-->
                                </div>
                                <div class="details">
                                    <div class="number">{{Profile.proverbs| number}}</div>
                                    <div class="desc">Proverbs</div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="dashboard-stat blue">
                                <div class="visual"></div>
                                <div class="details">
                                    <div class="number">{{Profile.quotes| number}}</div>
                                    <div class="desc">Quotes</div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
            <hr>
            <!--PROFILE BODY-->
            <div id="body-profile" class="row">
                <!--TAB LINKS COLUMN-->
                <div id="about_div" class="col-md-3">
                    <ul class="ver-inline-menu tabbable margin-bottom-10">
                        <li class="active">
                            <a data-toggle="tab" href="" ng-click="navigate('profile.activity')">
                                <i class="icon-cog"></i> 
                                Activity
                            </a> 
                            <span class="after"></span>                                    
                        </li>
                        <li class=""><a data-toggle="tab" href="" ng-click="navigate('profile.account')"><i class="icon-picture"></i> Account</a></li>
                        <li class=""><a data-toggle="tab" href="" ng-click="navigate('profile.pword')"><i class="icon-lock"></i> Change Password</a></li>
                    </ul>
                </div>
                <!--TAB CONTENTS-->
                <div class="col-md-9">
                    <ui-view></ui-view>
                </div>

            </div>
        </div>

    </div>
</div>
