/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.rest;

import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.idm.spi.IdentityService;
import com.dubic.scribbleit.email.MailServiceImpl;
import com.dubic.scribbleit.email.SimpleMailEvent;
import com.dubic.scribbleit.jms.Messager;
import com.dubic.scribbleit.models.TRef;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * REST Web Service
 *
 * @author dubem
 */
@Path("test2")
public class TestResource {
    

    @Context
    private UriInfo context;
    @Autowired
    private IdentityService id;
    @Autowired
    private Database db;
    @Autowired
    private Messager messager;
    @Autowired
    private MailServiceImpl mailer;
    private Logger log = Logger.getLogger(getClass());

    /**
     * Creates a new instance of TestResource
     */
    public TestResource() {
    }

    /**
     * Retrieves representation of an instance of
     * com.dubic.scribbleit.TestResource
     *
     * @param msg
     * @return an instance of java.lang.String
     */
    @GET
    @Path("tget/{msg}")
    @Produces("application/json")
    public String getJson(@PathParam("msg") String msg) {
        //TODO return proper representation object
//        return new Gson().toJson(id.findUserByEmail("udubic@gmail.com"));
        messager.send(msg);
        return "sent message";
    }

    @GET
    @Path("pram")
    @Produces("application/json")
    public String queryparam() {
        //TODO return proper representation object

        return messager.receive();
    }

    @GET
    @Path("mail")
    @Produces("application/json")
    public String testMail(@QueryParam("q") String email) {
        
            SimpleMailEvent mail = new SimpleMailEvent(email);
            mail.setSubject("A test mailer");
            Map model = new HashMap();
            model.put("name", "dubic uzuegbu");
            model.put("url", "http://localhost:7070/scribbleit/activate" + "?t=" + "iduiud");
            mailer.sendMail(mail, "reg.vm", model);
        
        return "true";
    }

    @GET
    @Path("tref")
    @Produces("application/json")
    public String treftest() {
        //TODO return proper representation object

        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    System.out.println("saved tref - " + getTref().getRef());
                }
            }, "thread " + i).start();
        }

        return new Gson().toJson("completed");
    }

    /**
     * PUT method for updating or creating an instance of TestResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }

    private synchronized TRef getTref() {
        Random r = new Random();
        try {
            Thread.sleep(r.nextInt(500));
        } catch (InterruptedException ex) {
            log.error(ex.getMessage());
        }
        TRef tref = db.find(TRef.class, 1);
        tref.setRef(tref.getRef() + 1);
        db.merge(tref);
        return tref;
    }
}
