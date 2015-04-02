/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.posts;

import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.dto.PostData;
import com.dubic.scribbleit.idm.spi.IdentityService;
import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.models.Comment;
import com.dubic.scribbleit.models.Post;
import com.dubic.scribbleit.models.Profile;
import com.dubic.scribbleit.models.Tag;
import com.dubic.scribbleit.utils.IdmUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * interface for all posts
 *
 * @author dubem
 */
@Named
public class PostService {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private Database db;
    @Autowired
    private IdentityService idmService;

    public JsonArray queryLatestPosts(String type, int start, int size) throws PersistenceException {
        log.debug(String.format("queryLatestPosts(%s,%d,%d)", type, start, size));
        String sql = "select p.id,p.title,p.post,p.source,p.tags,p.posted_date,u.screen_name,u.picture,count(l.id),count(c.id) \n"
                + "from posts p LEFT JOIN users u on p.user_id=u.id LEFT JOIN likes l on p.id=l.post_id LEFT JOIN comments c on p.id=c.post_id\n"
                + "where p.type='%s'\n"
                + "group by p.id\n"
                + "order by p.posted_date desc";
        List<Object[]> resultList = db.createNativeQuery(String.format(sql, type)).setFirstResult(start).setMaxResults(size).getResultList();
        JsonArray array = new JsonArray();
        for (Object[] object : resultList) {
            JsonObject job = new JsonObject();
            job.addProperty("id", (Long) object[0]);
            job.addProperty("title", (String) object[1]);
            job.addProperty("post", (String) object[2]);
            job.addProperty("source", (String) object[3]);
            job.add("tags", new Gson().toJsonTree(((String) object[4]).split(",")));
            job.addProperty("duration", IdmUtils.formatDate((Date) object[5]));
            job.addProperty("poster", (String) object[6]);
            job.addProperty("imageURL", (String) object[7]);
            job.addProperty("likes", (Long) object[8]);
            job.addProperty("commentsLength", (Long) object[9]);
            job.add("comments", new Gson().toJsonTree(new Object[]{}));
            array.add(job);
        }
        return array;
    }

    @Transactional
    public Post savePost(PostData data, String type) throws PersistenceException, PostException {
        log.debug(String.format("savePost(%s,%s)", new Gson().toJson(data), type));
        User user = idmService.getUserLoggedIn();
        if (user == null) {
            throw new PostException("please log in to continue");
        }
        Post p = new Post(data);
        p.setType(Post.Type.valueOf(type));
        p.setUser(user);
        db.persist(p);
        Profile profile = user.getProfile();
        if (p.getType().equals(Post.Type.JOKE)) {
            profile.setJokes(profile.getJokes() + 1);
        }
        if (p.getType().equals(Post.Type.PROVERB)) {
            profile.setJokes(profile.getProverbs() + 1);
        }
        if (p.getType().equals(Post.Type.QUOTE)) {
            profile.setQuotes(profile.getProverbs() + 1);
        }
        db.merge(profile);
        log.info(String.format("Post saved [%s] by [%s]", type, user.getScreenName()));
        return p;
    }

    public Comment saveComment(String comment, Long postId) throws PersistenceException, EntityNotFoundException, PostException {
        log.debug(String.format("saveComment(%s,%d)", comment, postId));
        User user = idmService.getUserLoggedIn();
        if (user == null) {
            throw new PostException("please log in to continue");
        }
        //get post
        Post post = db.find(Post.class, postId);
        //add comment to post
        Comment c = new Comment(post);
        c.setText(comment);
        c.setUser(user);
        db.persist(c);
        log.info(String.format("Comment saved by [%s]", user.getScreenName()));
        return c;
    }

//    public List<Comment> getComments(Long postId) throws PersistenceException, PostException;
    public Post updatePost(Post post) throws PersistenceException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void blockPost(Long postId) throws PersistenceException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void reportPost(Long postId, String reasons, String ip) throws PersistenceException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void watchTag(User user, Tag tag) throws PersistenceException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void removeWatch(Long id) throws PersistenceException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public Post like(Long postId) throws PersistenceException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public int numOfPosts(User user) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public JsonArray getComments(Long id) {
        String sql = "select c.id,c.posted,c.text,u.screen_name,u.picture from comments c,users u \n"
                + "where c.user_id=u.id and c.post_id='%d'\n"
                + "order by c.posted desc";
        List<Object[]> resultList = db.createNativeQuery(String.format(sql, id)).getResultList();
        JsonArray array = new JsonArray();
        for (Object[] o : resultList) {
            JsonObject job = new JsonObject();
            job.addProperty("id", (Long)o[0]);
            job.addProperty("text", (String)o[2]);
            job.addProperty("poster", (String)o[3]);
            job.addProperty("imageURL", (String)o[4]);
            job.addProperty("duration", IdmUtils.formatDate((Date)o[1]));
            array.add(job);
        }
        return array;
    }

}
