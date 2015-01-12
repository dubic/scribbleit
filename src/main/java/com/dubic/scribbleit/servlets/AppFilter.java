/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.servlets;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import org.apache.log4j.Logger;

/**
 *
 * @author dubem
 */
//@WebFilter(urlPatterns = {"/*"})
public class AppFilter implements Filter {

    private final Logger log = Logger.getLogger(getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("Application Filter Starting.........");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
    }

    @Override
    public void destroy() {
        log.debug("Application Filter destroying...........");
    }

}
