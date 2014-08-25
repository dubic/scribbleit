/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dubic.scribbleit.models;

import com.dubic.scribbleit.idm.models.User;
import java.util.Calendar;

/**
 *
 * @author dubem
 */
public abstract class Comment {
    protected Long id;
    protected String text;
    protected Calendar postedTime = Calendar.getInstance();
    protected User user;
}
