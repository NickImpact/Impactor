package com.nickimpact.impactor.common.api;

import com.nickimpact.impactor.api.Impactor;
import com.nickimpact.impactor.api.ImpactorServiceProvider;

import java.lang.reflect.Method;

public class ApiRegistrationUtil {

    private static final Method REGISTER;
    private static final Method UNREGISTER;

    static {
        try {
            REGISTER = ImpactorServiceProvider.class.getDeclaredMethod("register", Impactor.class);
            REGISTER.setAccessible(true);

            UNREGISTER = ImpactorServiceProvider.class.getDeclaredMethod("unregister");
            UNREGISTER.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void register(Impactor service) {
        try {
            REGISTER.invoke(null, service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unregister() {
        try {
            UNREGISTER.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
