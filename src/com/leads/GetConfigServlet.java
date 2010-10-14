package com.leads;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetConfigServlet extends HttpServlet{

  private static final long serialVersionUID = 906641593005252927L;

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    //TODO: Change this. This should automagically be generated, using reflection
    //on the leads object.
    JSONObject hackedResult = new JSONObject();
    
    JSONArray arr = new JSONArray();
    try {
      JSONObject startDate = new JSONObject();
      startDate.put("name", "startDate");
      startDate.put("displayName", "Start Date");
      startDate.put("type", "date");
      arr.put(startDate);
      
      JSONObject nextStep = new JSONObject();
      nextStep.put("name", "nextStep");
      nextStep.put("displayName", "Next Step");
      nextStep.put("type", "string");
      arr.put(nextStep);
      
      
//      JSONObject skills = new JSONObject();
//      skills.put("name", "skills");
//      skills.put("displayName", "Skills");
//      skills.put("type", "map");
//      arr.put(skills);
      hackedResult.put("fields", arr);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    resp.getWriter().println(hackedResult.toString());
  }
}