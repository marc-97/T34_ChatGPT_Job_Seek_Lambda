package org.t34;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.t34.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.*;

/**
 * The module containing all dependencies required by the {@link App}.
 */
public class DependencyFactory {
    private static SessionFactory sessionFactory;
    private static final Logger logger = LoggerFactory.getLogger(DependencyFactory.class);

    private DependencyFactory() {}


    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null) {
            return sessionFactory;
        }

        logger.info(Config.DB_URL);
        logger.info(Config.DB_SCHEMA);
        logger.info(Config.DB_USER);
        logger.info(Config.DB_PASSWORD);
        Map<String, String> settings = new HashMap<>();
        settings.put("hibernate.connection.url", Config.DB_URL); // Correct JDBC URL format
        settings.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        settings.put("hibernate.default_schema", Config.DB_SCHEMA);
        settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        settings.put("hibernate.connection.username", Config.DB_USER);
        settings.put("hibernate.connection.password", Config.DB_PASSWORD);
        settings.put("hibernate.hikari.connectionTimeout", "20000");
        settings.put("hibernate.hikari.minimumIdle", "1");
        settings.put("hibernate.hikari.maximumPoolSize", "2");
        settings.put("hibernate.hikari.idleTimeout", "30000");
        settings.put("hibernate.hbm2ddl.auto", "none");

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        return new MetadataSources(registry)
                .addAnnotatedClass(User.class)
                .buildMetadata()
                .buildSessionFactory();
    }
}
