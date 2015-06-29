/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
        String s = (String) so;
        return s;
    }

    public static void main(String[] atyty) throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<proxy>\n"
                +   "<retn>0</retn>\n"
                +   "<desc>Operation successfully.</desc>\n"
                +   "<result>\n"
                +       "<balanceDesc>Prepaid_Balance_Subaccount</balanceDesc>\n"
                +       "<balance>299</balance>\n"
                +       "<minMeasureId>101</minMeasureId>\n"
                +       "<unitType>1</unitType>\n"
                +       "<accountType>2000</accountType>\n"
                +       "<startTime>20120111083937</startTime>\n"
                +       "<expireTime>20370101000000</expireTime>\n"
                +       "<accountCredit>0</accountCredit>\n"
                +       "<accountKey>999000005004041606</accountKey>\n"
                +       "<productID/>\n"
                +   "</result>\n"
                + "</proxy>";
        DocumentBuilderFactory builderFactory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        builder = builderFactory.newDocumentBuilder(); 
        Document xmlDocument = builder.parse(new ByteArrayInputStream(xml.getBytes()));

        XPath xPath = XPathFactory.newInstance().newXPath();
//        String expression = "/Employees/Employee[@emplid='3333']/email";
        String expression = "/proxy/result/balanceDesc";

//read a string value
//        Node node = (Node) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODE);
//        System.out.println("bal desc : "+node.getNodeName());
        
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            System.out.println(nodeList.item(i).getFirstChild().getNodeValue());
        }
//        UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl("http://10.10.210.72:8088/ccbsproxy/");
//        url.queryParam("appid", "imanager").queryParam("action", "queryocs").queryParam("msisdn", "07045687509").queryParam("type", 0);
//
//        System.out.println(url.build().toString());
    }
}
