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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @author dubem
 */
@Component("logoutRef")
public class LogoutHandler implements LogoutSuccessHandler{

    @Override
    public void onLogoutSuccess(HttpServletRequest hsr, HttpServletResponse resp, Authentication a) throws IOException, ServletException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    
}
