/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.posts;

import com.dubic.scribbleit.application.CronFactory;
import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.dto.PostData;
import com.dubic.scribbleit.idm.spi.IdentityServiceImpl;
import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.models.Comment;
import com.dubic.scribbleit.models.MediaItem;
import com.dubic.scribbleit.models.Post;
import com.dubic.scribbleit.models.Profile;
import com.dubic.scribbleit.models.Report;
import com.dubic.scribbleit.models.Tag;
import com.dubic.scribbleit.utils.IdmCrypt;
import com.dubic.scribbleit.utils.IdmUtils;
import com.dubic.scribbleit.utils.InvalidException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private CronFactory cronFactory;
    @Autowired
    private IdentityServiceImpl idmService;
    @Autowired
    private AwsService awsService;
    @Autowired
    private TagService tagService;
    @Value("${bucket.url}")
    private String bucketUrl;

    @PostConstruct
    public void started() {
        log.debug(String.format("%s CREATED", getClass().getSimpleName()));
        log.debug(String.format("bucket.url = %s", bucketUrl));
    }

    public JsonObject queryLatestPosts(String type, int start, int size, Long id) throws PersistenceException {
        log.debug(String.format("queryLatestPosts(%s,%d,%d)", type, start, size));
        JsonObject resp = new JsonObject();
        String mark = id == 0 ? "" : " and p.id <= " + id;
        String sql = "select p.id,p.title,p.post,p.source,p.tags,p.posted_date,u.screen_name,u.picture,count(l.id),count(c.id),p.media_id\n"
                + "from posts p LEFT JOIN users u on p.user_id=u.id LEFT JOIN likes l on p.id=l.post_id LEFT JOIN comments c on p.id=c.post_id\n"
                + "where p.type='%s' and p.blocked=false " + mark + "\n"
                + "group by p.id\n"
                + "order by p.posted_date desc";
        List<Object[]> resultList = db.createNativeQuery(String.format(sql, type)).setFirstResult(start).setMaxResults(size).getResultList();
        JsonArray array = new JsonArray();
        for (Object[] object : resultList) {
            JsonObject job = new JsonObject();
            job.addProperty("id", (Long) object[0]);
            job.addProperty("title", (String) object[1]);
            String post = (String) object[2];
            if (post != null) {
                post = post.trim().replace("â??", "\"");
            }
            job.addProperty("post", post);
            job.addProperty("source", (String) object[3]);
            String tags = ((String) object[4]);
            job.add("tags", new Gson().toJsonTree((tags == null ? "" : tags).split(",")));
            job.addProperty("duration", IdmUtils.formatDate((Date) object[5]));
            job.addProperty("poster", (String) object[6]);
            job.addProperty("imageURL", (String) object[7]);
            job.addProperty("likes", (Long) object[8]);
            job.addProperty("commentsLength", (Long) object[9]);
            job.add("comments", new Gson().toJsonTree(new Object[]{}));
            String media = (String) object[10];
            if (!StringUtils.isEmpty(media)) {
                job.addProperty("image", bucketUrl.concat(media));
            }
            array.add(job);
        }
        resp.add("posts", array);
        resp.addProperty("total", db.countNative("select count(p.id) from posts p where p.type='" + type + "' and p.blocked=false " + mark));
        resp.addProperty("session", (idmService.getUserLoggedIn() != null));
        return resp;
    }

    @Transactional
    public Post savePost(PostData data, String type) throws PersistenceException, PostException, IOException, Exception {
        log.debug(String.format("savePost(%s)", type));
        User user = idmService.getUserLoggedIn();
        if (user == null) {
            throw new PostException("please log in to continue");
        }
        Post p = new Post(data);

        p.setType(Post.Type.valueOf(type));
        p.setUser(user);

        //save media item
        if (data.getFile() != null) {
            MediaItem m = new MediaItem();
            m.setFilesize(data.getFile().getSize());
            m.setMimeType(data.getFile().getContentType());
            //post to bucket
            String key = IdmCrypt.encodeMD5(new Date().getTime() + "", "image");
            awsService.putImage(data.getFile(), key, 350, 450);

            p.setMedia(key);

            m.setTitle(key);
            try {
                db.persist(m);
            } catch (Exception e) {
                awsService.remove(key);
                throw e;
            }
        }
        //save Post
        db.persist(p);
        tagService.addTags(p.getTags(), user.getScreenName());

        //update profile
        Profile profile = user.getProfile();
        if (p.getType().equals(Post.Type.JOKE)) {
            profile.setJokes(profile.getJokes() + 1);
        }
        if (p.getType().equals(Post.Type.PROVERB)) {
            profile.setProverbs(profile.getProverbs() + 1);
        }
        if (p.getType().equals(Post.Type.QUOTE)) {
            profile.setQuotes(profile.getQuotes() + 1);
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

    public void reportPost(Report r, String ip) throws PersistenceException {
        //create a report and save it
        log.debug(String.format("reportPost(%d,%s", r.getPostId(), ip));
        r.setIp(ip);
        r.setReporter(idmService.getUserLoggedIn());
        db.persist(r);
        log.info(String.format("Post Reported %d", r.getPostId()));
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
            job.addProperty("id", (Long) o[0]);
            job.addProperty("text", (String) o[2]);
            job.addProperty("poster", (String) o[3]);
            job.addProperty("imageURL", (String) o[4]);
            job.addProperty("duration", IdmUtils.formatDate((Date) o[1]));
            array.add(job);
        }
        return array;
    }

    public JsonObject getLatestsUserPosts(String username, int start, int size) {
        JsonObject resp = new JsonObject();
        log.debug(String.format("getLatestsUserPosts(%s,%d,%d)", username, start, size));
        String sql = "select p.id,p.title,p.post,p.source,p.posted_date,p.type,p.media_id from posts p,users u \n"
                + "where p.user_id=u.id and u.screen_name='%s' and p.blocked=false\n"
                + "order by p.posted_date desc";
        List<Object[]> resultList = db.createNativeQuery(String.format(sql, username)).setFirstResult(start).setMaxResults(size).getResultList();
        JsonArray array = new JsonArray();
        for (Object[] object : resultList) {
            JsonObject job = new JsonObject();
            job.addProperty("id", (Long) object[0]);
            job.addProperty("title", (String) object[1]);
            String text = (String) object[2];

            job.addProperty("source", (String) object[3]);
            job.addProperty("date", ((Date) object[4]).getTime());
            job.addProperty("type", (String) object[5]);
            job.addProperty("image", (String) object[6]);
            if (text != null) {
                text = text.trim().replace("â??", "\"");
                if (text.length() > 200) {
                    job.addProperty("readMore", true);
                    job.addProperty("text", text.substring(0, 199).concat("..."));
                } else {
                    job.addProperty("readMore", false);
                    job.addProperty("text", text);
                }
            }

            array.add(job);
        }
        resp.add("posts", array);
        resp.addProperty("session", (idmService.getUserLoggedIn() != null));
        return resp;
    }

    public static void main(String[] args) {
        String t = "Recently, a friend took me to the jewel-in-the-crown of the Redeemed Christian Church of God, a glitzy parish called â??City of Davidâ?? in Victoria Island, Lagos. Displayed resplendently on a wall in";
        System.out.println(t);
        System.out.println("".replace("â??", "\""));
    }

    public JsonArray getPost(Long id) {
        log.debug(String.format("getPost(%d)", id));
        String sql = "select p.id,p.title,p.post,p.source,p.tags,p.posted_date,u.screen_name,u.picture,count(l.id),count(c.id),p.media_id\n"
                + "from posts p LEFT JOIN users u on p.user_id=u.id LEFT JOIN likes l on p.id=l.post_id LEFT JOIN comments c on p.id=c.post_id\n"
                + "where p.id=%d and p.blocked=false";
        List<Object[]> resultList = db.createNativeQuery(String.format(sql, id)).getResultList();
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
            job.addProperty("image", (String) object[10]);
            array.add(job);
        }
        return array;

    }

    public JsonObject getPostByTag(String type, String tag, int start, int size) {
        log.debug(String.format("getPostByTag(%s)", tag));
        JsonObject resp = new JsonObject();
        String sql = "select p.id,p.title,p.post,p.source,p.tags,p.posted_date,u.screen_name,u.picture,count(l.id),count(c.id),p.media_id\n"
                + "from posts p LEFT JOIN users u on p.user_id=u.id LEFT JOIN likes l on p.id=l.post_id LEFT JOIN comments c on p.id=c.post_id\n"
                + "where p.blocked=false and p.tags like '%" + tag + "%' order by p.posted_date desc";
        log.info(sql);
        List<Object[]> resultList = db.createNativeQuery(sql).setFirstResult(start).setMaxResults(size).getResultList();
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
            job.addProperty("image", (String) object[10]);
            array.add(job);
        }
        resp.add("posts", array);
        resp.addProperty("total", db.countNative("select count(p.id) from posts p where p.type='" + type + "' and p.blocked=false and p.tags like '%" + tag + "%'"));
        return resp;
    }

    public MediaItem findMediaItem(String title) {
        return IdmUtils.getFirstOrNull(db.createQuery("SELECT m FROM MediaItem m WHERE m.title = :title", MediaItem.class).setParameter("title", title).getResultList());
    }

    @Transactional
    public void deletePost(Long postId) throws InvalidException {
        Post post = db.find(Post.class, postId);
        User userLoggedIn = idmService.getUserLoggedIn();
        if (!post.getUser().getId().equals(userLoggedIn.getId())) {
            throw new InvalidException("Not authorized for this action");
        }
        //delete post dependecies
        log.info(String.format("deleted %d comments", db.createNativeQuery("delete from comments where post_id = " + postId).executeUpdate()));
        log.info(String.format("deleted %d reports", db.createNativeQuery("delete from report where post_id = " + postId).executeUpdate()));
        log.info(String.format("deleted %d media items", db.createNativeQuery("delete from media where title = " + post.getMedia()).executeUpdate()));
        //proceed and delete post
        db.delete(post);
        //update profile
        Profile profile = userLoggedIn.getProfile();
        if (post.getType().equals(Post.Type.JOKE)) {
            profile.setJokes(profile.getJokes() - 1);
        } if (post.getType().equals(Post.Type.QUOTE)) {
            profile.setQuotes(profile.getQuotes() - 1);
        }if(post.getType().equals(Post.Type.PROVERB)) {
            profile.setProverbs(profile.getProverbs() - 1);
        }
        log.info(String.format("deleted post [%d] by [%s]", postId, userLoggedIn.getScreenName()));
    }

}
