package org.t34;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import javassist.NotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.t34.entity.User;
import org.t34.exception.InvalidTokenException;
import org.t34.service.UserService;
import org.t34.dto.LoginDTO;
import org.t34.util.Config;
import org.t34.util.GeneralHelper;

/**
 * Lambda function entry point. You can change to use other pojo type or implement
 * a different RequestHandler.
 *
 * @see <a href=https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html>Lambda Java Handler</a> for more information
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, Object> {
    private final SessionFactory sessionFactory;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public App() {
        // Initialize the SDK client outside of the handler method so that it can be reused for subsequent invocations.
        // It is initialized when the class is loaded.
        // Consider invoking a simple api here to pre-warm up the application, eg: dynamodb#listTables
        sessionFactory = DependencyFactory.getSessionFactory();
        userService = new UserService(sessionFactory);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        String responseBody = "OK";
        int responseCode = 200;
        String body = input.getBody();
        logger.info("received request (method: {}) (path: {}), (body: {})", input.getHttpMethod(), input.getPath(), body);
        String jwt = input.getHeaders().get(Config.AUTH_HEADER);

        try {
            if (!input.getHttpMethod().equalsIgnoreCase("POST")) {
                throw new Exception("Only post method is allowed");
            }
            Object result = null;
            switch (input.getPath()) {
                case "/login":
                    result = userService.login(OBJECT_MAPPER.readValue(body, LoginDTO.class));
                    break;
                case "/create":
                    result = userService.create(OBJECT_MAPPER.readValue(body, User.class));
                    break;
                case "/update":
                    result = userService.update(OBJECT_MAPPER.readValue(body, User.class), GeneralHelper.decodeJWT(jwt));
                    break;
                case "/view":
                    result = userService.view(GeneralHelper.decodeJWT(jwt));
                    break;
                default:
                    throw new NotFoundException("Resource not found");
            }
            responseBody = OBJECT_MAPPER.writeValueAsString(result);
            responseCode = 200;
            flushConnectionPool();
        } catch (NotFoundException ex) {
            logger.error(ex.getMessage());
            responseBody = ex.getMessage();
            responseCode = 404;
        } catch (InvalidTokenException ex) {
            logger.error(ex.getMessage());
            responseBody = ex.getMessage();
            responseCode = 401;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            responseBody = ex.getMessage();
            responseCode = 500;
        }
        responseEvent.setBody(responseBody);
        responseEvent.setStatusCode(responseCode);
        logger.info("response - body: {} status: {}", responseEvent.getBody(), responseEvent.getStatusCode());
        return responseEvent;
    }

    private void flushConnectionPool() {
        ConnectionProvider connectionProvider = sessionFactory.getSessionFactoryOptions()
                .getServiceRegistry()
                .getService(ConnectionProvider.class);
        HikariDataSource hikariDataSource = connectionProvider.unwrap(HikariDataSource.class);
        hikariDataSource.getHikariPoolMXBean().softEvictConnections();
    }
}
