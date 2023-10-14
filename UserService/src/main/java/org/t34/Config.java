package org.t34;

public class Config {
    public static final String AUTH_HEADER = System.getenv("AUTH_HEADER");
    public static final String JWT_SECRET = System.getenv("JWT_SECRET_KEY");
    public static final String PW_SALT = System.getenv("PW_SALT");
    public static final String DB_URL = "jdbc:mysql://" + System.getenv("DB_URL");
    public static final String DB_USER = System.getenv("DB_USER");
    public static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    public static final String DB_SCHEMA = "t34";
//    public static final String AUTH_HEADER = "Auth";
//    public static final String JWT_SECRET = "SECRET_KEY";
//    public static final String PW_SALT = "SALT";
//    public static final String DB_URL = "jdbc:mysql://host.docker.internal:3306/t34";
//    public static final String DB_USER = "root";
//    public static final String DB_PASSWORD = "password";
//    public static final String DB_SCHEMA = "t34";
}
