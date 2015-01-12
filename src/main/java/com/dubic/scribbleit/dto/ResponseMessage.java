/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dubic.scribbleit.dto;

/**
 *
 * @author dubem
 */
public class ResponseMessage {
     private int code;
     private String msg;

    public ResponseMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

     
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
     
     
}
