/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.idm.auth;

import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.idm.spi.IdentityService;
import com.dubic.scribbleit.utils.IdmCrypt;
import com.google.gson.Gson;
import java.util.Date;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author dubem
 */
//@Named
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private IdentityService userService;

    @Override
    public Authentication authenticate(Authentication a) throws AuthenticationException {
        log.info(new Gson().toJson(a));
        log.debug(String.format("authenticating...[%s]",a.getName().toLowerCase()));
        String userId = a.getName().toLowerCase();
        User user = userService.findUserByEmailandPasword(userId, IdmCrypt.encodeMD5(a.getCredentials().toString().trim(), userId.toLowerCase().trim()));
        if (user != null) {
            if (!user.isActivated()) {
                throw new DisabledException("User account is not activated - " + userId);
            }
//            if (user.isLocked()) {
//                throw new LockedException("User accountis locked - " + userId);
//            }
            
           
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), a.getCredentials().toString(), user.getRoles());
            SecurityContextHolder.getContext().setAuthentication(auth);
            try {
                //set last login date
                user.setLastLoginDate(new Date());
                userService.updateUser(user);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
            return auth;
        } else {
            throw new ProviderNotFoundException("User not found - " + userId);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
