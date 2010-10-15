package com.leads;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Display name for a persistent field.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DisplayName {
  String value();
}
