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
import org.apache.log4j.Logger;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

/**
 *
 * @author dubem
 */
public class AuthFailureHandler implements AuthenticationFailureHandler {

    private final Logger log = Logger.getLogger(getClass());

    @Override
    public void onAuthenticationFailure(HttpServletRequest hsr, HttpServletResponse hsr1, AuthenticationException ae) throws IOException, ServletException {
        log.info("AUTH failed : " + ae.getClass());
        if (ae instanceof ProviderNotFoundException) {
            hsr1.setStatus(HttpServletResponse.SC_NOT_FOUND);//404
        } else if (ae instanceof DisabledException) {
            hsr1.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);//501
        } else if (ae instanceof LockedException) {
           hsr1.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
        } else if (ae instanceof SessionAuthenticationException) {
            hsr1.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);//408
        } else {
            hsr1.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
        }
    }

}
