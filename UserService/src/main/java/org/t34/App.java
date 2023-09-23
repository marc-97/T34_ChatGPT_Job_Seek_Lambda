package org.t34;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.t34.entity.User;
import org.t34.service.UserService;
import org.t34.dto.LoginDTO;

/**
 * Lambda function entry point. You can change to use other pojo type or implement
 * a different RequestHandler.
 *
 * @see <a href=https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html>Lambda Java Handler</a> for more information
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, Object> {
    private final SessionFactory sessionFactory;
    private final UserService userService;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public App() {
        // Initialize the SDK client outside of the handler method so that it can be reused for subsequent invocations.
        // It is initialized when the class is loaded.
        // Consider invoking a simple api here to pre-warm up the application, eg: dynamodb#listTables
        sessionFactory = DependencyFactory.getSessionFactory();
        userService = new UserService(sessionFactory);
    }

    @Override
    public Object handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        // TODO: invoking the api call using rdsClient.
        Object result = "OK";
        String body = input.getBody();

        try {
            switch (input.getResource()) {
                case "/login":
                    result = userService.login(OBJECT_MAPPER.readValue(body, LoginDTO.class));
                    break;
                case "/create":
                    result = userService.create(OBJECT_MAPPER.readValue(body, User.class));
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        flushConnectionPool();
        try {
            return OBJECT_MAPPER.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void flushConnectionPool() {
        ConnectionProvider connectionProvider = sessionFactory.getSessionFactoryOptions()
                .getServiceRegistry()
                .getService(ConnectionProvider.class);
        HikariDataSource hikariDataSource = connectionProvider.unwrap(HikariDataSource.class);
        hikariDataSource.getHikariPoolMXBean().softEvictConnections();
    }
}
