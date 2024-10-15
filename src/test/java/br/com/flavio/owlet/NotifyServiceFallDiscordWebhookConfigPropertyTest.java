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
import java.net.URI;
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
        properties.setProperty("notify.service.fall.discord.webhook.url", "localhost");
        properties.setProperty("notify.service.fall.discord.webhook.username", "owlet");
        properties.setProperty("notify.service.fall.discord.webhook.avatarUrl", "localhost/owlet.png");
        properties.setProperty("notify.service.fall.discord.webhook.messageTemplate", "Service is fall.");
        properties.setProperty("notify.service.fall.discord.webhook.dateTimePattern", "dd/MM/yyyy hh:mm:ss");

        var notifyServiceFallDiscordWebhookConfig = configService.loadConfigNotifyServiceFallProperties(properties);
        assertEquals(notifyServiceFallDiscordWebhookConfig.getUrl(), URI.create("localhost"));
        assertEquals(notifyServiceFallDiscordWebhookConfig.getUsername(), "owlet");
        assertEquals(notifyServiceFallDiscordWebhookConfig.getUsername(), URI.create("localhost/owlet.png"));
        assertEquals(notifyServiceFallDiscordWebhookConfig.getMessageTemplate(), "Service is fall.");
        assertEquals(notifyServiceFallDiscordWebhookConfig.getDateTimePattern(), "dd/MM/yyyy hh:mm:ss");
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

        notifyServiceFallDiscordWebhookConfig.setUrl(URI.create("localhost"));
        notifyServiceFallDiscordWebhookConfig.setMessageTemplate("Service is fall.");
        assertDoesNotThrow(() -> configService.checkRequiredFields(notifyServiceFallDiscordWebhookConfig));
    }
}
