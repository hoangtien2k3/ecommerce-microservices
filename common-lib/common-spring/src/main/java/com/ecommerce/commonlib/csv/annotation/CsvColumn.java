package com.ecommerce.commonlib.csv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field for CSV export. Order in the output follows declaration order
 * within the class (parent fields first, then child fields).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvColumn {

    /** Header label written to the first row. */
    String columnName();
}
