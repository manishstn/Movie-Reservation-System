package com.moviereservationsystem.constants;

public class SecurityConstants {

    // These MUST match the 'name' column in your 'roles' table exactly
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

    // PreAuthorize Strings
    // hasRole('ADMIN') looks for 'ROLE_ADMIN' in the GrantedAuthority list
    public static final String HAS_ROLE_ADMIN = "hasRole('ADMIN')";
    public static final String HAS_ROLE_MANAGER = "hasRole('MANAGER')";
    public static final String HAS_ROLE_CUSTOMER = "hasRole('CUSTOMER')";

    // Combined access for Movie/Showtime management
    public static final String HAS_ANY_ROLE_STAFF = "hasAnyRole('ADMIN', 'MANAGER')";
}