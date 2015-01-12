/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dubic.scribbleit.dto;

import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author dubem
 */
public class Registration {
    @Size(min = 3,max = 20,message = "screen name must be between 3 and 20 characters")
    @NotEmpty(message = "screen name must not be empty")
    private String screenName;
    @NotEmpty(message = "Email must not be empty")
    private String email;
    @NotEmpty(message = "Password is required")
    @Size(min = 5,max = 20,message = "Password must be between 5 and 20 characters")
    private String password;
    private String vpword;

    public Registration() {
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVpword() {
        return vpword;
    }

    public void setVpword(String vpword) {
        this.vpword = vpword;
    }
    
    
}
