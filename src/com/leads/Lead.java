package com.leads;

import com.google.appengine.api.datastore.Text;
import com.google.common.base.CaseFormat;
import com.google.common.primitives.Primitives;
import org.json.JSONException;
import org.json.JSONObject;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

@PersistenceCapable
public class Lead {
  // The following fields are required.

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private String title;

  @Persistent
  private boolean active;

  @Persistent
  private Text content;

  // Begin custom fields:

  @Persistent
  @DisplayName("Start Date")
  private Date startDate;

  @Persistent
  @DisplayName("Next Step")
  private String nextStep;

  //@Persistent
  //@DisplayName("Skills")
  //private Map<String, String> skills;


  public String toString() {
    JSONObject j = new JSONObject();
    try {
      for (Field f : Lead.class.getDeclaredFields()) {
        if (f.getAnnotation(Persistent.class) != null) {
          Class<?> wrappedClass = Primitives.wrap(f.getType());
          Method getter = Lead.class.getMethod("get" + getCapitalizedName(f));
          Object value = getter.invoke(this);
          if (value != null) {
            j.put(f.getName(), Type.getType(wrappedClass).convertToJson(value));
          }
        }
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return j.toString();
  }

  public JSONObject asAbridgedJson() {
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

  private static String getCapitalizedName(Field f) {
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, f.getName());
  }

  public void update(JSONObject obj) {
    try {
      for (Field f : Lead.class.getDeclaredFields()) {
        if (f.getAnnotation(Persistent.class) != null) {
          Object value = obj.opt(f.getName());
          if (value != null) {
            // We have to use the setter since that is how the enhanced class works.
            Class<?> unwrappedClass = Primitives.unwrap(f.getType());
            Class<?> wrappedClass = Primitives.wrap(f.getType());
            Method setter = Lead.class.getMethod("set" + getCapitalizedName(f), unwrappedClass);
            Object fieldValue = Type.getType(wrappedClass).convertFromJson(value);
            setter.invoke(this, fieldValue);
          }
        }
      }
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(e);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    } catch (InvocationTargetException e) {
      throw new IllegalArgumentException(e);
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

  public boolean getActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Text getContent() {
    return content;
  }

  public void setContent(Text content) {
    this.content = content;
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
}
