<%-- 
    Document   : home
    Created on : Sep 10, 2014, 4:11:12 PM
    Author     : dubem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="b" uri="/WEB-INF/tlds/dubic.tld"%>

<div>
    <div class="middle-page" ng-controller="mainCtrl">
        <!--<button class="btn btn-danger" ng-click="dialog()">open</button>-->
        <div class="row">
            <div class="col-md-12 margin-top-10">
                <section class="post-bar">
                    <ul class="nav nav-pills">
                        <li role="presentation" ng-class="{active : activePage === 'home.jokes'}" class="bold"><a href="" ng-click="route('home.jokes')">Jokes</a></li>
                        <li role="presentation" ng-class="{active : activePage === 'home.quotes'}" class="bold"><a href="" ng-click="route('home.quotes')">Quotes</a></li>
                        <li role="presentation" ng-class="{active : activePage === 'home.proverbs'}" class="bold"><a href=""  ng-click="route('home.proverbs')">Proverbs</a></li>
                        <li class="navbar-right bold"><a href="" style="margin-right: 10px" ng-click="openpost = !openpost">post new</a></li>
                    </ul>
                    
                    
                </section>


            </div>
        </div>

        
        <!--POST TEXTAREA-->
        <div collapse="!openpost" style="border: 1px #ddd solid;padding: 10px;">
            <div style="max-width: 70%">

                <div class="row" ng-show="activePage === 'home.jokes'">
                    <div class="col-md-12"><input class="form-control" ng-model="Post.title" placeholder="Title"/></div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-md-12"><textarea name="postxt" class="form-control" ng-model="Post.msg" placeholder="Post Message" style="height: 130px"></textarea></div>
                </div>
                <div class="row margin-top-10" ng-hide="activePage === 'home.quotes'">
                    <div class="col-md-12">
                        <label>Tags:</label>
                        <input multiple tag-cloud="tagcloud" ng-model="Post.tags" data-placeholder="tag this post" style="min-width: 300px"/>
                    </div>
                </div>
                <div class="row margin-top-10" ng-show="activePage === 'home.quotes'">
                    <div class="col-md-12"><input class="form-control" ng-model="Post.source" placeholder="Source"/></div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-md-12">
                        <button type="button" ng-disabled="newposting" ng-click="Post.msg = '';
                                openpost = !openpost;" class="btn btn-sm btn-warning">cancel</button>
                        <button type="submit" ng-show="Post.msg" ng-click="newpost()" ng-disabled="frm.$invalid || newposting" class="btn btn-sm btn-info">
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

