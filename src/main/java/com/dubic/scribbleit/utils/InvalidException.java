/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dubic.scribbleit.utils;

/**
 *
 * @author dubem
 */
public class InvalidException extends Exception {

    /**
     * Creates a new instance of <code>InvalidException</code> without detail
     * message.
     */
    public InvalidException() {
    }

    /**
     * Constructs an instance of <code>InvalidException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidException(String msg) {
        super(msg);
    }
}
