/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**Facebook error class from token validation call
 * 
 * "error": {
      "message": "Error validating access token: Session has expired on Wednesday, 05-Aug-15 14:00:00 PDT. The current time is Thursday, 06-Aug-15 04:35:45 PDT.",
      "type": "OAuthException",
      "code": 190,
      "error_subcode": 463
   }
 *
 * @author Dubic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FBError {
    private String message,type;
    private int code,error_subcode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getError_subcode() {
        return error_subcode;
    }

    public void setError_subcode(int error_subcode) {
        this.error_subcode = error_subcode;
    }
    
    
}
