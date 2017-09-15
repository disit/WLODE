package org.disit.wlode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mirco Soderi @ DISIT DINFO UNIFI (mirco.soderi at unifi dot it)
 */
public class GraphProvider extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String ontology = request.getParameter("ontology");
        String owlClass = request.getParameter("class");
        String lang = request.getParameter("lang");
        String imgtype = request.getParameter("imgtype");
        
        URL graphUrl;
                
        // Look for static graph
        try {
            if(owlClass != null) 
                graphUrl = new URL(getServletConfig().
                    getInitParameter("staticGraphsBaseURL").
                    concat(lang).
                    concat("/").
                    concat(owlClass.replaceAll("[^A-Za-z0-9_]", "")).
                    concat(".").
                    concat(imgtype));
            else 
                graphUrl = new URL(getServletConfig().
                    getInitParameter("staticGraphsBaseURL").
                    concat(lang).
                    concat("/").
                    concat(ontology.replaceAll("[^A-Za-z0-9]", "")).
                    concat(".").
                    concat(imgtype));
            try ( InputStream in = graphUrl.openStream()) {
                response.setContentType(getMimeType(imgtype));
                pipe(in,response.getOutputStream());
            }
        } catch(IOException e) { 
            graphUrl = null; 
        }
        
        if(graphUrl == null) { // if failed, attempt to load dynamically
            try {
            if(owlClass != null) 
                graphUrl = new URL(getServletConfig().
                    getInitParameter("classGraphServiceLocation").
                    concat("ontology=").concat(URLEncoder.encode(ontology,"UTF-8")).
                    concat("&class=").concat(URLEncoder.encode(owlClass,"UTF-8")).
                    concat("&lang=").concat(lang).
                    concat("&out=svg"));
            else 
                graphUrl = new URL(getServletConfig().
                    getInitParameter("ontologyGraphServiceLocation").
                    concat("ontology=").concat(URLEncoder.encode(ontology,"UTF-8")).
                    concat("&lang=").concat(lang).
                    concat("&out=svg"));
            try ( InputStream in = graphUrl.openStream()) {
                response.setContentType(getMimeType(imgtype));
                pipe(in,response.getOutputStream());
            }
        } catch(IOException e) { graphUrl = null; }
        }
        
        if(graphUrl == null) {
            try {
                URL blank = new URL(getServletConfig().
                    getInitParameter("blankImage"));
                try ( InputStream in = blank.openStream()) {
                    response.setContentType("image/*");
                    pipe(in,response.getOutputStream());
                }
            } catch(Exception e) {}
        }
        
    }    
    
    private String getMimeType(String ext) {
        switch(ext) {
            case "svg":
                return "image/svg+xml";
            default:
                return "image/*";
        }
    }
    
    public void pipe(InputStream is, OutputStream os) throws IOException {
        int n;
        byte[] buffer = new byte[16384];
        while((n = is.read(buffer)) > -1) {
          os.write(buffer, 0, n);   // Don't allow any extra bytes to creep in, final write
        }
       os.close ();
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
