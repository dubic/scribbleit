/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.faces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;

/**
 *
 * @author dubem
 */
public class Element {

    private String name;
    private final List<Attribute> attributes = new ArrayList<Attribute>();
    private final List<Element> children = new ArrayList<Element>();
    private JspFragment fragment;
    private String text;

    public Element(String name) {
        this.name = name;
    }

    public Element(String name, JspFragment f) {
        this.name = name;
        this.fragment = f;
    }

    public Element addAttribute(String name, String value) {
        this.attributes.add(new Attribute(name, value));
        return this;
    }

    public Element addAttribute(Attribute attr) {
        this.attributes.add(attr);
        return this;
    }
    public Element text(String text) {
        this.text = text;
        return this;
    }

    /**
     * creates a new child element with the name and adds it to this element.
     *
     * @param name
     * @return the added child for further DOM iteration/cascading
     */
    public Element child(String name) {
        Element child = new Element(name);
        this.children.add(child);
        return child;
    }

    /**
     * adds the element to the children of this element.
     *
     * @param child
     * @return the current element
     */
    public Element addChild(Element child) {
        this.children.add(child);
        return this;
    }

    @Override
    public String toString() {
        StringBuffer html = new StringBuffer();
        html.append("<").append(this.name);
        //append attributes
        if (!this.attributes.isEmpty()) {
            for (Attribute attr : attributes) {
                html.append(" ").append(attr.getName()).append("=").append("\"").append(attr.getValue()).append("\"");
            }
        }
        if (!this.children.isEmpty()) {
            html.append(">").append("\n");
            for (Element child : this.children) {
                html.append(child.toString());
            }
            html.append("\n</").append(this.name).append(">");
        } else {
            html.append("/>");
        }

        return html.toString();
    }

    public void writeJsp(JspWriter out) throws IOException, JspException {

        out.append("<").append(this.name);
        //append attributes
        if (!this.attributes.isEmpty()) {
            for (Attribute attr : attributes) {
                if (attr.getValue() == null) {
                    continue;
                }
                out.append(" ").append(attr.getName()).append("=").append("\"").append(attr.getValue()).append("\"");
            }
        }
        if (!this.children.isEmpty() || this.fragment != null || this.text != null) {
            out.append(">");
            if (text != null) {
                out.append(text);
            }
            for (Element child : this.children) {
                out.append(child.toString());
            }
            //add fragment
            if (this.fragment != null) {
                this.fragment.invoke(out);
            }
            //close
            out.append("</").append(this.name).append(">");
        } else {
            out.append("/>");
        }
    }

}
