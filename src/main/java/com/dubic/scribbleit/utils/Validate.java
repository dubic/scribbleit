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
public class Validate {

    private Object test;

    public Validate(Object test) {
        this.test = test;
        if (test == null) {
            throw new IllegalArgumentException("No test property");
        }
    }

    public Validate notEmpty(String msg) throws InvalidException {
        if (test instanceof String) {
            if ((test == null) || (test.toString().trim().length() <= 0)) {
                throw new InvalidException(msg);
            }
        } else {
            test = (String) test;
        }
        return this;
    }

    public Validate notLessthan(Number n, String msg) throws InvalidException, ClassCastException {

        if (test instanceof Integer) {
            if (((Integer) test).intValue() < n.intValue()) {
                throw new InvalidException(msg);
            }
        } else if (test instanceof Double) {
            if (((Double) test).doubleValue() < n.doubleValue()) {
                throw new InvalidException(msg);
            }
        } else if (test instanceof Long) {
            if (((Long) test).longValue() < n.longValue()) {
                throw new InvalidException(msg);
            }
        }
        return this;
    }

}
