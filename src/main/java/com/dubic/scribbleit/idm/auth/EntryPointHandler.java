/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dubic.scribbleit.idm.auth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 *
 * @author dubem
 */
@Component("entryPointRef")
public class EntryPointHandler implements AuthenticationEntryPoint{

     @Override
    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException ae) throws IOException, ServletException {
        System.out.println("EntryPointHandler called..."+req.getRequestURI()+"?"+req.getQueryString());
        resp.sendError(403, "this url is forbidden");
    }
    
}
