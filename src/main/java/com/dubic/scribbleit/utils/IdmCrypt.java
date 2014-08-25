/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Cryptographic utilities class
 *
 * @author dubem
 */
public class IdmCrypt {

    private static final String constant = "dubine";
    private static String testname;
//    private static String testname = ResourceBundle.getBundle("english", Locale.getDefault(), new URL[]{new URLClassLoader(new File("C:/temp/conf.properties").toURI().toURL())}).getString("database.driver");

    public static String encrypt(String password) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static String encodeMD5(String data, String salt) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException("data to encode cannot be null");
        }
        return DigestUtils.md5Hex(salt + constant + data);
    }
 
    public static void main(String[] atyty) {
 String[] r = new String[]{"salacious","vulgar","offensive"};
//        System.out.println(join(r));
    }
}
