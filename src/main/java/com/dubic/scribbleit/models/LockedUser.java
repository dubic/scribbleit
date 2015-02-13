/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.models;

import com.dubic.scribbleit.models.User;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author dubem
 */
@Entity
@Table(name = "locked_user")
public class LockedUser implements Serializable {

    private Long id;
    private String reason;
    private Date when = new Date();
    private User user;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotEmpty(message = "lock reason must not be null or empty")
    @Max(value = 255, message = "lock reason must not be more than 255 characters")
    @Column(name = "reason", length = 255, nullable = false)
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Column(name = "lock_dt", nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    @NotNull(message = "provide a user to lock")
    @JoinColumn(name = "user_id",nullable = false)
    @OneToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
