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
//        if (test == null) {
//            throw new IllegalArgumentException("No test property");
//        }
    }

    public Validate notEmptyString(String msg) throws InvalidException {
        if (test != null) {
            if (test.toString().trim().length() > 0) {
                return this;
            }
        }
        throw new InvalidException(msg);
    }

    public Validate notLessthan(Number n, String msg) throws InvalidException, ClassCastException {
        nullTest(n, msg);
        if (test instanceof Integer) {
            if (((Integer) test) < n.intValue()) {
                throw new InvalidException(msg);
            }
        } else if (test instanceof Double) {
            if (((Double) test) < n.doubleValue()) {
                throw new InvalidException(msg);
            }
        } else if (test instanceof Long) {
            if (((Long) test) < n.longValue()) {
                throw new InvalidException(msg);
            }
        }
        return this;
    }

    private void nullTest(Object o, String msg) throws InvalidException {
        if ((test == null) || (test.toString().trim().length() <= 0)) {
            throw new InvalidException(msg);
        }
    }

}
