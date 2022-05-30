package com.pdp.apphrmanagement.annotations;

import javax.persistence.GenerationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface  Generate16Numbers {
    GenerationType strategy() default GenerationType.IDENTITY;

    String generator() default "";
}
