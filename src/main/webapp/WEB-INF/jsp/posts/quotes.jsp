<%-- 
    Document   : quotes
    Created on : Feb 3, 2015, 10:08:05 PM
    Author     : dubem
--%>

<section class="post-container" ng-repeat="quote in quotes">
    <div class="row">
        <div class="col-md-1">
            <img src="{{imagePath}}/{{quote.imageURL}}" class="poster-img" width="60" height="60"/>
        </div>
        <div  class="col-md-11">
            
            <blockquote>
                <div class="quote-person"><a href="#/profile/{{quote.poster}}" class="person" ng-bind="quote.poster"></a>
                    on <span class="date-grey" ng-bind="quote.duration"></span>
                </div>
                <p ng-bind="quote.post"></p>
                <small ng-bind="quote.source"></small>
            </blockquote>
        </div>
    </div>
    <!--MAIN POST MESSAGE-->
    <!--    <div class="post">
            <p ng-bind="quote.post"></p>
        </div>-->
    <!--ACTION BAR-->
    <div class="action-bar">
        <ul class="list-inline">
            <li><a href="" ng-hide="quote.liking || quote.liked" ng-click="like($index)">Like</a>
            <li><a href="" ng-hide="quote.liking || !quote.liked" ng-click="unlike($index)">Unlike</a>
                <img ng-show="quote.liking" src="{{spinner}}"/>
            </li>
            <li><a href="" ng-click="quote.makeComment = !quote.makeComment">Comment</a></li>
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
        <span class="pull-right" style="position: relative" ng-hide="quote.oncomment || quote.commentsLength === 0">
            <a href="" ng-click="loadComments($index)" ng-hide="quote.showComments">show comments</a>
            <a href="" ng-click="hideComments($index)" ng-hide="!quote.showComments">hide comments</a>
            <img ng-show="quote.loadingComments" src="{{spinner}}"/>
        </span>
        <div ng-cloak ng-show="quote.showComments">
            <div class="row comment" ng-repeat="comment in quote.comments">
                <div class="col-md-1">
                    <img src="{{comment.imageURL}}" class="poster-img" width="40" height="40"/>
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
    <div class="my-comment" collapse="!quote.makeComment">
        <div class="row"><div class="col-md-12"><textarea ng-model="quote.myComment" style="width: 100%" placeholder="comment on this joke"></textarea></div></div>
        <div class="pull-right margin-top-10">
            <button class="btn btn-warning btn-sm" ng-click="quote.makeComment = !quote.makeComment" ng-hide="quote.oncomment">cancel</button>
            <button class="btn btn-info btn-sm" ng-click="comment($index)" ng-hide="quote.oncomment">comment</button>
            <span ng-show="quote.oncomment"><img src="{{spinner}}"/> saving comment...</span>
        </div>
        <div class="clearfix"></div>
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
                    <div class="well well-sm" ng-bind="selectedQuote.post"></div>
                    <ul class="list-unstyled rep-checklist">
                        <li><input type="checkbox" jq-uniform value="Offensive" ng-model="reports.offensive"/>Offensive</li>
                        <li><input type="checkbox" jq-uniform value="Vulgar" ng-model="reports.vulgar"/>Vulgar</li>
                        <li><input type="checkbox" jq-uniform value="Salacious" ng-model="reports.salacious"/>Salacious</li>
                    </ul>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-warning btn-sm" data-dismiss="modal">cancel</button>
                    <button class="btn btn-info btn-sm" ng-disabled="selectedQuote.reporting" ng-click="report()">
                        <span ng-hide="selectedQuote.reporting">submit</span>
                        <span ng-show="selectedQuote.reporting">saving</span>
                    </button>
                </div>

            </div>
        </div>
    </div>
    <!--END OF LOGIN MODAL BOX-->
</section>
