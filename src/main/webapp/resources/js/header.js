jQuery(document).ready(function() {
    App.init();
//load first post tab = jokes

});

//var Appl = angular.module('AppHome', ['remoteValidation', 'InputMatch','ui.bootstrap']);

$("#loginModal").on("show.bs.modal", function(e) {
    $(".forgot-password-cont").hide();
    $(".register-cont").hide();
    $(".login-cont").show();
});
$("#forget-password").click(function() {
    Deffects.transitionFade($(".login-cont"), $(".forgot-password-cont"), 300);
    
});
$("#register-btn").click(function() {
    Deffects.transitionFade($(".login-cont"), $(".register-cont"), 300);
});
$("#back-btn").click(function() {
    Deffects.transitionFade($(".forgot-password-cont"), $(".login-cont"), 300);
});
$("#register-back-btn").click(function() {
    Deffects.transitionFade($(".register-cont"), $(".login-cont"), 300);
});


function verifyPassword() {
    var pword = $('#password');
    var vpword = $('#vpassword');

    if (vpword !== pword) {

    }
}

function reload(test) {
    if (test === true) {
        location.href = "/scribbleit/home.jsf";
    }
}

//////////////////////////////////////////


