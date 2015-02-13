/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Cryptographic utilities class
 *
 * @author dubem
 */
public class IdmCrypt {

    private static final String constant = "dubine";
    private static String testname;
    private static int ap = 0;
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
    
    public static String generateTimeToken() {
        char[] time = String.valueOf(System.currentTimeMillis()).toCharArray();
        List<char[]> charrList = new ArrayList<char[]>();
        charrList.add(new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'});
        charrList.add(new char[]{'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T'});
        charrList.add(new char[]{'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3'});
        charrList.add(new char[]{'4', '5', '6', '7', '8', '9'});

        StringBuffer tokenBuf = new StringBuffer();
        for (int i = 0; i < time.length; i++) {
            ap = ap >= charrList.size() ? 0 : ap;
            int chPos = Character.digit(time[i], Character.MAX_RADIX);
            if (chPos >= charrList.get(ap).length) {
                tokenBuf.append(chPos);
            } else {
                tokenBuf.append(charrList.get(ap)[chPos]);
            }
            ap++;
        }
        return tokenBuf.toString();
    }
    
    public static String castNull(Object so) {
        String s = (String)so;
        return s;
    }
 
    public static void main(String[] atyty) {
// String[] r = new String[]{"salacious","vulgar","offensive"};
        System.out.println(castNull(null));
    }
}
