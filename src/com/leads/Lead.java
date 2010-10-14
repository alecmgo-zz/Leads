package com.leads;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class Lead {
	
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;
    
  @Persistent
  private String title;

  @Persistent
  private boolean active;
  
  @Persistent
  private Date startDate;
  
//  @Persistent
//  private Map<String, String> skills;
  
  @Persistent
  private String nextStep;
  
  @Persistent
  private String status;
  
  @Persistent
  private Text content;

  public String toString() {
    JSONObject j = new JSONObject();
    try {
      j.put("id", id);
      j.put("title", title);
      j.put("active", active);
      j.put("content", content.getValue().toString());
      j.put("startDate", startDate);
      j.put("nextStep", nextStep);
      j.put("status", status);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return j.toString();
  }
  
  public JSONObject asAridgedJson() {
    JSONObject j = new JSONObject();
    try {
      j.put("id", id);
      j.put("title", title);
      j.put("active", active);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return j;
  }
  
  public void update(JSONObject obj) {
    String title = obj.optString("title");    
    if(title != null && !title.equals("")) {
      this.title = title;
    }
    
    Boolean active = obj.optBoolean("active");
    if(active != null) {
      this.active = active;
    }
    
    String nextStep = obj.optString("nextStep");
    if(nextStep != null) {
      
      this.nextStep = nextStep;
      System.out.println("updated nextStep");
    }
    
    String startDate = obj.optString("startDate");  
    if(startDate != null && !startDate.equals("")) {
      SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
      try {
        this.startDate = fmt.parse(startDate);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    
    String content = obj.optString("content");
    if(content != null) {
      this.content = new Text(content);
    }
  }
  
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
  
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

//  public Map<String, String> getSkills() {
//    return skills;
//  }
//
//  public void setSkills(Map<String, String> skills) {
//    this.skills = skills;
//  }

  public String getNextStep() {
    return nextStep;
  }

  public void setNextStep(String nextStep) {
    this.nextStep = nextStep;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
