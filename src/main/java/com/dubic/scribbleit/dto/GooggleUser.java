/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**{
 "sub": "116068384741792299174",
 "name": "Dubem Uzuegbu",
 "given_name": "Dubem",
 "family_name": "Uzuegbu",
 "picture": "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg",
 "email": "dubem.uzuegbu@crowninteractive.com",
 "email_verified": true,
 "locale": "en-US",
 "hd": "crowninteractive.com"
}
 *
 * @author Dubic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GooggleUser {
    private String sub,name,given_name,family_name,picture,email;

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
