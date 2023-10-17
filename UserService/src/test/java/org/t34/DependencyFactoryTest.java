package org.t34;

import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.internal.MetadataImpl;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.TypeDefinition;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.FetchProfile;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.type.Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;
import org.t34.dto.LoginDTO;
import org.t34.dto.UserContextDTO;
import org.t34.entity.User;
import org.t34.service.UserService;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class DependencyFactoryTest {
    private AutoCloseable closeable;
    @Mock
    private Metadata metadata;
    @Mock
    private MetadataSources metadataSources;
    private MockedConstruction<MetadataSources> metadataSourcesMockedConstruction;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Configuration configuration = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:t34;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER;IGNORECASE=TRUE;MODE=MySQL")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .addAnnotatedClass(User.class);

        SessionFactory sessionFactory = configuration.buildSessionFactory();
        when(metadata.buildSessionFactory()).thenReturn(sessionFactory);
        when(metadataSources.buildMetadata()).thenReturn(metadata);
        metadataSourcesMockedConstruction = mockConstruction(MetadataSources.class, (mock, context) -> {
            when(mock.addAnnotatedClass(any())).thenReturn(metadataSources);
        });
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
        metadataSourcesMockedConstruction.close();
    }

    @Test
    public void testGetSessionFactory() {
        SessionFactory sessionFactory = DependencyFactory.getSessionFactory();
        assertNotNull(sessionFactory);
    }
}
