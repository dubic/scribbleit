///* 
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//Appl.factory('PostService', function($http) {
//    var factory = {};
//    var views = ['jokes', 'quotes', 'proverbs'];
//    factory.getPosts = function(view, start, size) {
//        return $http.get('/scribbleit/posts/' + views[view] + '/' + start + '/' + size);
//    };
//    factory.getPost = function(id, posts) {
//        for (var i = 0; i < posts.length; i++) {
////            console.log("id = " + id + ", post id = " + posts[i].id);
//            if (id === posts[i].id)
//                return posts[i];
//        }
//        return undefined;
//    };
//    factory.setPost = function(post, posts) {
//        for (var i = 0; i < posts.length; i++) {
//            if (post.id === posts[i].id)
//                posts[i] = post;
//        }
//    };
//    factory.like = function(view, id) {
//        return $http.get('/scribbleit/posts/' + views[view] + '/like/' + id);
//    };
//    factory.dislike = function(view, id) {
//        return $http.get('/scribbleit/posts/' + views[view] + '/dislike/' + id);
//    };
//    factory.loadComments = function(view, id) {
//        return $http.get('/scribbleit/posts/' + views[view] + '/comments/' + id);
//    };
//    factory.comment = function(view, id, text) {
//        return $http.post('/scribbleit/posts/' + views[view] + '/comment/' + id, text);
//    };
//    factory.report = function(view, id, text) {
//        return $http.post('/scribbleit/posts/' + views[view] + '/report/' + id, text);
//    };
//    factory.newpost = function(view, text) {
//        return $http.post('/scribbleit/posts/' + views[view] + '/new', text);
//    };
//    return factory;
//});
//
//Appl.controller('HomeController', function($scope, $http, PostService) {
//    
//    $scope.postContainer = {
//        loading: false,
//        liking: -1,
//        disliking: -1,
//        commenting: -1,
//        reporting: -1,
//        openReport: false,
//        view: -1,
//        posts: [[], [], []]
//    };
//    $scope.postContainer.newpost = '';
//
//    $scope.switchView = function(view) {
//        loadPosts(view);
//    };
//    $scope.like = function(id) {
//        $scope.postContainer.liking = id;
//        PostService.like($scope.postContainer.view, id).success(function(resp) {
//            $scope.postContainer.liking = -1;
//            PostService.setPost(resp, $scope.postContainer.posts[$scope.postContainer.view]);
//        });
//    };
//    $scope.dislike = function(id) {
//        $scope.postContainer.disliking = id;
//        PostService.dislike($scope.postContainer.view, id).success(function(resp) {
//            $scope.postContainer.disliking = -1;
//            PostService.setPost(resp, $scope.postContainer.posts[$scope.postContainer.view]);
//        });
//    };
//    $scope.loadComments = function(id) {
//        $scope.postContainer.commenting = id;
//        PostService.loadComments($scope.postContainer.view, id).success(function(resp) {
//            $scope.postContainer.commenting = -1;
//            var p = PostService.getPost(id, $scope.postContainer.posts[$scope.postContainer.view]);
//            p.comments = resp;
//            p.commentsLoaded = true;
//        });
//    };
//    $scope.comment = function(id) {
//        var p = PostService.getPost(id, $scope.postContainer.posts[$scope.postContainer.view]);
//        p.oncomment = true;
//        PostService.comment($scope.postContainer.view, id, p.myComment).success(function(resp) {
//            p.oncomment = false;
//            p.comments = resp;
//            p.commentsLoaded = true;
//        });
//    };
//    
//    $scope.newpost = function() {
//        $scope.newposting = true;
//        PostService.newpost($scope.postContainer.view, $scope.postContainer.newpost).success(function(resp) {
//            $scope.newposting = false;
//            $scope.postContainer.newpost = '';
////            $scope.postContainer.posts[$scope.postContainer.view];
//        });
//    };
//
//    $scope.report = function(id) {
//        var p = PostService.getPost(id, $scope.postContainer.posts[$scope.postContainer.view]);
//        var faults = [];
//        if(p.offensive)faults.push('offensive');
//        if(p.vulgar)faults.push('vulgar');
//        $scope.postContainer.reporting = id;
//        PostService.report($scope.postContainer.view, id, faults).success(function(resp) {
//            $scope.postContainer.reporting = -1;
//        });
//    };
//
//
//
//    ///////INIT FUNCTIONS///////////
//    loadPosts(0);
/////////////////////////////
//    function loadPosts(view) {
//        $scope.postContainer.loading = true;
////        it('should be true',function(){
////            expect(view).toEqual(0); 
////        });
//        PostService.getPosts(view, 0, 10).success(function(resp) {
//            $scope.postContainer.loading = false;//hide loading..
//            $scope.postContainer.posts[view] = resp;//display jokes
//            $scope.postContainer.view = view;//switch view
//        });
//    }
//
//});
//
