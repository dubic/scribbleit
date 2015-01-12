/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.idm.spi;

/**
 *
 * @author dubem
 */
public class IdentityServiceException extends RuntimeException{

    public IdentityServiceException() {
    }

    public IdentityServiceException(String message) {
        super(message);
    }

    public IdentityServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
