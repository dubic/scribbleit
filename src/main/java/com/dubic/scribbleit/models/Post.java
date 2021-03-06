/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.models;

import com.dubic.scribbleit.dto.PostData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

/**
 *
 * @author dubem
 */
@Entity
@Table(name = "posts")
public class Post implements Serializable {

    public enum Type {

        JOKE, PROVERB, QUOTE
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "source")
    private String source;

    @Lob
    @Column(name = "post")
    private String text;

    @Column(name = "tags")
    private String tags;//comma separated list

    @NotNull(message = "you must be logged in to post a joke")
    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull(message = "posted date must be set")
    @Column(name = "posted_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar postedDate = Calendar.getInstance();

    @Column(name = "blocked")
    private boolean blocked;

    @NotNull(message = "Post type must be specified")
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;
    
    @Column(name = "media_id",nullable = true)
    private String media;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<MediaItem> files = new ArrayList<MediaItem>();

//    ======================================================================
    public Post() {
    }

    public Post(PostData d) {
        this.text = d.getPost();
        this.source = d.getSource();
        if (d.getTags() == null) {
            this.tags = "";//split method will be called to avoid null pointer exception
        } else {
            this.tags = d.getTags();
        }
        this.title = d.getTitle();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Calendar getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Calendar postedDate) {
        this.postedDate = postedDate;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }



}
