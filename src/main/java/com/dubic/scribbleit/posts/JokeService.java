/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.posts;

import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.idm.models.User;
import com.dubic.scribbleit.idm.spi.IdentityService;
import com.dubic.scribbleit.models.Comment;
import com.dubic.scribbleit.models.JKComment;
import com.dubic.scribbleit.models.Joke;
import com.dubic.scribbleit.models.Post;
import com.dubic.scribbleit.models.Profile;
import com.dubic.scribbleit.models.Report;
import com.dubic.scribbleit.models.Tag;
import com.dubic.scribbleit.models.Watch;
import com.dubic.scribbleit.profile.ProfileService;
import com.dubic.scribbleit.utils.IdmUtils;
import java.util.Arrays;
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
public class JokeService implements PostService {

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
     * accept a joke message and create a Joke object with it and save created
     * to DB
     *
     * @param post the posted message
     * @return posted Joke object in the response wrapper
     * @throws PersistenceException
     * @throws com.dubic.scribbles.posts.PostException
     */
    @Override
    public Post savePost(String post) throws PersistenceException, PostException ,ConstraintViolationException{
        log.debug(String.format("savePost(%s)", post));
        if(blockcreate){
            throw new PostException("service unavailable. Try again later");
        }
        User authenticatedUser = id.getUserLoggedIn();
        //create Joke
        Joke joke = new Joke();
//        joke.setIconUrl();
        joke.setPost(post);
        joke.setUser(authenticatedUser);
        IdmUtils.validate(joke);
        //update profile
        Profile profile = authenticatedUser.getProfile();
        profile.setJokes(profile.getJokes()+1);
        profile.setUpdated(new Date());
        db.merge(joke,profile);
        return joke;
    }

    @Override
    public Post updatePost(Post post) throws PersistenceException {
        log.debug("updatePost");
        Joke joke = (Joke) post;
        joke.setEditedDate(new Date());
        db.merge(joke);
        return joke;
    }

    @Override
    public void blockPost(Long postId) throws NullPointerException, PersistenceException {
        log.debug("blockPost");
        Joke joke = db.find(Joke.class, postId);
        joke.setBlocked(true);
        updatePost(joke);
    }

    public List<Joke> queryPosts(User user) throws PersistenceException {
        log.debug("queryPosts");
        return db.createQuery("SELECT j FROM Joke j WHERE j.blocked = FALSE", Joke.class).getResultList();
    }

    public List<Joke> queryLatestPosts(User user) throws PersistenceException {
        log.debug("queryLatestPosts");
        return db.createQuery("SELECT j FROM Joke j WHERE j.blocked = FALSE ORDER BY j.postedTime DESC", Joke.class).getResultList();
    }

    public List<Joke> queryMyPosts(User user) throws PersistenceException {
        log.debug("queryMyPosts");
        return db.createQuery("SELECT j FROM Joke j WHERE j.blocked = FALSE AND j.user.id = :userId ORDER BY j.postedTime DESC", Joke.class)
                .setParameter("userId", user.getId()).getResultList();
    }

    public List<Joke> queryPopularPosts(User user) throws PersistenceException {
        log.debug("queryPopularPosts");
        return db.createQuery("SELECT j FROM Joke j WHERE j.blocked = FALSE ORDER BY j.likes DESC", Joke.class).getResultList();
    }

    @Override
    public void watchTag(User user, Tag tag) throws PersistenceException {
        log.debug(String.format("watchTag tag[%s],user[%s]", tag.getName(), user.getScreenName()));
        Watch watch = new Watch(user, tag);
        db.persist(watch);
        log.info(String.format("new watch created [%d]", watch.getId()));
    }

    @Override
    public void removeWatch(Long id) throws PersistenceException {
        log.debug(String.format("removeWatch id[%d]", id));
        int res = db.createQuery("DELETE FROM Watch w WHERE w.id = :id").setParameter("id", id).executeUpdate();
        log.info(String.format("watch removed [%d]", id));
    }

    @Override
    public int like(Long postId) throws PersistenceException {
        Joke joke = db.getReference(Joke.class, postId);
        if (joke == null) {
            throw new NullPointerException(String.format("joke with id : %s is null", postId+""));
        }
        log.debug(String.format("like post[%s]", joke.getId()));
        joke.setLikes(joke.getLikes() + 1);
        db.merge(joke);
        return joke.getLikes();
    }

    @Override
    public int dislike(Long postId) throws PersistenceException {
        Joke joke = db.getReference(Joke.class, postId);
        if (joke == null) {
            throw new NullPointerException(String.format("joke with id : %s is null", postId+""));
        }
        log.debug(String.format("dislike post[%s]", joke.getId()));
        joke.setDislikes(joke.getDislikes()+ 1);
        db.merge(joke);
        return joke.getDislikes();
    }

    @Override
    public int numOfPosts(User user) {
        log.debug(String.format("Number of posts of user[%s]", user.getScreenName()));
        try {
            return (Integer) db.createQuery("SELECT COUNT(j) FROM Joke j WHERE j.user.id = :uid").setParameter("uid", user.getId()).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Object[]> getLatestPosts(int start, int amount) {
        return db.createQuery("SELECT j.id,j.post,j.likes,j.dislikes,j.editedDate,u.screenName,u.profile.picture FROM Joke j,User u WHERE j.blocked != TRUE AND j.user.id = u.id ORDER BY j.editedDate DESC", Object[].class)
                .setFirstResult(start).setMaxResults(amount).getResultList();
    }

    @Override
    public Comment saveComment(String commentStr, Long postId) throws PersistenceException, PostException {
        //make sure user is logged in
        User userLoggedIn = id.getUserLoggedIn();
        if (userLoggedIn == null) {
            throw new PostException("No user in session");
        }
        //find joke
        Joke joke = db.find(Joke.class, postId);
        //include comment and save
        if (joke == null) {
            throw new PostException("Joke with id not found : "+postId);
        }
        //create comment
        JKComment comment = new JKComment();
        comment.setJoke(joke);
        comment.setText(commentStr);
        comment.setUser(userLoggedIn);
        db.persist(comment);
        log.debug(String.format("comment saved to joke : %s", postId+""));
        return comment;
    }

    
    public List<Object[]> getComments(Long postId) throws PersistenceException, PostException {
        if(postId == null){
            throw new PostException("post id cannot be null");
        }
        return db.createQuery("SELECT c.id,c.postedTime,c.text,c.user FROM JKComment c WHERE c.joke.id = :postId", Object[].class)
                .setParameter("postId", postId).getResultList();
    }

    @Override
    public void reportPost(Long postId,String[] reasons) throws PersistenceException {
        log.debug(String.format("reportPost(%s,%s)", postId+"",Arrays.toString(reasons)));
        Report r = new Report();
        r.setPostClass(Joke.class.getName());
        r.setPostId(postId);
        r.setReasons(Arrays.toString(reasons));
        r.setReporter(id.getUserLoggedIn());
        db.persist(r);
    }

}
