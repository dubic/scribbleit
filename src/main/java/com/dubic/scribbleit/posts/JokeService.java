/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.posts;

import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.idm.spi.IdentityService;
import com.dubic.scribbleit.models.Comment;
import com.dubic.scribbleit.models.Post;
import com.dubic.scribbleit.models.Profile;
import com.dubic.scribbleit.models.Report;
import com.dubic.scribbleit.models.Tag;
import com.dubic.scribbleit.profile.ProfileService;
import com.dubic.scribbleit.utils.IdmUtils;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import org.apache.log4j.Logger;

/**
 *
 * @author dubem
 */
@Named("jokeService")
public class JokeService {

    private final Logger log = Logger.getLogger(getClass());
    @Inject
    private Database db;
    @Inject
    private IdentityService id;
    @Inject
    private ProfileService ps;
    private boolean blockcreate = false;
    

    public boolean isBlockcreate() {
        return blockcreate;
    }

    public void setBlockcreate(boolean blockcreate) {
        this.blockcreate = blockcreate;
    }

    /**
     * accept a post message and create a Post object with it and save created
     * to DB
     *
     * @param post the posted message
     * @return posted Post object in the response wrapper
     * @throws PersistenceException
     * @throws com.dubic.scribbles.posts.PostException
     */
    
    public Post savePost(String text) throws PersistenceException, PostException ,ConstraintViolationException{
        log.debug(String.format("savePost(%s)", text));
        if(blockcreate){
            throw new PostException("service unavailable. Try again later");
        }
        User authenticatedUser = id.getUserLoggedIn();
        //create Post
        Post post = new Post();
//        post.setIconUrl();
        post.setText(text);
        post.setUser(authenticatedUser);
        IdmUtils.validate(post);
        //update profile
        Profile profile = authenticatedUser.getProfile();
        profile.setJokes(profile.getJokes()+1);
        profile.setUpdated(new Date());
        db.merge(post,profile);
        return post;
    }

    
    public Post updatePost(Post post) throws PersistenceException {
        log.debug("updatePost");
        db.merge(post);
        return post;
    }

    
    public void blockPost(Long postId) throws NullPointerException, PersistenceException {
        log.debug("blockPost");
        Post post = db.find(Post.class, postId);
        post.setBlocked(true);
        updatePost(post);
    }

    public List<Post> queryPosts(User user) throws PersistenceException {
        log.debug("queryPosts");
        return db.createQuery("SELECT j FROM Post j WHERE j.blocked = FALSE", Post.class).getResultList();
    }

    public List<Post> queryLatestPosts(User user) throws PersistenceException {
        log.debug("queryLatestPosts");
        return db.createQuery("SELECT j FROM Post j WHERE j.blocked = FALSE ORDER BY j.postedTime DESC", Post.class).getResultList();
    }

    public List<Post> queryMyPosts(User user) throws PersistenceException {
        log.debug("queryMyPosts");
        return db.createQuery("SELECT j FROM Post j WHERE j.blocked = FALSE AND j.user.id = :userId ORDER BY j.postedTime DESC", Post.class)
                .setParameter("userId", user.getId()).getResultList();
    }

    public List<Post> queryPopularPosts(User user) throws PersistenceException {
        log.debug("queryPopularPosts");
        return db.createQuery("SELECT j FROM Post j WHERE j.blocked = FALSE ORDER BY j.likes DESC", Post.class).getResultList();
    }

    
    
    public Post like(Long postId) throws PersistenceException {
        Post post = db.getReference(Post.class, postId);
//        if (post == null) {
//            throw new NullPointerException(String.format("post with id : %s is null", postId+""));
//        }
//        log.debug(String.format("like post[%s]", post.getId()));
//        post.setLikes(post.get() + 1);
//        db.merge(post);
        return post;
    }

    
    public int numOfPosts(User user) {
        log.debug(String.format("Number of posts of user[%s]", user.getScreenName()));
        try {
            return (Integer) db.createQuery("SELECT COUNT(j) FROM Post j WHERE j.user.id = :uid").setParameter("uid", user.getId()).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Object[]> getLatestPosts(int start, int amount) {
        return db.createQuery("SELECT j.id,j.post,j.likes,j.dislikes,j.editedDate,u.screenName,u.profile.picture FROM Post j,User u WHERE j.blocked != TRUE AND j.user.id = u.id ORDER BY j.editedDate DESC", Object[].class)
                .setFirstResult(start).setMaxResults(amount).getResultList();
    }

    
    public Comment saveComment(String commentStr, Long postId) throws PersistenceException, PostException {
        //make sure user is logged in
        User userLoggedIn = id.getUserLoggedIn();
        if (userLoggedIn == null) {
            throw new PostException("No user in session");
        }
        //find post
        Post post = db.find(Post.class, postId);
        //include comment and save
        if (post == null) {
            throw new PostException("Post with id not found : "+postId);
        }
        //create comment
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setText(commentStr);
        comment.setUser(userLoggedIn);
        db.persist(comment);
        log.debug(String.format("comment saved to post : %s", postId+""));
        return comment;
    }

    
    public List<Object[]> getComments(Long postId) throws PersistenceException, PostException {
        if(postId == null){
            throw new PostException("post id cannot be null");
        }
        return db.createQuery("SELECT c.id,c.postedTime,c.text,c.user FROM JKComment c WHERE c.post.id = :postId", Object[].class)
                .setParameter("postId", postId).getResultList();
    }

    
    public void reportPost(Long postId,String reasons,String ip) throws PersistenceException {
        log.debug(String.format("reportPost(%s,%s)", postId+"",reasons));
        Report r = new Report();
        r.setPostClass(Post.class.getName());
        r.setPostId(postId);
        r.setReasons(reasons);
        r.setReporter(id.getUserLoggedIn());
        r.setIp(ip);
        db.persist(r);
    }

    public long countComments(Long id) {
        return (Long)db.createQuery("SELECT COUNT(c.id) FROM JKComment c WHERE c.post.id = :jid")
                .setParameter("jid", id).getSingleResult();
    }

}
