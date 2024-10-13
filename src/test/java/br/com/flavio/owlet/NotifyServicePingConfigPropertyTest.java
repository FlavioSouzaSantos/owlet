package br.com.flavio.owlet;

import br.com.flavio.owlet.exceptions.ConfigException;
import br.com.flavio.owlet.model.HttpMethod;
import br.com.flavio.owlet.model.NotifyServicePingConfig;
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
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class NotifyServicePingConfigPropertyTest {

    @InjectMocks
    private ConfigService configService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLoadConfigs(){
        var properties = new Properties();
        properties.setProperty("notify.service.ping.url", "localhost");
        properties.setProperty("notify.service.ping.httpMethod", "GET");
        properties.setProperty("notify.service.ping.sendPayload", "true");

        var notifyServicePingConfig = configService.loadConfigNotifyServicePingProperties(properties);
        assertEquals(notifyServicePingConfig.getUrl(), URI.create("localhost"));
        assertEquals(notifyServicePingConfig.getHttpMethod(), HttpMethod.valueOf("GET"));
        assertEquals(notifyServicePingConfig.isSendPayload(), true);
    }

    @Test
    void shouldLoadFileProperties() throws URISyntaxException, IOException {
        var uri = getClass().getClassLoader().getResource("config.properties").toURI();
        var properties = new Properties();
        properties.load(Files.newInputStream(Paths.get(uri)));
        var notifyServicePingConfig = configService.loadConfigNotifyServicePingProperties(properties);
        assertNotNull(notifyServicePingConfig);
    }

    @Test
    void shouldCheckRequiredFields(){
        var notifyServicePingConfig = new NotifyServicePingConfig();
        assertThrows(ConfigException.class, () -> configService.checkRequiredFields(notifyServicePingConfig));

        notifyServicePingConfig.setUrl(URI.create("localhost"));
        notifyServicePingConfig.setHttpMethod(HttpMethod.GET);
        assertDoesNotThrow(() -> configService.checkRequiredFields(notifyServicePingConfig));
    }
}
