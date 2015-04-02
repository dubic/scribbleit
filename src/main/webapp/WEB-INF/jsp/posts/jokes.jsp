<%-- 
    Document   : jokes
    Created on : Sep 27, 2014, 9:47:38 AM
    Author     : dubem
--%>

<section class="post-container" ng-repeat="joke in jokes">
    <div class="row title">
        <span ng-bind="joke.title"></span>
    </div>
    <div class="row">
        <div class="col-md-1">
            <img src="{{imagePath}}/{{joke.imageURL}}" class="poster-img" width="60" height="60"/>
        </div>
        <div  class="col-md-10">
            <div><a href="#/profile/{{joke.poster}}" class="person" ng-bind="joke.poster"></a></div>
            <div><span class="date-grey">{{joke.duration}}</span></div>
            <div class="stats">
                <i class="icon-thumbs-up"> <span class="likes" ng-bind="joke.likes"></span></i>
                <i class="icon-comment"> <span ng-bind="joke.commentsLength"></span></i>
                <i class="icon-tags"><span ng-repeat="t in joke.tags"> <a href="#/jokes/tags/{{t}}">&num;{{t}}</a></span></i>
            </div>
        </div>
    </div>
    <!--MAIN POST MESSAGE-->
    <div class="post">
        <p ng-bind="joke.post"></p>
    </div>
    <!--ACTION BAR-->
    <div class="action-bar">
        <ul class="list-inline">
            <li><a href="" ng-hide="joke.liking || joke.liked" ng-click="like($index)">Like</a>
            <li><a href="" ng-hide="joke.liking || !joke.liked" ng-click="unlike($index)">Unlike</a>
                <img ng-show="joke.liking" src="{{spinner}}"/>
            </li>
            <li><a href="" ng-click="joke.makeComment = !joke.makeComment">Comment</a></li>
            <li>
                <a href="#">Share</a>
            </li>
            <li>
                <a href="" ng-click="openReport($index)">Report</a>
            </li>
        </ul>
    </div>
    <!--COMMENTS-->
    <div class="row comments">
        <span class="pull-right" style="position: relative" ng-hide="joke.oncomment || joke.commentsLength === 0">
            <a href="" ng-click="loadComments($index)" ng-hide="joke.showComments">show comments</a>
            <a href="" ng-click="hideComments($index)" ng-hide="!joke.showComments">hide comments</a>
            <img ng-show="joke.loadingComments" src="{{spinner}}"/>
        </span>
        <div class="col-md-10 col-lg-offset-1" ng-cloak ng-show="joke.showComments">
            <div class="row comment" ng-repeat="comment in joke.comments">
                <div class="col-md-1">
                    <img src="{{imagePath}}/{{comment.imageURL}}" class="poster-img" width="40" height="40"/>
                </div>
                <div class="col-md-9">
                    <div class="row">
                        <a href="#" class="person">{{comment.poster}}</a>
                        <span class="date-grey" style="margin:0px 10px">{{comment.duration}}</span>
                    </div>
                    <div class="row post">
                        <p>{{comment.text}}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!--COMMENT TEXT BLOCK-->
    <div class="my-comment" collapse="!joke.makeComment">
        <div class="row">
            <div class="col-md-10 col-lg-offset-1"><textarea ng-model="joke.myComment" style="width: 100%" placeholder="comment on this joke"></textarea></div>
        </div>
        <div class="row">
            <div class="col-md-10 col-lg-offset-1 margin-top-10">
                <button class="btn btn-warning btn-sm" ng-click="joke.makeComment = !joke.makeComment" ng-hide="joke.oncomment">cancel</button>
                <button class="btn btn-info btn-sm" ng-click="comment($index)" ng-hide="joke.oncomment">comment</button>
                <span ng-show="joke.oncomment"><img src="{{spinner}}"/> saving comment...</span>
            </div>
        </div>
    </div>


    <!--LOGIN MODAL BOX-->
    <div class="modal fade"  id="reportModal" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
        <div class="modal-dialog" style="min-width: 40%">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
                    <h4 class="modal-title" id="myModalLabel">Report inappropriate content </h4>
                </div>
                <div class="modal-body">
                    <div ng-repeat="a in repAlerts" class="alert {{a.class}}">{{a.msg}}</div>
                    <div class="well well-sm" ng-bind="selectedJoke.post"></div>
                    <ul class="list-unstyled rep-checklist">
                        <li><input type="checkbox" jq-uniform value="Offensive" ng-model="reports.offensive"/>Offensive</li>
                        <li><input type="checkbox" jq-uniform value="Vulgar" ng-model="reports.vulgar"/>Vulgar</li>
                        <li><input type="checkbox" jq-uniform value="Salacious" ng-model="reports.salacious"/>Salacious</li>
                    </ul>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-warning btn-sm" data-dismiss="modal">cancel</button>
                    <button class="btn btn-info btn-sm" ng-disabled="selectedJoke.reporting" ng-click="report()">
                        <span ng-hide="selectedJoke.reporting">submit</span>
                        <span ng-show="selectedJoke.reporting">saving</span>
                    </button>
                </div>

            </div>
        </div>
    </div>
    <!--END OF LOGIN MODAL BOX-->
</section>
