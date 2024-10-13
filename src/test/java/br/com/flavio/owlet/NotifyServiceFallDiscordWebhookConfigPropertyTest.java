package br.com.flavio.owlet;

import br.com.flavio.owlet.exceptions.ConfigException;
import br.com.flavio.owlet.model.NotifyServiceFallDiscordWebhookConfig;
import br.com.flavio.owlet.services.ConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class NotifyServiceFallDiscordWebhookConfigPropertyTest {

    @InjectMocks
    private ConfigService configService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLoadConfigs(){
        var properties = new Properties();
        properties.setProperty("notify.service.fall.discord.webhook.urlTemplate", "localhost");
        properties.setProperty("notify.service.fall.discord.webhook.id", "123456");
        properties.setProperty("notify.service.fall.discord.webhook.token", "xydgztdh4hj7");
        properties.setProperty("notify.service.fall.discord.webhook.messageTemplate", "Service is fall.");

        var notifyServiceFallDiscordWebhookConfig = configService.loadConfigNotifyServiceFallProperties(properties);
        assertEquals(notifyServiceFallDiscordWebhookConfig.getUrlTemplate(), "localhost");
        assertEquals(notifyServiceFallDiscordWebhookConfig.getId(), "123456");
        assertEquals(notifyServiceFallDiscordWebhookConfig.getToken(), "xydgztdh4hj7");
        assertEquals(notifyServiceFallDiscordWebhookConfig.getMessageTemplate(), "Service is fall.");
    }

    @Test
    void shouldLoadFileProperties() throws URISyntaxException, IOException {
        var uri = getClass().getClassLoader().getResource("config.properties").toURI();
        var properties = new Properties();
        properties.load(Files.newInputStream(Paths.get(uri)));
        var notifyServiceFallDiscordWebhookConfig = configService.loadConfigNotifyServiceFallProperties(properties);
        assertNotNull(notifyServiceFallDiscordWebhookConfig);
    }

    @Test
    void shouldCheckRequiredFields(){
        var notifyServiceFallDiscordWebhookConfig =  new NotifyServiceFallDiscordWebhookConfig();
        assertThrows(ConfigException.class, () -> configService.checkRequiredFields(notifyServiceFallDiscordWebhookConfig));

        notifyServiceFallDiscordWebhookConfig.setUrlTemplate("localhost");
        notifyServiceFallDiscordWebhookConfig.setId("123");
        notifyServiceFallDiscordWebhookConfig.setToken("789456123");
        notifyServiceFallDiscordWebhookConfig.setMessageTemplate("Service is fall.");
        assertDoesNotThrow(() -> configService.checkRequiredFields(notifyServiceFallDiscordWebhookConfig));
    }
}
