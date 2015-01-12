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
public class RegistrationController {

    @RequestMapping("/reg")
    public String regHome() {
        return "registration";
    }

    @RequestMapping("/login-view")
    public String loginView() {
        return "login";
    }

    @RequestMapping("/signup-view")
    public String signupView() {
        return "signup";
    }
    @RequestMapping("/forgot-password-view")
    public String fpasswordView() {
        return "forgot-password";
    }
}
