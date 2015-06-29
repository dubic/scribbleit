/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

/**
 *
 * @author dubem
 */
@Table(name = "report")
@Entity
public class Report implements Serializable {

    private Long id;
    private Long postId;
    private boolean offensive, sexual, violent, hateful, harmful, childAbuse, spam = false;
    private User reporter;
    private Date createDate = new Date();
    private Date handledDate;
    private String ip;
    private Action action;

    public enum Action {

        IGNORED, BLOCKED, NEW
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

    @NotNull(message = "reported post must not be null")
    @Column(name = "post_id", nullable = false)
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    public User getReporter() {
        return reporter;
    }

    @Column(name = "offensive")
    public boolean isOffensive() {
        return offensive;
    }

    public void setOffensive(boolean offensive) {
        this.offensive = offensive;
    }

    @Column(name = "sexual")
    public boolean isSexual() {
        return sexual;
    }

    public void setSexual(boolean sexual) {
        this.sexual = sexual;
    }

    @Column(name = "violent")
    public boolean isViolent() {
        return violent;
    }

    public void setViolent(boolean violent) {
        this.violent = violent;
    }

    @Column(name = "hateful")
    public boolean isHateful() {
        return hateful;
    }

    public void setHateful(boolean hateful) {
        this.hateful = hateful;
    }

    @Column(name = "harmful")
    public boolean isHarmful() {
        return harmful;
    }

    public void setHarmful(boolean harmful) {
        this.harmful = harmful;
    }

    @Column(name = "childAbuse")
    public boolean isChildAbuse() {
        return childAbuse;
    }

    public void setChildAbuse(boolean childAbuse) {
        this.childAbuse = childAbuse;
    }

    @Column(name = "spam")
    public boolean isSpam() {
        return spam;
    }

    public void setSpam(boolean spam) {
        this.spam = spam;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    @NotNull(message = "create date must be provided.check code")
    @Column(name = "create_dt", nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "handled_dt")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getHandledDate() {
        return handledDate;
    }

    public void setHandledDate(Date handledDate) {
        this.handledDate = handledDate;
    }

    @Column(name = "action_taken")
    @Enumerated(EnumType.STRING)
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Column(name = "ip", length = 20)
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
