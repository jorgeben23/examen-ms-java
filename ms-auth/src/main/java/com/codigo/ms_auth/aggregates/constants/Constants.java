package com.codigo.ms_auth.aggregates.constants;

public class Constants {
    public static final Boolean STATUS_ACTIVE = true;
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String REFRESH = "refresh";
    public static final String ACCESS = "access";

    public static final String[] PERMIT_ENDPOINTS = {
            "/api/auth/clave",
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/validate",
            "/actuator/**"
    };

    public static final String[] ENDPOINTS_SUPERADMIN = {
            "/api/auth/signupadmin"
    };

    // accesibles por ADMIN y SUPERADMIN
    public static final String[] ENDPOINTS_ADMIN = {
            "/api/auth/signupuser",
            "/api/auth/listar",
            "/api/auth/all"
    };

    public static final String[] ENDPOINTS_USER = {
            "/api/auth/saludar",
            "/api/auth/refreshtoken"
    };

    public static final String USER_ADMIN = "JBENITES";
}
