/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.dto;

import java.util.Calendar;
import java.util.Date;

/**
 * DTO class for user activation parameters
 *
 * @author dubic
 * @since idm 1.0.0
 */
public class UserActivation {

    private Long id;
    private Date expDate;

    public UserActivation() {
    }

    
    public UserActivation(Long id) {
        this.id = id;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.expDate = cal.getTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public boolean expired() {
        return new Date().after(expDate);
    }

    @Override
    public String toString() {
        return "UserActivation{" + "id=" + id + ", expDate=" + expDate + '}';
    }
    
    
}
