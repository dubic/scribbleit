/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$(function() {
    App.blockUI(".middle-page");
    $.getJSON("/scribbleit/api/posts/jokes/0/10", function(posts) {
        for (var i = 0; i < posts.length; i++) {
            var post = createJoke(posts[i]);
            $('#post-list').append(post);
        }
        App.unblockUI(".middle-page");
        ///////////////POST CREATION EVENTS////////////////////////////////////////
        registerJokeEvents();
    });
});

function createJoke(post) {
    var postContainer = $('<section class="post-container"></section>')
            .append($('<div class="row"></div>')
                    .append($('<div class="col-md-1"></div>')
                            .append($('<img class="poster-img" width="60" height="60"/>').attr('src', post.imageURL)))
                    .append($('<div  class="col-md-10"></div>')
                            .append($('<div class="row"></div>').append($('<a class="person"></a>').attr('href', 'profile.jsf?name=' + post.poster).text(post.poster)))
                            .append($('<div class="row"></div>').append($('<span class="date-grey"></span>').text(post.duration)))
                            .append($('<div class="row stats"></div>')
                                    .append($('<i class="icon-thumbs-up"></i>')).append($('<span class="likes"></span>').text(post.likes))
                                    .append($('<i class="icon-thumbs-down"></i>')).append($('<span class="dislikes"></span>').text(post.dislikes))
                                    .append($('<i class="icon-comments"></i>')).append($('<span></span>').text(post.comments.length))
                                    )));
    postContainer.append($('<div class="row post"></div>').append($('<p></p>').text(post.post)));
    //////////////ACTIONS BAR AND POP UPS//////////////////////
    var sharePop = $('<div class="dpopover"><div class="pop-head"><span>Share Options</span><i class="icon-remove" title="close"></i></div><div class="pop-body"><input class="form-text" type="text"/><button class="btn green small">show</button></div></div>');
    var shareLink = $('<a href="#">Share</a>').click(function(evt) {
        openShareOptions(evt, $(this));
    });
    var reportPop = createReportPop(post.id);
    var reportLink = $('<a href="#" class="report-action">Report</a>');
    var likeLink = $('<a href="#">Like</a><img class="c_loader" style="display:none" src="/scribbleit/resources/images/spinner.gif"/>').click(function(evt) {
        like(evt, $(this), post.id);
    });
    var dislikeLink = $('<a href="#">Dislike</a><img class="c_loader" style="display:none" src="/scribbleit/resources/images/spinner.gif"/>').click(function(evt) {
        dislike(evt, $(this), post.id);
    });
    var commentLink = $('<a href="#">Comment</a>').click(function(evt) {
        showMyComment(evt, $(this));
    });

    postContainer.append($('<div class="row action-bar"></div>')
            .append($('<ul class="list-inline"></ul>')
                    .append($('<li></li>').append(likeLink))
                    .append($('<li></li>').append(dislikeLink))
                    .append($('<li></li>').append(commentLink))
                    .append($('<li></li>').append(shareLink).append(sharePop))
                    .append($('<li></li>').append(reportLink).append(reportPop))));
    /////////////////HIDE COMMENTS LINK//////////////////////////
    var hideCommLink = $('<span class="pull-right" style="position: relative"></span>').append($('<a href="#">show comments</a>').click(function(evt) {
        hideComments(evt, $(this));
    }));
    //////////////////APPEND COMMENTS//////////////////////
    var commentsBody = $('<div class="comments-body"></div>');
    $.each(post.comments, function() {
        commentsBody.append(createCommentBlock(this));
    });

    postContainer.append($('<div class="row comments"></div>').append(hideCommLink).append(commentsBody));
    ///////////////MY COMMENT//////////////////////////////

    var postCommentBtn = $('<button class="btn blue" onclick="postComment($(this),' + post.id + ')">comment</button>');
    var myComment = $('<div class="row my-comment" style="display:none"></div>')
            .append($('<div class="row"><textarea style="width: 100%" placeholder="comment on this joke"></textarea></div>'))
            .append($('<div class="pull-right"></div>')
                    .append(postCommentBtn)
                    .append($('<span class="ajax-loader"><img title="c_loader.gif" src="assets/img/loading.gif"/></span>')));

    postContainer.append(myComment);

    return postContainer;
}

function registerJokeEvents() {
    $(".dpopover .icon-remove").click(function(evt) {
        $(this).closest(".dpopover").fadeOut(300);
    });
    $(".report-submit").click(function() {
        var boxes = $(this).parent().find(".checker .checked");
        boxes.each(function() {
            console.log($(this).closest('li').text());
//              alert($(this).find('input[type="checkbox"]').text()); 
        });
    });
    $("select, input:checkbox, input:radio, input:file").uniform();
    $("#post-list .report-action").click(function(evt) {
        $(this).siblings(".dpopover").fadeToggle(300);
        evt.preventDefault();
    });
}

function openShareOptions(evt, source) {
    source.parent().find(".dpopover").fadeToggle(300);
    evt.preventDefault();
}
//LIKE ACTION
function like(evt, source, postid) {
    Deffects.transitionFade(source, source.parent().find(".c_loader"), 0);
    $.post('/scribbleit/api/posts/jokes/like/' + postid, function(data) {
        if (data.likes !== -1) {
            var likes = source.closest(".post-container").find(".likes");
            likes.text(data.likes);
        }
    }).always(function() {
        Deffects.transitionFade(source.parent().find(".c_loader"), source, 0);
    });
    evt.preventDefault();
}
//DISLIKE ACTION
function dislike(evt, source, postid) {
    Deffects.transitionFade(source, source.parent().find(".c_loader"), 0);
    $.post('/scribbleit/api/posts/jokes/dislike/' + postid, function(data) {
        if (data.dislikes !== -1) {
            var dislikes = source.closest(".post-container").find(".dislikes");
            dislikes.text(data.dislikes);
        }
    }).always(function() {
        Deffects.transitionFade(source.parent().find(".c_loader"), source, 0);
    });
    evt.preventDefault();
}
//////////////SHOW COMMENT TEXTAREA//////////////////////
function showMyComment(evt, source) {
    source.closest(".post-container").find(".my-comment").slideDown(800).focus(true);
    evt.preventDefault();
}
function hideComments(evt, source) {
    source.closest(".post-container").find(".comments-body").slideToggle();
    if (source.hasClass("active")) {
        source.text("show comments");
        source.removeClass("active");
    } else {
        source.text("hide comments");
        source.addClass("active");
    }
    evt.preventDefault();
}
//////////////POST A COMMENT ACTION//////////////////////
function postComment(source, postId) {
    var loader = source.closest('.my-comment').find('.ajax-loader');
    var comment = source.closest('.my-comment').find('textarea').val();
//    alert("post id = " + postId + ", comment = " + comment);
    if (comment === undefined || comment === "") {
        return;
    }
    //save comment to db
    Deffects.transitionFade(source, loader, 0);
    $.post('/scribbleit/api/posts/jokes/comment/' + postId + "?comment=" + comment, function(data) {
        source.closest(".post-container").find(".comments-body").append(createCommentBlock(data));
        source.closest('.my-comment').find('textarea').val("");
    }).always(function() {
        Deffects.transitionFade(loader, source, 500);
    });

}
///////////////////CREATE COMMENT BLOCK//////////////////
function createCommentBlock(comment) {
    var cblock = $('<div class="row comment"></div>')
            .append($('<div class="col-md-1"></div>')
                    .append($('<img class="poster-img" width="40" height="40"/>').attr('src', comment.imageURL)))
            .append($('<div class="col-md-9"></div>')
                    .append($('<div class="row"></div>')
                            .append($('<a class="person"></a>').attr('href', 'profile.jsp').text(comment.poster))
                            .append($('<span class="date-grey" style="margin:0px 10px"></span>').text(comment.duration)))
                    .append($('<div class="row post"></div>').append($('<p></p>').text(comment.text))));
    return cblock;
}
///////////////////CREATE COMMENT BLOCK//////////////////
function createReportPop(postid) {
    var reportBtn = $('<button class="btn btn-info report-submit pull-right">submit</button>').click(function() {
//        alert('post '+postid +" reported");
        var ul = $(this).closest('.post-container').find('ul.rep-checklist');
        var reasons = [];
        ul.find('input[type="checkbox"]:checked').each(function() {
            reasons.push($(this).val());
        });
        if (reasons.length > 0) {
//            $.ajax({
//                type: "post",
//                url: '/scribbleit/api/posts/jokes/report/' + postid,
//                dataType: "json",
//                data: {"reasons": JSON.stringify(["vulgar","salacious"])},
//                success: function(r) {
//                    alert(r.dislikes);
//                },
//                error: function(r) {
//                    alert(r.statusText);
//                }
//            });
            $.post('/scribbleit/api/posts/jokes/report/' + postid+"?reasons="+reasons.join(","), function(data){
                alert(data.dislikes);
            },'json');
        }
    });
    var rpop = $('<div class="dpopover"></div>')
            .append($('<div class="pop-head"><span>Report Post</span><i class="icon-remove" title="close"></i></div>'))
            .append($('<div class="pop-body"></div>')
                    .append($('<ul class="list-unstyled rep-checklist"></ul>')
                            .append('<li><input type="checkbox" value="Offensive">Offensive</li>')
                            .append('<li><input type="checkbox" value="Vulgar">Vulgar</li>')
                            .append('<li><input type="checkbox" value="Salacious">Salacious</li>'))
                    .append($('<div class="row"></div>').append(reportBtn))
                    );


    return rpop;
}
//////////////////CALLBACK FOR POSTED JOKE///////////////////////////////
function updatePosted(post) {
    $('#post-list').prepend(createJoke(post));
    registerJokeEvents();
}

