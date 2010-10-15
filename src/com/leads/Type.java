package com.leads;

import com.google.appengine.api.datastore.Text;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * JSON conversion.
 */
public abstract class Type<T> {
  public abstract T convertFromJson(Object value);

  public abstract Object convertToJsonInternal(T value);

  @SuppressWarnings("unchecked")
  public Object convertToJson(Object value) {
    return convertToJsonInternal((T) value);
  }

  public abstract String getName();



  private static final Map<Class<?>, Type<?>> TYPE_MAP = ImmutableMap.<Class<?>, Type<?>>builder()
      .put(Date.class, new DateType())
      .put(Text.class, new TextType())
      .put(String.class, new StringType())
      .put(Long.class, new LongType())
      .put(Boolean.class, new BooleanType())
      .build();

  @SuppressWarnings("unchecked")
  public static <T> Type<T> getType(Class<T> clazz) {
    return (Type<T>) TYPE_MAP.get(clazz);
  }


  public static class DateType extends Type<Date> {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    public Date convertFromJson(Object value) {
      Preconditions.checkArgument(value instanceof String);

      try {
        return FORMAT.parse((String) value);
      } catch (ParseException e) {
        throw new IllegalArgumentException(e);
      }
    }

    public Object convertToJsonInternal(Date value) {
      return FORMAT.format(value);
    }

    public String getName() {
      return "date";
    }
  }


  public static class TextType extends Type<Text> {
    public Text convertFromJson(Object value) {
      Preconditions.checkArgument(value instanceof String);
      return new Text((String) value);
    }

    public Object convertToJsonInternal(Text value) {
      return value.toString();
    }

    public String getName() {
      return "text";
    }
  }


  public static class StringType extends Type<String> {
    public String convertFromJson(Object value) {
      Preconditions.checkArgument(value instanceof String);
      return (String) value;
    }

    public Object convertToJsonInternal(String value) {
      return value;
    }

    public String getName() {
      return "string";
    }
  }


  public static class BooleanType extends Type<Boolean> {
    public Boolean convertFromJson(Object value) {
      if (value instanceof String) {
        return Boolean.valueOf((String) value);
      } else if (value instanceof Boolean) {
        return (Boolean) value;
      }
      throw new IllegalArgumentException("Invalid type for long: " + value.getClass());
    }

    public Object convertToJsonInternal(Boolean value) {
      return value;
    }

    public String getName() {
      return "string";
    }
  }


  public static class LongType extends Type<Long> {
    public Long convertFromJson(Object value) {
      if (value instanceof String) {
        return Long.valueOf((String) value);
      } else if (value instanceof Long) {
        return (Long) value;
      } else if (value instanceof Integer) {
        return ((Integer) value).longValue();
      }
      throw new IllegalArgumentException("Invalid type for long: " + value.getClass());
    }

    public Object convertToJsonInternal(Long value) {
      return value.toString();
    }

    public String getName() {
      return "long";
    }
  }
}
