package org.t34;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.t34.dto.UserContextDTO;
import org.t34.exception.InvalidPasswordException;
import org.t34.exception.InvalidTokenException;
import org.t34.exception.NotFoundException;
import org.t34.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.t34.dto.LoginDTO;
import org.t34.entity.User;
import org.t34.util.GeneralHelper;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AppTest {
    @Mock
    private APIGatewayProxyRequestEvent mockRequestEvent;
    @Mock
    private Context mockContext;
    MockedConstruction<UserService> mockUserService;
    private MockedStatic<DependencyFactory> dependencyFactoryMockedStatic;
    private MockedStatic<GeneralHelper> generalHelper;
    private App app;
    private AutoCloseable closeable;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        Configuration configuration = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:t34;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER;IGNORECASE=TRUE;MODE=MySQL")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .addAnnotatedClass(User.class);

        SessionFactory sessionFactory = configuration.buildSessionFactory();
        closeable = MockitoAnnotations.openMocks(this);
        dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class);
        dependencyFactoryMockedStatic.when(DependencyFactory::getSessionFactory).thenReturn(sessionFactory);
        generalHelper = mockStatic(GeneralHelper.class);
        generalHelper.when(() -> GeneralHelper.isPasswordMatch(any(), any())).thenReturn(true);
        generalHelper.when(() -> GeneralHelper.encodeJWT(any())).thenReturn("jwt");
        generalHelper.when(() -> GeneralHelper.decodeJWT(any())).thenReturn(1);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
        dependencyFactoryMockedStatic.close();
        mockUserService.close();
        generalHelper.close();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        mockUserService = mockConstruction(UserService.class, (mock, context) -> {
            when(mock.login(any(LoginDTO.class))).thenReturn(new UserContextDTO());
        });
        app = new App();
        LoginDTO loginDTO = new LoginDTO();
        UserContextDTO userContextDTO = new UserContextDTO(); // Initialize with the required data

        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        expectedResponse.setBody(OBJECT_MAPPER.writeValueAsString(userContextDTO));
        expectedResponse.setStatusCode(200);

        when(mockRequestEvent.getHttpMethod()).thenReturn("POST");
        when(mockRequestEvent.getPath()).thenReturn("/user/login");
        when(mockRequestEvent.getBody()).thenReturn(new ObjectMapper().writeValueAsString(loginDTO));

        APIGatewayProxyResponseEvent response = app.handleRequest(mockRequestEvent, mockContext);
        assertResponseEquals(expectedResponse, response);
    }

    @Test
    public void testLoginFailure() throws Exception {
        mockUserService = mockConstruction(UserService.class, (mock, context) -> {
            when(mock.login(any(LoginDTO.class))).thenThrow(new InvalidPasswordException());
        });
        app = new App();
        LoginDTO loginDTO = new LoginDTO();

        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        expectedResponse.setBody("Invalid password");
        expectedResponse.setStatusCode(500);

        when(mockRequestEvent.getHttpMethod()).thenReturn("POST");
        when(mockRequestEvent.getPath()).thenReturn("/user/login");
        when(mockRequestEvent.getBody()).thenReturn(new ObjectMapper().writeValueAsString(loginDTO));


        APIGatewayProxyResponseEvent response = app.handleRequest(mockRequestEvent, mockContext);
        assertResponseEquals(expectedResponse, response);
    }

    @Test
    public void testCreateSuccess() throws Exception {
        mockUserService = mockConstruction(UserService.class, (mock, context) -> {
            when(mock.create(any(User.class))).thenReturn("OK");
        });
        app = new App();
        LoginDTO loginDTO = new LoginDTO();

        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        expectedResponse.setBody("\"OK\"");
        expectedResponse.setStatusCode(200);

        when(mockRequestEvent.getHttpMethod()).thenReturn("POST");
        when(mockRequestEvent.getPath()).thenReturn("/user/create");
        when(mockRequestEvent.getBody()).thenReturn(new ObjectMapper().writeValueAsString(loginDTO));


        APIGatewayProxyResponseEvent response = app.handleRequest(mockRequestEvent, mockContext);
        assertResponseEquals(expectedResponse, response);
    }

    @Test
    public void testUpdateSuccess() throws Exception {
        mockUserService = mockConstruction(UserService.class, (mock, context) -> {
            when(mock.update(any(User.class),any(int.class))).thenReturn("OK");
        });
        app = new App();
        LoginDTO loginDTO = new LoginDTO();

        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        expectedResponse.setBody("\"OK\"");
        expectedResponse.setStatusCode(200);

        when(mockRequestEvent.getHttpMethod()).thenReturn("POST");
        when(mockRequestEvent.getPath()).thenReturn("/user/update");
        when(mockRequestEvent.getBody()).thenReturn(new ObjectMapper().writeValueAsString(loginDTO));


        APIGatewayProxyResponseEvent response = app.handleRequest(mockRequestEvent, mockContext);
        assertResponseEquals(expectedResponse, response);
    }

    @Test
    public void testUpdateFailure() throws Exception {
        mockUserService = mockConstruction(UserService.class, (mock, context) -> {
            when(mock.update(any(User.class),any(int.class))).thenThrow(new NotFoundException("User not found"));
        });
        app = new App();
        LoginDTO loginDTO = new LoginDTO();

        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        expectedResponse.setBody("User not found");
        expectedResponse.setStatusCode(404);

        when(mockRequestEvent.getHttpMethod()).thenReturn("POST");
        when(mockRequestEvent.getPath()).thenReturn("/user/update");
        when(mockRequestEvent.getBody()).thenReturn(new ObjectMapper().writeValueAsString(loginDTO));


        APIGatewayProxyResponseEvent response = app.handleRequest(mockRequestEvent, mockContext);
        assertResponseEquals(expectedResponse, response);
    }

    @Test
    public void testViewSuccess() throws Exception {
        mockUserService = mockConstruction(UserService.class, (mock, context) -> {
            when(mock.view(any(int.class))).thenReturn(new User());
        });
        app = new App();
        User user = new User();

        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        expectedResponse.setBody(OBJECT_MAPPER.writeValueAsString(user));
        expectedResponse.setStatusCode(200);

        when(mockRequestEvent.getHttpMethod()).thenReturn("POST");
        when(mockRequestEvent.getPath()).thenReturn("/user/view");
        when(mockRequestEvent.getBody()).thenReturn(new ObjectMapper().writeValueAsString(user));


        APIGatewayProxyResponseEvent response = app.handleRequest(mockRequestEvent, mockContext);
        assertResponseEquals(expectedResponse, response);
    }

    @Test
    public void testViewFailure() throws Exception {
        mockUserService = mockConstruction(UserService.class, (mock, context) -> {
            when(mock.view(any(int.class))).thenThrow(new NotFoundException("User not found"));
        });
        app = new App();
        User user = new User();

        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        expectedResponse.setBody("User not found");
        expectedResponse.setStatusCode(404);

        when(mockRequestEvent.getHttpMethod()).thenReturn("POST");
        when(mockRequestEvent.getPath()).thenReturn("/user/view");
        when(mockRequestEvent.getBody()).thenReturn(new ObjectMapper().writeValueAsString(user));


        APIGatewayProxyResponseEvent response = app.handleRequest(mockRequestEvent, mockContext);
        assertResponseEquals(expectedResponse, response);
    }

    @Test
    public void testTokenFailure() throws Exception {
        mockUserService = mockConstruction(UserService.class, (mock, context) -> {
            when(mock.view(any(int.class))).thenThrow(new NotFoundException("User not found"));
        });
        generalHelper.when(() -> GeneralHelper.decodeJWT(any())).thenThrow(new InvalidTokenException("Invalid token"));
        app = new App();
        User user = new User();

        APIGatewayProxyResponseEvent expectedResponse = new APIGatewayProxyResponseEvent();
        expectedResponse.setBody("Invalid token");
        expectedResponse.setStatusCode(401);

        when(mockRequestEvent.getHttpMethod()).thenReturn("POST");
        when(mockRequestEvent.getPath()).thenReturn("/user/view");
        when(mockRequestEvent.getBody()).thenReturn(new ObjectMapper().writeValueAsString(user));


        APIGatewayProxyResponseEvent response = app.handleRequest(mockRequestEvent, mockContext);
        assertResponseEquals(expectedResponse, response);
    }

    private void assertResponseEquals(APIGatewayProxyResponseEvent expected, APIGatewayProxyResponseEvent actual) {
        assertEquals(expected.getBody(), actual.getBody());
        assertEquals(expected.getStatusCode(), actual.getStatusCode());
    }
}
