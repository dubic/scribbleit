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
public class BootFormInput extends SimpleTagSupport {

    private String label;
    private boolean rendered;
    private String icon;

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
            if (!rendered) {
                return;
            }
            HTMLWriter w = new HTMLWriter();
            w.startElement(out, "div");
            w.attribute(out, "class", "form-group");
            w.startElement(out, "label");
            w.attribute(out, "class", "control-label visible-ie8 visible-ie9");
            w.text(out, label);
            w.endElement(out, "label");
            w.startElement(out, "div");
            w.attribute(out, "class", "input-icon");
            w.startElement(out, "i");
            w.attribute(out, "class", icon);
            w.endElement(out, "i");
            
            JspFragment f = getJspBody();
            if (f != null) {
                f.invoke(out);
            }

            w.endElement(out, "div");
            w.endElement(out, "div");
            // TODO: insert code to write html after writing the body content.
            // e.g.:
            //
            // out.println("    </blockquote>");
        } catch (java.io.IOException ex) {
            throw new JspException("Error in BootFormInput tag", ex);
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
