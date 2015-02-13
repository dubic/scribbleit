/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.models.Joke;
import com.dubic.scribbleit.posts.JokeService;
import com.dubic.scribbleit.posts.PostException;
import com.dubic.scribbleit.utils.IdmUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**usr/share/apache-tomcat-7.0.54/webapps/scribbleit/WEB-INF/classes/com/dubic/scribbleit/controllers
 *
 * @author dubem
 */
@Controller
@RequestMapping("/posts")
public class PostController {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private JokeService jokeService;
    @Value("${picture.location}")
    private String picturePath;
    
    @RequestMapping("/load")
    public String loadjokes(@RequestParam("page") String page){
        return "posts/"+page;
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
                IOUtils.copy(new FileInputStream(picturePath+"male.jpg"), responseStream);
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

    @RequestMapping("/jokes/{start}/{size}")
    public @ResponseBody
    JsonArray loadJokes(@PathVariable("start") int start, @PathVariable("size") int size) {
        JsonArray array = new JsonArray();
        try {
            List<Object[]> latestJokes = jokeService.getLatestPosts(start, size);
            for (Object[] res : latestJokes) {
                JsonObject ob = new JsonObject();

                ob.addProperty("id", (Long) res[0]);
                ob.addProperty("dislikes", (Integer) res[3]);
                ob.addProperty("likes", (Integer) res[2]);
                ob.addProperty("duration", IdmUtils.formatDate((Date) res[4]));
                ob.addProperty("post", (String) res[1]);
                ob.addProperty("poster", (String) res[5]);
                ob.addProperty("imageURL", "/scribbleit/posts/img/" + res[6]);
                ob.addProperty("commentsLength", jokeService.countComments((Long) res[0]));
//                ob.put("commentsLoaded", false);
                array.add(ob);
            }
        } catch (Exception e) {
            log.fatal(e.getMessage(), e);
        }
        return array;
    }

    @RequestMapping("/jokes/like/{id}")
    public @ResponseBody
    ObjectNode likeJoke(@PathVariable("id") Long id) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.warn(ex.getMessage());
        }
        ObjectMapper m = new ObjectMapper();
        ObjectNode ob = m.createObjectNode();
        try {
            Joke j = jokeService.like(id);
            updateJoke(j, ob);

        } catch (Exception e) {
            log.fatal(e.getMessage(), e);
        }
        return ob;
    }

    @RequestMapping("/jokes/dislike/{id}")
    public @ResponseBody
    ObjectNode dislikeJoke(@PathVariable("id") Long id) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.warn(ex.getMessage());
        }
        ObjectMapper m = new ObjectMapper();
        ObjectNode ob = m.createObjectNode();
        try {
            Joke j = jokeService.dislike(id);
            updateJoke(j, ob);

        } catch (Exception e) {
            log.fatal(e.getMessage(), e);
        }
        return ob;
    }

    @RequestMapping("/jokes/comments/{id}")
    public @ResponseBody
    ArrayNode loadComments(@PathVariable("id") Long id) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.warn(ex.getMessage());
        }
        ArrayNode arr = new ObjectMapper().createArrayNode();

        try {
            List<Object[]> comments = jokeService.getComments(id);
            createCommentsArray(comments, arr);

        } catch (Exception e) {
            log.fatal(e.getMessage(), e);
        }
        return arr;
    }

    @RequestMapping("/jokes/comment/{id}")
    public @ResponseBody
    ArrayNode comment(@PathVariable("id") Long id, @RequestBody String text) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.warn(ex.getMessage());
        }
        ArrayNode arr = new ObjectMapper().createArrayNode();
        try {
            jokeService.saveComment(text, id);
            createCommentsArray(jokeService.getComments(id), arr);
        } catch (PersistenceException ex) {

        } catch (PostException ex) {

        }
        log.debug("posted comment >> " + text);
        return arr;
    }
    
    @RequestMapping("/jokes/report/{id}")
    public @ResponseBody
    ArrayNode comment(@PathVariable("id") Long id, @RequestBody String[] faults) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.warn(ex.getMessage());
        }
        ArrayNode arr = new ObjectMapper().createArrayNode();
        
        log.debug("posted faults >> " + Arrays.toString(faults));
        return arr;
    }
    
    @RequestMapping("/jokes/new")
    public @ResponseBody
    ArrayNode newpost(@RequestBody String text) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.warn(ex.getMessage());
        }
        ArrayNode arr = new ObjectMapper().createArrayNode();
        
        log.debug("posted >> " + text);
        return arr;
    }

    private void updateJoke(Joke j, ObjectNode ob) {
        ob.put("id", j.getId());
        ob.put("dislikes", (Integer) j.getDislikes());
        ob.put("likes", (Integer) j.getLikes());
        ob.put("duration", IdmUtils.formatDate(j.getEditedDate()));
        ob.put("post", j.getPost());
        ob.put("poster", j.getUser().getScreenName());
        ob.put("imageURL", "/scribbleit/posts/img/" + j.getUser().getPicture());
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
