/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**{
   "id": "888701074502643",
   "email": "dubeuzuegbu\u0040yahoo.com.au",
   "first_name": "Dubic",
   "gender": "male",
   "last_name": "Uzuegbu",
   "link": "https://www.facebook.com/app_scoped_user_id/888701074502643/",
   "locale": "en_GB",
   "name": "Dubic Uzuegbu",
   "timezone": 1,
   "updated_time": "2015-06-07T08:02:37+0000",
   "verified": true
}
 *
 * @author Dubic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FBMe {
    private String id,email,first_name,last_name,name;
    private FBError error;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FBError getError() {
        return error;
    }

    public void setError(FBError error) {
        this.error = error;
    }
    
}
