package br.com.flavio.owlet;

import br.com.flavio.owlet.exceptions.ConfigException;
import br.com.flavio.owlet.model.ClientServiceConfig;
import br.com.flavio.owlet.model.HttpMethod;
import br.com.flavio.owlet.services.ConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceConfigPropertyTest {

    @InjectMocks
    private ConfigService configService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLoadConfigs(){
        var properties = new Properties();
        properties.setProperty("client.service.url", "localhost");
        properties.setProperty("client.service.serviceName", "My webservice");
        properties.setProperty("client.service.applicationName", "My system");
        properties.setProperty("client.service.httpMethod", "GET");
        properties.setProperty("client.service.httpResponseCodeForCheckIfServiceIsUp", "200");
        properties.setProperty("client.service.checkPeriodInMilliseconds", "60000");
        properties.setProperty("client.service.timeoutConnectionInMilliseconds", "60000");
        properties.setProperty("client.service.maxFailureForCheckIfServiceIsDown", "2");
        properties.setProperty("client.service.periodForNewCheckAfterFailure", "120000");

        var clientServiceConfig = configService.loadClientServiceConfigProperties(properties);
        assertEquals(clientServiceConfig.getUrl(), URI.create("localhost"));
        assertEquals(clientServiceConfig.getServiceName(), "My webservice");
        assertEquals(clientServiceConfig.getApplicationName(), "My system");
        assertEquals(clientServiceConfig.getHttpMethod(), HttpMethod.valueOf("GET"));
        assertEquals(clientServiceConfig.getHttpResponseCodeForCheckIfServiceIsUp(), 200);
        assertEquals(clientServiceConfig.getCheckPeriodInMilliseconds(), 60000L);
        assertEquals(clientServiceConfig.getTimeoutConnectionInMilliseconds(), 60000L);
        assertEquals(clientServiceConfig.getMaxFailureForCheckIfServiceIsDown(), 2);
        assertEquals(clientServiceConfig.getPeriodForNewCheckAfterFailure(), 120000L);
    }

    @Test
    void shouldSpreadFileProperties() {
        var properties = new Properties();
        properties.setProperty("client.service.1.url", "localhost1");
        properties.setProperty("client.service.1.serviceName", "My webservice 1");
        properties.setProperty("client.service.1.applicationName", "My system 1");
        properties.setProperty("client.service.1.httpMethod", "GET");

        properties.setProperty("client.service.2.url", "localhost2");
        properties.setProperty("client.service.2.serviceName", "My webservice 2");
        properties.setProperty("client.service.2.applicationName", "My system 2");
        properties.setProperty("client.service.2.httpMethod", "GET");

        properties.setProperty("client.service.3.url", "localhost3");
        properties.setProperty("client.service.3.serviceName", "My webservice 3");
        properties.setProperty("client.service.3.applicationName", "My system 3");
        properties.setProperty("client.service.3.httpMethod", "GET");

        var spreadProperties = PropertiesUtil.spreadByPrefixIndex("client.service", properties);
        assertEquals(3, spreadProperties.size());
    }

    @Test
    void shouldLoadFileProperties() throws URISyntaxException, IOException {
        var uri = getClass().getClassLoader().getResource("config.properties").toURI();
        var properties = new Properties();
        properties.load(Files.newInputStream(Paths.get(uri)));
        var spreadProperties = PropertiesUtil.spreadByPrefixIndex("client.service", properties);

        var clientServiceConfigs = spreadProperties.stream()
                .map(configService::loadClientServiceConfigProperties)
                .filter(Objects::nonNull)
                .toList();
        assertEquals(clientServiceConfigs.size(), 3);
    }

    @Test
    void shouldCheckRequiredFields(){
        var clientServiceConfig = new ClientServiceConfig();
        assertThrows(ConfigException.class, () -> configService.checkRequiredFields(clientServiceConfig));

        clientServiceConfig.setUrl(URI.create("localhost"));
        clientServiceConfig.setHttpMethod(HttpMethod.GET);
        clientServiceConfig.setServiceName("My webservice");
        clientServiceConfig.setApplicationName("My system");
        assertDoesNotThrow(() -> configService.checkRequiredFields(clientServiceConfig));
    }
}
