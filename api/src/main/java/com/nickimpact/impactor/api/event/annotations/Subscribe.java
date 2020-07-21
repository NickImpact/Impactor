package com.nickimpact.impactor.api.event.annotations;

import com.nickimpact.impactor.api.event.annotations.util.Orders;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    int order() default Orders.NORMAL;

    boolean ignoreCallcelled() default false;
}
