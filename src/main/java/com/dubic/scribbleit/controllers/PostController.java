/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import com.dubic.scribbleit.dto.PostData;
import com.dubic.scribbleit.models.Post;
import com.dubic.scribbleit.models.Report;
import com.dubic.scribbleit.posts.PostException;
import com.dubic.scribbleit.posts.PostService;
import com.dubic.scribbleit.posts.TagService;
import com.dubic.scribbleit.utils.IdmUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.ExternallyRolledFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
    @Value("${bucket.url}")
    private String bucketUrl;
    @Autowired
    private TagService tagService;

    @RequestMapping("/new/{type}")
    @ResponseBody
    public JsonObject newpost(
                    @PathVariable("type") String type,
                    @RequestParam(value = "file",required = false) MultipartFile file,
                    @RequestParam(value = "title",required = false) String title,
                    @RequestParam(value = "post",required = false) String text,
                    @RequestParam(value = "source",required = false) String source,
                    @RequestParam(value = "tags",required = false) String tags
            ) {
        JsonObject resp = new JsonObject();
        try {
//            System.out.println("File: "+file.getSize());
//            System.out.println("File: "+file.getContentType());
//            System.out.println("File: "+file.getOriginalFilename());
//            throw new Exception("just testing");
            
            PostData pd = new PostData();
            pd.setPost(text);
            pd.setSource(source);
            pd.setTags(tags);
            pd.setTitle(title);
            pd.setFile(file);
            
            Post p = postService.savePost(pd, type);
            JsonObject job = new JsonObject();
            job.addProperty("id", p.getId());
            job.addProperty("title", p.getTitle());
            job.addProperty("post", p.getText());
            job.addProperty("source", p.getSource());
            job.add("tags", new Gson().toJsonTree((p.getTags()==null?"":p.getTags()).split(",")));
            job.addProperty("duration", IdmUtils.formatDate(p.getPostedDate().getTime()));
            job.addProperty("poster", p.getUser().getScreenName());
            job.addProperty("imageURL", p.getUser().getPicture());
            job.addProperty("likes", 0);
            job.addProperty("commentsLength", 0);
            job.add("comments", new Gson().toJsonTree(new Object[]{}));
            if (!StringUtils.isEmpty(p.getMedia())) {
                job.addProperty("image", bucketUrl.concat(p.getMedia()));
            }

            resp.addProperty("code", 0);
            resp.add("post", job);

        } catch (PersistenceException ex) {
            log.fatal(ex.getMessage(),ex);
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
    JsonObject loadPostsByType(@PathVariable("type") String type,
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "id", defaultValue = "0") Long id) {
        return postService.queryLatestPosts(type, start, size,id);
    }

    @RequestMapping("/load/{type}/{id}")
    public @ResponseBody
    JsonArray loadPostsById(@PathVariable("type") String type, @PathVariable("id") Long id) {
        return postService.getPost(id);
    }
    
    @RequestMapping("/load/{type}/tag/{tag}")
    public @ResponseBody
    JsonObject loadPostsByTag(@PathVariable("type") String type, @PathVariable("tag") String tag,
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return postService.getPostByTag(type,tag,start,size);
    }
    
    @RequestMapping("/ping")
    @ResponseBody
    public String ping() {
        return "OK";
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
    @RequestMapping(value = "/comments/{id}", method = RequestMethod.GET)
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

    @RequestMapping("/report")
    public @ResponseBody
    JsonObject report(@RequestBody Report report, HttpServletRequest req) {
        JsonObject resp = new JsonObject();
        try {
            IdmUtils.validate(report);
            postService.reportPost(report, req.getRemoteAddr());
            resp.addProperty("code", 0);
        } catch (ConstraintViolationException cve) {
            List<String> msgs = IdmUtils.listValidationMsgs(cve.getConstraintViolations());
            resp.addProperty("code", 501);
            resp.add("msgs", new Gson().toJsonTree(msgs));
            log.warn(new Gson().toJson(msgs));
        } catch (Exception e) {
            log.fatal(e.getMessage(), e);
            resp.addProperty("code", 500);
        }

        return resp;
    }
    
    @RequestMapping("/tags")
    public @ResponseBody
    List<Object[]> tags() {
        return tagService.getTags();
    }

    

}
