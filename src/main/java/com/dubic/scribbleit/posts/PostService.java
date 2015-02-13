/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.posts;

import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.models.Comment;
import com.dubic.scribbleit.models.Post;
import com.dubic.scribbleit.models.Tag;
import java.util.List;
import javax.persistence.PersistenceException;

/**
 * interface for all posts
 *
 * @author dubem
 */
public interface PostService {

    public Post savePost(String post) throws PersistenceException, PostException;

    public Comment saveComment(String comment, Long postId) throws PersistenceException, PostException;

//    public List<Comment> getComments(Long postId) throws PersistenceException, PostException;
    public Post updatePost(Post post) throws PersistenceException;

    public void blockPost(Long postId) throws PersistenceException;

    public void reportPost(Long postId,String reasons,String ip) throws PersistenceException;

    public void watchTag(User user, Tag tag) throws PersistenceException;

    public void removeWatch(Long id) throws PersistenceException;

    public Post like(Long postId) throws PersistenceException;

    public Post dislike(Long postId) throws PersistenceException;

    public int numOfPosts(User user);

}
