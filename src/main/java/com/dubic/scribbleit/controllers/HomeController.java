/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import java.util.Date;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author dubem
 */
@Controller
public class HomeController {

    public HomeController() {
    }

    @RequestMapping("/app")
    public String welcome() {
        return "app";
    }
    

    @RequestMapping("/angular")
    public ModelAndView angularView() {
        ModelAndView model = new ModelAndView("angular");
        return model;
    }
}
