/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.utils.IdmUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Dubic
 */
@Controller
@RequestMapping("/search")
public class SearchController {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private Database db;

    @RequestMapping("/results/posts/{type}")
    public @ResponseBody
    JsonObject searchJokes(
            @PathVariable("type") String type,
            @RequestParam("q") String keyword,
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        JsonObject resp = new JsonObject();
        //search snippets
        String sql = "select p.id,p.post,p.title from posts p \n"
                + "where p.type='"+type+"' \n"
                + "and (p.title like '%" + keyword + "%' or p.post like '%" + keyword + "%' or p.tags like '%" + keyword + "%') \n"
                + "and p.blocked=false";
        
        List<Object[]> searchSnippets = db.createNativeQuery(sql).setFirstResult(start).setMaxResults(size).getResultList();
        JsonArray snips = new JsonArray();
        for (Object[] snippet : searchSnippets) {
            JsonObject s = new JsonObject();
            String title = (String) snippet[2];
            if (IdmUtils.isEmpty(title)) {
                s.addProperty("text", (String) snippet[1]);
            }else{
                s.addProperty("text", title);
            }
            s.addProperty("id", (Long) snippet[0]);
            snips.add(s);
        }
        resp.add("posts", snips);
        //count
        String countsql = "select count(p.id) from posts p where p.type='"+type+"' \n"
                + "and (p.title like '%" + keyword + "%' or p.post like '%" + keyword + "%' or p.tags like '%" + keyword + "%') \n"
                + "and p.blocked=false";
        resp.addProperty("count", (Long) db.createNativeQuery(countsql).getSingleResult());
        return resp;
    }

    @RequestMapping("/results/users")
    public @ResponseBody
    JsonObject searchUsers(
            @RequestParam("q") String keyword,
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        JsonObject resp = new JsonObject();
        //search snippets
        String usersql = "select u.id,u.screen_name,u.firstname,u.lastname from users u where\n"
                + " u.screen_name like '%" + keyword + "%' or u.firstname like '%" + keyword + "%' or u.lastname like '%" + keyword + "%'";
        List<Object[]> users = db.createNativeQuery(usersql).setFirstResult(start).setMaxResults(size).getResultList();
        JsonArray uarray = new JsonArray();
        for (Object[] user : users) {
            JsonObject s = new JsonObject();
            s.addProperty("id", (String) user[1]);
            s.addProperty("text", (String) user[1]);
            s.addProperty("firstname", (String) user[2]);
            s.addProperty("lastname", (String) user[3]);
            uarray.add(s);
        }
        resp.add("users", uarray);
        //count
        String usercountsql = "select count(u.id) from users u where\n"
                + " u.screen_name like '%" + keyword + "%' or u.firstname like '%" + keyword + "%' or u.lastname like '%" + keyword + "%'";
        resp.addProperty("userscount", (Long) db.createNativeQuery(usercountsql).getSingleResult());
        return resp;
    }
}
