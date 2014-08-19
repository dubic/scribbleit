/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.idm.spi;

/**
 *
 * @author dubem
 */
public class GroupServiceException extends Exception{

    public GroupServiceException() {
    }

    public GroupServiceException(String message) {
        super(message);
    }

    public GroupServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
