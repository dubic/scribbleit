/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.models;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author dubem
 */
@Entity
@Table(name = "comments")
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotEmpty(message = "comment must not be a null value")
    @Lob
    @Column(name = "text", nullable = false)
    private String text;

    @NotNull(message = "posted time must be included")
    @Column(name = "posted")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar postedTime = Calendar.getInstance();

    @NotNull(message = "this comment is not assigned to a user")
    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne
    private User user;

    @NotNull(message = "this comment is not assigned to a post")
    @Valid
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
//    =================================================
    
    public Comment() {
        
    }
    public Comment(Post p) {
        this.post = p;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Calendar getPostedTime() {
        return postedTime;
    }

    public void setPostedTime(Calendar postedTime) {
        this.postedTime = postedTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
    
}
