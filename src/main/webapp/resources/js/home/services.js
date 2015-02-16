/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
app.factory('services', function($http,$timeout) {
    var factory = {};
    factory.openDialog = function(id){
        $('#'+id).modal({backdrop : "static"});
    };
    factory.closeDialog = function(id){
        $('#'+id).modal('hide');
    };
    
    
    var views = ['jokes', 'quotes', 'proverbs'];
    
    
    factory.notify = function(msg,scope) {
        scope.notif = msg;
        $timeout(function() {
                scope.notif = undefined;
            },2000);
    };
    factory.buildAlerts = function(msgs) {
        var a = [];
        $.each(msgs, function(i, m) {
            if (m.code === 0)
                a.push({class: 'alert-success', msg: m.msg});
            else if (m.code !== 0)
                a.push({class: 'alert-danger', msg: m.msg});
        });
        return a;
    };
    
    factory.isAuthenticated = false;
    

    factory.table = function(callback) {
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
            loadCall: function() {
                this.pending = true;
                callback();
            },
            init: function() {
                $.each(this.columns, function(i, col) {
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
            update: function(total, rows) {
                this.total = total;
                this.fetched = rows;
//                    this.asearch = [];
            },
            sort: function(col) {
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
            unsortclass: function(col) {
                this.columns[col].class = 'sorting';
                this.columns[col].sort = '';
            },
            sortclass: function(col) {
                if (this.columns[col].sort === 'asc') {
                    this.columns[col].sort = 'desc';
                    this.columns[col].class = 'sorting_desc';
                } else {
                    this.columns[col].sort = 'asc';
                    this.columns[col].class = 'sorting_asc';
                }
            },
            search: function(searches) {
                var s = [];
                $.each(searches, function(i, search) {
                    if (search.value !== undefined && search.value !== '')
                        s.push(search);
                });
                this.asearch = s;
                console.log(this.asearch);
                this.loadCall();
            },
            clearSearch: function(searches) {
                $.each(searches, function(i, search) {
                    search.value = '';
                });
                this.asearch = [];
            },
            pageChanged: function() {
                table.start = (table.page - 1) * table.size;
                this.loadCall();
            }
        };
        return table;
    };
    return factory;
});

