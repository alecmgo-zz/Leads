package com.leads;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetItemServlet extends HttpServlet{
  
  private static final long serialVersionUID = -7735340128363218709L;

  public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
    Long id = Long.parseLong(req.getParameter("id"));
    PersistenceManager pm = PMF.get().getPersistenceManager();
    Lead lead = pm.getObjectById(Lead.class, id);
    resp.getWriter().println(lead.toString());
  }
}
