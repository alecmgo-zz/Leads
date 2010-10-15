package com.leads;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

public class GetConfigServlet extends HttpServlet{

  private static final long serialVersionUID = 906641593005252927L;

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    JSONObject result = new JSONObject();

    JSONArray arr = new JSONArray();
    try {
      for (Field f : Lead.class.getDeclaredFields()) {
        DisplayName displayName = f.getAnnotation(DisplayName.class);
        if (displayName != null) {
          JSONObject info = new JSONObject();
          info.put("name", f.getName());
          info.put("displayName", displayName.value());
          info.put("type", Type.getType(f.getType()).getName());
          arr.put(info);
        }
      }

      result.put("fields", arr);
    } catch (JSONException e) {
      throw new IllegalStateException("This shouldn't happen", e);
    }
    resp.getWriter().println(result.toString());
  }
}