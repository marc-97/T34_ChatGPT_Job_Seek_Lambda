package org.t34;

public class Config {
    public static final String AUTH_HEADER = System.getenv("AUTH_HEADER");
    public static final String JWT_SECRET = System.getenv("JWT_SECRET_KEY");
    public static final String PW_SALT = System.getenv("PW_SALT");
    public static final String DB_URL = "jdbc:mysql://" + System.getenv("DB_URL");
    public static final String DB_USER = System.getenv("DB_USER");
    public static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    public static final String DB_SCHEMA = System.getenv("DB_SCHEMA");
}
