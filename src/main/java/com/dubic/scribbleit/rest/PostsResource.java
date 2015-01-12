/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.rest;

import com.dubic.scribbleit.idm.models.User;
import com.dubic.scribbleit.models.JKComment;
import com.dubic.scribbleit.posts.JokeService;
import com.dubic.scribbleit.posts.PostException;
import com.dubic.scribbleit.utils.IdmUtils;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * REST Web Service
 *
 * @author dubem
 */
@Path("posts")
public class PostsResource {

    @Context
    private UriInfo context;
    @Context
    private HttpServletRequest request;
    @Autowired
    private JokeService jokeService;
    private final Logger log = Logger.getLogger(getClass());
    @PathParam("postId")
    private Long postId;
    private GsonBuilder gb = new GsonBuilder();

    /**
     * Creates a new instance of PostsResource
     */
    public PostsResource() {
    }

    /**
     * /0/10
     *
     * @param start
     * @param amount
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/jokes/{start}/{amount}")
    @Produces("application/json")
    public String getJokes(@PathParam("start") int start, @PathParam("amount") int amount) {
        JsonArray ja = new JsonArray();
        try {
            List<Object[]> latestJokes = jokeService.getLatestPosts(start, amount);
            for (Object[] res : latestJokes) {
                JsonObject jo = new JsonObject();
                jo.addProperty("id", (Long) res[0]);
                jo.addProperty("dislikes", (Integer) res[3]);
                jo.addProperty("likes", (Integer) res[2]);
                jo.addProperty("duration", IdmUtils.formatDate((Date) res[4]));
                jo.addProperty("post", (String) res[1]);
                jo.addProperty("poster", (String) res[5]);
                jo.addProperty("imageURL", "p/" + res[6]);
                List<Object[]> comments = jokeService.getComments((Long) res[0]);
                log.info(String.format("[[%s]]", gb.create().toJson(comments)));
                JsonArray ca = new JsonArray();
                for (Object[] object : comments) {
                    JsonObject co = new JsonObject();
                    co.addProperty("id", (Long) object[0]);
                    co.addProperty("text", (String) object[2]);
                    co.addProperty("duration", IdmUtils.convertPostedTime(((Calendar) object[1]).getTimeInMillis()));
                    User u = (User) object[3];
                    co.addProperty("imageURL", "p/" + u.getProfile().getPicture());
                    co.addProperty("poster", u.getScreenName());
                    ca.add(co);
                }
                jo.add("comments", ca);
                ja.add(jo);
            }
        } catch (Exception e) {
            log.fatal(e.getMessage(), e);
        }

        gb.disableHtmlEscaping();
        return gb.create().toJson(ja);
    }

    @POST
    @Path("/jokes/comment/{postId}")
    @Consumes("application/json")
    @Produces("application/json")
    public String commentJoke(@QueryParam("comment") String comment) {
        if (postId == null || IdmUtils.isEmpty(comment)) {
            log.error("Post id null");
            throw new NullPointerException("post id is null");
        }
        try {
            JKComment savedComment = (JKComment) jokeService.saveComment(comment, postId);
            JsonObject jo = new JsonObject();
            jo.addProperty("id", savedComment.getId());
            jo.addProperty("text", savedComment.getText());
            jo.addProperty("duration", IdmUtils.convertPostedTime(savedComment.getPostedTime().getTimeInMillis()));
            jo.addProperty("imageURL", "p/" + savedComment.getUser().getProfile().getPicture());
            jo.addProperty("poster", savedComment.getUser().getScreenName());

            return gb.create().toJson(jo);
        } catch (PersistenceException ex) {
            log.fatal("could not save comment", ex);
        } catch (PostException ex) {
            log.error("could not save comment", ex);
        }
        return gb.create().toJson("");
    }

    


    @POST
    @Path("/jokes/report/{postId}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response reportJoke(@QueryParam("reasons") String reasons) {
        if (postId == null) {
            log.error("Post id null");
            throw new NullPointerException("post id is null");
        }
//        log.debug("reasons - "+Arrays.toString(reasons.toArray()));

        try {
            jokeService.reportPost(postId, reasons,request.getRemoteAddr());
            return Response.ok().build();
        } catch (PersistenceException ex) {
            log.fatal("could not save joke", ex);
        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
        return Response.serverError().build();
    }
}
