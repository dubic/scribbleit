/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import com.dubic.scribbleit.dto.PostData;
import com.dubic.scribbleit.models.Comment;
import com.dubic.scribbleit.models.Post;
import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.posts.JokeService;
import com.dubic.scribbleit.posts.PostException;
import com.dubic.scribbleit.posts.PostService;
import com.dubic.scribbleit.utils.IdmUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * usr/share/apache-tomcat-7.0.54/webapps/scribbleit/WEB-INF/classes/com/dubic/scribbleit/controllers
 *
 * @author dubem
 */
@Controller
@RequestMapping("/posts")
public class PostController {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private PostService postService;
    @Value("${picture.location}")
    private String picturePath;

    @RequestMapping("/view")
    public String loadjokes(@RequestParam("page") String page) {
        return "posts/" + page;
    }

    @RequestMapping(value = {"/img/{pic}"})
    public void processImage(HttpServletRequest request, HttpServletResponse response, @PathVariable("pic") String picId) {
        ServletOutputStream responseStream = null;
        try {
            response.setContentType("image/jpeg");
            responseStream = response.getOutputStream();

            if (!picId.equalsIgnoreCase("male")) {
                IOUtils.copy(new FileInputStream(picturePath + picId), responseStream);
            } else {
                //send avatar
                IOUtils.copy(new FileInputStream(picturePath + "male.jpg"), responseStream);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            try {
                responseStream.close();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(PostController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @RequestMapping("/new/{type}")
    public @ResponseBody
    JsonObject newpost(@RequestBody PostData postData, @PathVariable("type") String type) {
        JsonObject resp = new JsonObject();
        try {
            Post p = postService.savePost(postData, type);
            JsonObject job = new JsonObject();
            job.addProperty("id", p.getId());
            job.addProperty("title", p.getTitle());
            job.addProperty("post", p.getText());
            job.addProperty("source", p.getSource());
            job.add("tags", new Gson().toJsonTree(p.getTags().split(",")));
            job.addProperty("duration", IdmUtils.formatDate(p.getPostedDate().getTime()));
            job.addProperty("poster", p.getUser().getScreenName());
            job.addProperty("imageURL", p.getUser().getPicture());
            job.addProperty("likes", 0);
            job.addProperty("commentsLength", 0);
            job.add("comments", new Gson().toJsonTree(new Object[]{}));

            resp.addProperty("code", 0);
            resp.add("post", job);

        } catch (PersistenceException ex) {
            log.fatal(ex);
            resp.addProperty("code", 500);
        } catch (PostException ex) {
            log.warn(ex.getMessage());
            resp.addProperty("code", 403);
        } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);
            resp.addProperty("code", 500);
        }
        return resp;
    }

    @RequestMapping("/load/{type}")
    public @ResponseBody
    JsonArray loadPostsByType(@PathVariable("type") String type,
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return postService.queryLatestPosts(type, start, size);
    }

//    @RequestMapping("/jokes/like/{id}")
//    public @ResponseBody
//    ObjectNode likeJoke(@PathVariable("id") Long id) {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            log.warn(ex.getMessage());
//        }
//        ObjectMapper m = new ObjectMapper();
//        ObjectNode ob = m.createObjectNode();
//        try {
//            Joke j = jokeService.like(id);
//            updateJoke(j, ob);
//
//        } catch (Exception e) {
//            log.fatal(e.getMessage(), e);
//        }
//        return ob;
//    }
    @RequestMapping(value = "/comments/{id}",method = RequestMethod.GET)
    public @ResponseBody
    JsonArray loadComments(@PathVariable("id") Long id) {
       return postService.getComments(id);
    }

    @RequestMapping("/comment/{id}")
    public @ResponseBody
    JsonObject comment(@PathVariable("id") Long id, @RequestBody String text) {
        JsonObject resp = new JsonObject();
        try {
            postService.saveComment(text, id);

            resp.addProperty("code", 0);
            resp.add("comments", postService.getComments(id));
        } catch (PostException ex) {
            log.warn(ex.getMessage());
            resp.addProperty("code", 403);
        } catch (EntityNotFoundException ex) {
            log.warn(ex.getMessage());
            resp.addProperty("code", 404);
        }
        return resp;
    }

    @RequestMapping("/jokes/report/{id}")
    public @ResponseBody
    ArrayNode report(@PathVariable("id") Long id, @RequestBody String[] faults) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.warn(ex.getMessage());
        }
        ArrayNode arr = new ObjectMapper().createArrayNode();

        log.debug("posted faults >> " + Arrays.toString(faults));
        return arr;
    }

    private void createCommentsArray(List<Object[]> comments, ArrayNode arr) {
        for (Object[] res : comments) {
            ObjectNode ob = new ObjectNode(JsonNodeFactory.instance);

            ob.put("id", (Long) res[0]);
            ob.put("text", (String) res[2]);
            ob.put("duration", IdmUtils.convertPostedTime(((Calendar) res[1]).getTimeInMillis()));
            User u = (User) res[3];
            ob.put("imageURL", "/scribbleit/posts/img/" + u.getPicture());
            ob.put("poster", u.getScreenName());
            arr.add(ob);
        }
    }

}
