/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.servlets;

import com.dubic.scribbleit.idm.models.User;
import com.dubic.scribbleit.idm.spi.IdentityService;
import com.dubic.scribbleit.idm.spi.IdentityServiceImpl;
import com.dubic.scribbleit.idm.spi.InvalidTokenException;
import com.dubic.scribbleit.idm.spi.LinkExpiredException;
import java.io.IOException;
import java.util.logging.Level;
import javax.persistence.PersistenceException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author dubem
 */
@WebServlet(name = "Activation", urlPatterns = {"/activate"})
public class Activation extends HttpServlet {

    private final Logger log = Logger.getLogger(getClass());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String token = request.getParameter("t");

        WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
//        for (String name : appContext.getBeanDefinitionNames()) {
//            System.out.println("bean def name = "+name);
//        }
        IdentityService identityService = (IdentityService) appContext.getBean("identityService");
        try {
            User activatedUser = identityService.activateUser(token);
            //redirect to success activation
            request.setAttribute("error", false);
        } catch (InvalidTokenException ex) {
            request.setAttribute("error", true);
            request.setAttribute("linkError", Boolean.TRUE);
            log.error(ex.getMessage());
        } catch (LinkExpiredException ex) {
            request.setAttribute("error", true);
            request.setAttribute("linkExpired", Boolean.TRUE);
            log.error(ex.getMessage());
        } catch (Exception ex) {
            request.setAttribute("error", true);
            request.setAttribute("Service error, Try some time later", Boolean.TRUE);
            log.error(ex.getMessage());
        }
        request.setAttribute("text", "testing to see");
            request.getRequestDispatcher("activation.jsf").forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
