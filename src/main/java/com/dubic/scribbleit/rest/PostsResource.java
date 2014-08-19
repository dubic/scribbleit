/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.rest;

import com.dubic.scribbleit.dto.JokeData;
import com.dubic.scribbleit.posts.JokeService;
import com.dubic.scribbleit.utils.IdmUtils;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
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
    @Autowired
    private JokeService jokeService;
    private final Logger log = Logger.getLogger(getClass());

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
        List<JokeData> jokeDatas = new ArrayList<JokeData>();
        try {
            List<Object[]> latestJokes = jokeService.getLatestPosts(start, amount);
            jokeDatas = new ArrayList<JokeData>();
            for (Object[] res : latestJokes) {
                JokeData jokeData = new JokeData();
                jokeData.setDislikes((Integer) res[3]);
                jokeData.setLikes((Integer) res[2]);
                jokeData.setDuration(IdmUtils.formatDate((Date) res[4]));
                jokeData.setId((Long) res[0]);
                jokeData.setPost((String) res[1]);
                jokeData.setPoster((String) res[5]);
                jokeData.setImageURL("p/" + res[6]);
                jokeDatas.add(jokeData);
            }
        } catch (Exception e) {
            log.fatal(e.getMessage(), e);
        }
        GsonBuilder gb = new GsonBuilder();
        gb.disableHtmlEscaping();
        return gb.create().toJson(jokeDatas);

    }

    /**
     * PUT method for updating or creating an instance of PostsResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
