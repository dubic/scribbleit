/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dubic.scribbleit.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author dubem
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {
    
    @RequestMapping("/home")
    public String profileTemplate() {
        return "users/profile";
    }
    @RequestMapping("/activity")
    public String profileActivity() {
        return "users/activity";
    }
    @RequestMapping("/account")
    public String profileAccount() {
        return "users/account";
    }
    @RequestMapping("/pword")
    public String profileChangePassword() {
        return "users/change-password";
    }
}
