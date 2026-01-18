package com.estivate.reconciliation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReconciliationScope {
    String table() default "";       // noms des tables visées
    String column() default "";      // noms des colonnes visées
}