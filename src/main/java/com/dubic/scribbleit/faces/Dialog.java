/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dubic.scribbleit.faces;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author dubem
 */
public class Dialog extends SimpleTagSupport {
    private String id;
    private String styleClass;
    private boolean rendered;
    private String width;
    private String effect;

    /**
     * Called by the container to invoke this tag. The implementation of this
     * method is provided by the tag library developer, and handles all tag
     * processing, body iteration, etc.
     */
    @Override
    public void doTag() throws JspException {
        JspWriter out = getJspContext().getOut();
        
        try {
            // TODO: insert code to write html before writing the body content.
            // e.g.:
            //
            // out.println("<strong>" + attribute_1 + "</strong>");
            // out.println("    <blockquote>");
            if(!rendered){
                return;
            }
            HTMLWriter w = new HTMLWriter();
            w.startElement(out, "div");
            w.attribute(out, "id", id);
            w.attribute(out, "class", "modal "+effect);
            w.attribute(out, "tabindex", "-1");
            w.attribute(out, "role", "dialog");
            w.attribute(out, "aria-labelledby","basicModal");
            w.attribute(out, "aria-hidden","true");
            w.startElement(out, "div");
            w.attribute(out, "class", "modal-dialog");
            w.attribute(out, "style","width: 350px");
            
            w.fragment(out, getJspBody());

            w.endElement(out, "div");
            w.endElement(out, "div");
            // TODO: insert code to write html after writing the body content.
            // e.g.:
            //
            // out.println("    </blockquote>");
        } catch (java.io.IOException ex) {
            throw new JspException("Error in Dialog tag", ex);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
    
}
