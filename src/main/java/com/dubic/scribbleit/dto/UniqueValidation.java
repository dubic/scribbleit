/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dubic.scribbleit.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 *
 * @author dubem
 */
public class UniqueValidation {
    private String value;

//    private boolean isValid;
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public static void main(String[] ghgh){
        JsonObject resp = new JsonObject();
        resp.addProperty("value", "dubic");
        resp.addProperty("isValid", true);
        System.out.println(new Gson().toJson(resp));
//        System.out.println(resp.getClass().getName());
    }
}
