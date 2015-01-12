<%-- 
    Document   : home
    Created on : Sep 10, 2014, 4:11:12 PM
    Author     : dubem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="b" uri="/WEB-INF/tlds/dubic.tld"%>

<div style="margin-top: 42px;">
    <div class="middle-page" ng-controller="mainCtrl">
        <!--<button class="btn btn-danger" ng-click="dialog()">open</button>-->
        <div class="row">
            <ul class="horizontal post-links" style="margin: auto;display: table">
                <li><a href="" ng-click="testDialog()" class="active">Jokes</a></li>
                <li><a href="" ng-click="switchView(1)">Quotes</a></li>
                <li><a href="" ng-click="switchView(2)">Proverbs</a></li>
            </ul>
        </div>

        <section class="container row">
            <div class="col-lg-8" style="margin: auto">   
                <input placeholder="Search..." class="form-control" type="text"/>
            </div>
            <div class="col-md-4">
                <button type="button" class="btn btn-default">Search</button>
                <button class="btn btn-default" ng-click="openpost = !openpost">share a joke</button>
            </div>
        </section>
        <!--POST TEXTAREA-->
        <div collapse="!openpost" style="border: 1px #ddd solid;padding: 10px;">
            <div style="max-width: 70%">
                <div ng-repeat="a in alerts" class="alert {{a.class}}">{{a.msg}}</div>
                <div class="row">
                    <div class="col-md-12"><input class="form-control" ng-model="Post.title" placeholder="Title"/></div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-md-12"><textarea name="postxt" class="form-control" ng-model="Post.msg" placeholder="what's so funny" style="height: 130px"></textarea></div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-md-12">
                        <label>Tags:</label>
                        <input multiple tag-cloud="tagcloud" ng-model="Post.tags" data-placeholder="tag this post" style="min-width: 300px"/>
                    </div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-md-12">
                        <button type="button" ng-disabled="newposting" ng-click="Post.msg = '';
                                openpost = !openpost;" class="btn btn-sm btn-warning">cancel</button>
                        <button type="submit" ng-click="newpost()" ng-disabled="frm.$invalid || newposting" class="btn btn-sm btn-info">
                            <span ng-show="!newposting">post</span><span ng-show="newposting">saving</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <section class="container">
            <div ui-view></div>


        </section>
    </div>
</div>

