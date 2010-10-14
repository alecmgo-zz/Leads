package com.leads;

import java.io.BufferedReader;
import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class SaveItemServlet extends HttpServlet {

  private static final long serialVersionUID = 7287291071894202021L;

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    process(req, resp);
  }
  
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    process(req, resp);
  }

  private void process(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    try {
      //Get payload
      BufferedReader reader = req.getReader();
      StringBuilder payloadString = new StringBuilder();
      String line;
      while((line = reader.readLine()) != null) {
        payloadString.append(line);
      }
      
      JSONObject payload = new JSONObject(payloadString.toString());
      Long id = payload.optLong("id");
      
      PersistenceManager pm = PMF.get().getPersistenceManager();
      Lead lead;
      if(id == 0) {
        lead = new Lead();
        System.err.println("Making new lead");
      } else {
        lead = pm.getObjectById(Lead.class, id);
      }
      
      lead.update(payload);
      pm.makePersistent(lead);
      pm.close();
      
      JSONObject response = new JSONObject();
      response.put("id", lead.getId());
      resp.getWriter().println(response.toString());
    } catch(JSONException e) {
      e.printStackTrace();
      JSONObject response = new JSONObject();
      try {
        response.put("error", e.toString());
      } catch (JSONException e1) {
        e1.printStackTrace();
      }
      resp.getWriter().println(response.toString());      
    }
  }
}
