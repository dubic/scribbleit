/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import java.util.Date;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author dubem
 */
@Controller
public class HomeController {

    private final Logger log = Logger.getLogger(getClass());

    public HomeController() {
    }

    @RequestMapping("/app")
    public String welcome() {
        return "app";
    }

    /**
     * handles requests for posts from shared sites
     *
     * @param type 'j,p, or q for jokes,proverbs,and quotes
     * @param id post id
     * @return
     */
    @RequestMapping("/p/{type}/{id}")
    public String postsPublic(@PathVariable("type") String type, @PathVariable("id") Long id) {
        String url = "redirect:/#/home/jokes";
        if ("j".equalsIgnoreCase(type)) {
            url = "redirect:/#/home/jokes?id=" + id;
        } else if ("p".equalsIgnoreCase(type)) {
            url = "redirect:/#/home/proverbs?id=" + id;
        } else if ("q".equalsIgnoreCase(type)) {
            url = "redirect:/#/home/quotes?id=" + id;
        }
        log.infof("shared posts type[%s] id[%d]", type,id);
        return url;
    }

    @RequestMapping("/angular")
    public ModelAndView angularView() {
        ModelAndView model = new ModelAndView("angular");
        return model;
    }
}
