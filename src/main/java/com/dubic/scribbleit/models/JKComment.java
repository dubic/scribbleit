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
@Table(name = "jk_comment")
public class JKComment extends Comment implements Serializable {

    private Joke joke;

    public JKComment() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotEmpty(message = "comment must not be a null value")
    @Lob
    @Column(name = "text", nullable = false)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @NotNull(message = "posted time must be included")
    @Column(name = "posted")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Calendar getPostedTime() {
        return postedTime;
    }

    public void setPostedTime(Calendar postedTime) {
        this.postedTime = postedTime;
    }

    @NotNull(message = "this comment is not assigned to a joke")
    @Valid
    @ManyToOne
    @JoinColumn(name = "joke_id", nullable = false)
    public Joke getJoke() {
        return joke;
    }

    public void setJoke(Joke joke) {
        this.joke = joke;
    }

    @NotNull(message = "this comment is not assigned to a user")
    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
