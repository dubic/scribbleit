/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
app.factory('services', function ($http, $timeout, $rootScope) {
    var factory = {};
    factory.openDialog = function (id) {
        $('#' + id).modal({backdrop: "static"});
    };
    factory.closeDialog = function (id) {
//        $timeout(function () {
        $('#' + id).modal('hide');
//        }, 1000);
    };

    factory.openModal = function (id) {
        $('#' + id).css('display', 'block').addClass('in');
        $('body').addClass('modal-open').append('<div class="modal-backdrop fade in"></div>');
        $('#' + id).find('.dlg-close').click(function () {
            $('#' + id).css('display', 'none').removeClass('in');
            $('body').removeClass('modal-open');
            $('.modal-backdrop').remove();
        });
    };

    factory.closeModal = function (id) {
        $('#' + id).css('display', 'none').removeClass('in');
        $('body').removeClass('modal-open');
        $('.modal-backdrop').remove();
    };

    factory.popLogin = function () {
        this.openModal('loginModal');
        $rootScope.$broadcast('login.modal.shown');
    };



    factory.notify = function (msg, dur) {
        $rootScope.notif = msg;
        var d = dur ? dur : 2000;
        $timeout(function () {
            $rootScope.notif = undefined;
        }, d);
    };
    factory.showMsg = function (msg) {
        $rootScope.loadingMsg = msg;
    };
    factory.hideMsg = function () {
        $rootScope.loadingMsg = undefined;
    };

    factory.buildAlerts = function (msgs) {
        var a = [];
        $.each(msgs, function (i, m) {
            if (m.code === 0)
                a.push({class: 'alert-success', msg: m.msg});
            else if (m.code !== 0)
                a.push({class: 'alert-danger', msg: m.msg});
        });
        return a;
    };

    factory.isAuthenticated = false;


    factory.table = function (callback) {
        var table = {
            pending: false,
            pages: [10, 25, 50, 100],
            columns: [],
            size: 10,
            page: 1,
            start: 0,
            total: 0,
            fetched: 0,
            filter: '',
            asearch: [],
            loadCall: function () {
                this.pending = true;
                callback();
            },
            init: function () {
                $.each(this.columns, function (i, col) {
                    if (col.sorting === true) {
                        col.class = 'sorting';
                        if (col.sort === 'asc' || col.sort === 'desc') {
                            col.class = 'sorting_' + col.sort;
                            table.sortcol = i;
                            table.sortasc = col.sort === 'asc';
                            return;
                        }
                    }
                });
                this.loadCall();
            },
            update: function (total, rows) {
                this.total = total;
                this.fetched = rows;
//                    this.asearch = [];
            },
            sort: function (col) {
                if (!this.columns[col].sorting)
                    return;
                if (this.sortcol === col)
                    this.sortasc = !this.sortasc;
                else {
                    this.sortasc = true;
                    this.unsortclass(this.sortcol);
                }
                this.sortcol = col;
                this.sortclass(col);
                this.loadCall();
            },
            unsortclass: function (col) {
                this.columns[col].class = 'sorting';
                this.columns[col].sort = '';
            },
            sortclass: function (col) {
                if (this.columns[col].sort === 'asc') {
                    this.columns[col].sort = 'desc';
                    this.columns[col].class = 'sorting_desc';
                } else {
                    this.columns[col].sort = 'asc';
                    this.columns[col].class = 'sorting_asc';
                }
            },
            search: function (searches) {
                var s = [];
                $.each(searches, function (i, search) {
                    if (search.value !== undefined && search.value !== '')
                        s.push(search);
                });
                this.asearch = s;
                console.log(this.asearch);
                this.loadCall();
            },
            clearSearch: function (searches) {
                $.each(searches, function (i, search) {
                    search.value = '';
                });
                this.asearch = [];
            },
            pageChanged: function () {
                table.start = (table.page - 1) * table.size;
                this.loadCall();
            }
        };
        return table;
    };
    return factory;
});

