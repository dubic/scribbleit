<%-- 
    Document   : provberbs
    Created on : Jan 20, 2015, 1:47:40 PM
    Author     : dubem
--%>
<section class="post-container" ng-repeat="prov in proverbs">
    <div class="row">
        <div class="col-md-1">
            <img src="{{imagePath}}/{{prov.imageURL}}" class="poster-img" width="60" height="60"/>
        </div>
        <div  class="col-md-10">
            <div><a href="#/profile/{{prov.poster}}" class="person" ng-bind="prov.poster"></a></div>
            <div><span class="date-grey">{{prov.duration}}</span></div>
            <div class="stats">
                <i class="icon-thumbs-up"> <span class="likes" ng-bind="prov.likes"></span></i>
                <i class="icon-comment"> <span ng-bind="prov.commentsLength"></span></i>
                <i class="icon-tags"><span ng-repeat="t in prov.tags"> <a href="#/proverbs/tags/{{t}}">&num;{{t}}</a></span></i>
            </div>
        </div>
    </div>
    <!--MAIN POST MESSAGE-->
    <div class="post">
        <p ng-bind="prov.post"></p>
    </div>
    <!--ACTION BAR-->
    <div class="action-bar">
        <ul class="list-inline">
            <li><a href="" ng-hide="prov.liking || prov.liked" ng-click="like($index)">Like</a>
            <li><a href="" ng-hide="prov.liking || !prov.liked" ng-click="unlike($index)">Unlike</a>
                <img ng-show="prov.liking" src="{{spinner}}"/>
            </li>
            <li><a href="" ng-click="prov.makeComment = !prov.makeComment">Comment</a></li>
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
        <span class="pull-right" style="position: relative" ng-hide="prov.oncomment || prov.commentsLength === 0">
            <a href="" ng-click="loadComments($index)" ng-hide="prov.showComments">show comments</a>
            <a href="" ng-click="hideComments($index)" ng-hide="!prov.showComments">hide comments</a>
            <img ng-show="prov.loadingComments" src="{{spinner}}"/>
        </span>
        <div ng-cloak ng-show="prov.showComments">
            <div class="row comment" ng-repeat="comment in prov.comments">
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
    <div class="my-comment" collapse="!prov.makeComment">
        <div class="row"><div class="col-md-12"><textarea ng-model="prov.myComment" style="width: 100%" placeholder="comment on this joke"></textarea></div></div>
        <div class="pull-right margin-top-10">
            <button class="btn btn-warning btn-sm" ng-click="prov.makeComment = !prov.makeComment" ng-hide="prov.oncomment">cancel</button>
            <button class="btn btn-info btn-sm" ng-click="comment($index)" ng-hide="prov.oncomment">comment</button>
            <span ng-show="prov.oncomment"><img src="{{spinner}}"/> saving comment...</span>
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
                    <div class="well well-sm" ng-bind="selectedProverb.post"></div>
                    <ul class="list-unstyled rep-checklist">
                        <li><input type="checkbox" jq-uniform value="Offensive" ng-model="reports.offensive"/>Offensive</li>
                        <li><input type="checkbox" jq-uniform value="Vulgar" ng-model="reports.vulgar"/>Vulgar</li>
                        <li><input type="checkbox" jq-uniform value="Salacious" ng-model="reports.salacious"/>Salacious</li>
                    </ul>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-warning btn-sm" data-dismiss="modal">cancel</button>
                    <button class="btn btn-info btn-sm" ng-disabled="selectedProverb.reporting" ng-click="report()">
                        <span ng-hide="selectedProverb.reporting">submit</span>
                        <span ng-show="selectedProverb.reporting">saving</span>
                    </button>
                </div>

            </div>
        </div>
    </div>
    <!--END OF LOGIN MODAL BOX-->
</section>
