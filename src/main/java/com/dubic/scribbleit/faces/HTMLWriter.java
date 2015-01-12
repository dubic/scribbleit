/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.faces;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;

/**
 *
 * @author dubem
 */
public class HTMLWriter {

    private boolean tagOpen = false;

    public void startElement(JspWriter out, String name) throws IOException {
        if (name == null) {
            return;
        }
        closeTag(out);
        out.append("<").append(name);
        tagOpen = true;
    }

    public void attribute(JspWriter out, String name, String value) throws IOException {
        if (name == null || value == null) {
            return;
        }
        if (!tagOpen) {
            throw new IllegalStateException("cannot add an attribute to a closed tag");
        }
        out.append(" ").append(name).append("=\"").append(value).append("\"");
        tagOpen = true;
    }

    public void text(JspWriter out, String text) throws IOException {
        if (text == null) {
            return;
        }
        closeTag(out);
        out.append(text);
    }

    public void endElement(JspWriter out, String name) throws IOException {
        if (name == null) {
            return;
        }
        closeTag(out);
        out.append("</").append(name).append(">");
    }

    public void fragment(JspWriter out, JspFragment f) throws IOException, JspException {
        if (f != null) {
            closeTag(out);
            f.invoke(out);
        }
    }

    private void closeTag(JspWriter out) throws IOException {
        if (tagOpen) {
            out.append(">");
            tagOpen = false;
        }
    }

}
