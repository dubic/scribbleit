/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dubic.scribbleit.rest;

import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author dubem
 */
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        register(com.dubic.scribbleit.rest.TestResource.class);
        register(com.dubic.scribbleit.rest.PostsResource.class);

    }

    
}
