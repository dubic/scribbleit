package com.dubic.scribbleit.dto;

import java.util.ArrayList;
import java.util.List;

public class Response {

   
    private List<ResponseMessage> msgs = new ArrayList<ResponseMessage>();
    private Object data;

    public Response() {
    }

    public Response(Exception ex) {
        this.msgs.add(new ResponseMessage(-1, ex.getMessage()));
    }

    public Response(Exception ex, String msg) {
        this.msgs.add(new ResponseMessage(-1, msg));
    }

    public Response(int code, String msg) {
        this.msgs.add(new ResponseMessage(code, msg));
    }

    public Response msg(int code,String msg, Object... params) {
        this.msgs.add(new ResponseMessage(code, String.format(msg, params)));
        return this;
    }

    public Response data(Object data) {
        this.data = data;
        return this;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
