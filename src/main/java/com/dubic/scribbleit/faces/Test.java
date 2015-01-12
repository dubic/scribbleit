/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.faces;

import com.google.gson.Gson;

/**
 *
 * @author dubem
 */
public class Test {

    public static void main(String[] arrg) {
        Element e = new Element("div")
                .addAttribute("name", "dubic")
                .addAttribute("class", "form-control")
                .addChild(new Element("input")
                        .addAttribute("type", "text"));
//         System.out.println(new Gson().toJson(e));
//         System.out.println(new Gson().toJson(ch));
//        e.toHtmlString(new StringBuffer());
        System.out.println(e.toString());
    }
}
