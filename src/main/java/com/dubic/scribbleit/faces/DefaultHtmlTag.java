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
public class DefaultHtmlTag extends SimpleTagSupport {

    protected String id;
    protected boolean rendered;

    /**
     * Called by the container to invoke this tag. The implementation of this
     * method is provided by the tag library developer, and handles all tag
     * processing, body iteration, etc.
     */
    @Override
    public void doTag() throws JspException {
        JspWriter out = getJspContext().getOut();

//        try {
            // TODO: insert code to write html before writing the body content.
            // e.g.:
            //
            // out.println("<strong>" + attribute_1 + "</strong>");
            // out.println("    <blockquote>");
            if (!rendered) {
                return;
            }
//            JspFragment f = getJspBody();
//            if (f != null) {
//                f.invoke(out);
//            }

            // TODO: insert code to write html after writing the body content.
            // e.g.:
            //
            // out.println("    </blockquote>");
//        } catch (java.io.IOException ex) {
//            throw new JspException("Error in DefaultHtmlTag tag", ex);
//        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

}
