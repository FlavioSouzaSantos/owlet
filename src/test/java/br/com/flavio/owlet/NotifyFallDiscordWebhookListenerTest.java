package br.com.flavio.owlet;

import br.com.flavio.owlet.listeners.NotifyFallDiscordWebhookListener;
import br.com.flavio.owlet.model.ClientServiceConfig;
import br.com.flavio.owlet.model.HttpMethod;
import br.com.flavio.owlet.model.NotifyServiceFallDiscordWebhookConfig;
import br.com.flavio.owlet.model.ServiceEvent;
import br.com.flavio.owlet.services.CheckEndpointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotifyFallDiscordWebhookListenerTest {

    @Test
    void shouldNotifyFall() {
        var notifyFallDiscordWebhookListener = Mockito.mock(NotifyFallDiscordWebhookListener.class,
                Mockito.withSettings()
                        .useConstructor(new NotifyServiceFallDiscordWebhookConfig())
                        .defaultAnswer(Answers.RETURNS_DEFAULTS));

        var clientServiceConfig = new ClientServiceConfig();
        clientServiceConfig.setUrl(URI.create("https://www.google.com.br"));
        clientServiceConfig.setHttpMethod(HttpMethod.GET);
        clientServiceConfig.setServiceName("Google Search");
        clientServiceConfig.setApplicationName("Google");
        clientServiceConfig.setHttpResponseCodeForCheckIfServiceIsUp(999);
        clientServiceConfig.setMaxFailureForCheckIfServiceIsDown(1);

        var checkEndpointService = new CheckEndpointService(clientServiceConfig);
        checkEndpointService.setServiceFallListener(notifyFallDiscordWebhookListener);
        checkEndpointService.checkHealth();
        checkEndpointService.checkHealth();

        verify(notifyFallDiscordWebhookListener, times(1)).onFall(any(ServiceEvent.class));
    }

    @Test
    void shouldNotNotifyFall() {
        var notifyFallDiscordWebhookListener = Mockito.mock(NotifyFallDiscordWebhookListener.class,
                Mockito.withSettings()
                        .useConstructor(new NotifyServiceFallDiscordWebhookConfig())
                        .defaultAnswer(Answers.RETURNS_DEFAULTS));

        var clientServiceConfig = new ClientServiceConfig();
        clientServiceConfig.setUrl(URI.create("https://www.google.com.br"));
        clientServiceConfig.setHttpMethod(HttpMethod.GET);
        clientServiceConfig.setServiceName("Google Search");
        clientServiceConfig.setApplicationName("Google");
        clientServiceConfig.setTimeoutConnectionInMilliseconds(10_000L);
        clientServiceConfig.setHttpResponseCodeForCheckIfServiceIsUp(200);
        clientServiceConfig.setMaxFailureForCheckIfServiceIsDown(1);

        var checkEndpointService = new CheckEndpointService(clientServiceConfig);
        checkEndpointService.setServiceFallListener(notifyFallDiscordWebhookListener);
        checkEndpointService.checkHealth();
        checkEndpointService.checkHealth();
        checkEndpointService.checkHealth();

        verify(notifyFallDiscordWebhookListener, never()).onFall(any(ServiceEvent.class));
    }

    @Test
    void shouldCreateUrlWebhook() {
        var notifyServiceFallDiscordWebhookConfig = new NotifyServiceFallDiscordWebhookConfig();
        notifyServiceFallDiscordWebhookConfig.setUrlTemplate("https://discord.com/webhook/{webhook.id}/{webhook.token}");
        notifyServiceFallDiscordWebhookConfig.setId("123");
        notifyServiceFallDiscordWebhookConfig.setToken("a123b456c789");

        assertEquals(URI.create("https://discord.com/webhook/123/a123b456c789"),
                notifyServiceFallDiscordWebhookConfig.createUrl());

        notifyServiceFallDiscordWebhookConfig.setUrlTemplate("https://discord.com/webhook");
        assertEquals(URI.create("https://discord.com/webhook"),
                notifyServiceFallDiscordWebhookConfig.createUrl());
    }

    @Test
    void shouldNotCreateUrlWebhook() {
        var notifyServiceFallDiscordWebhookConfig = new NotifyServiceFallDiscordWebhookConfig();

        assertNull(notifyServiceFallDiscordWebhookConfig.createUrl());

        notifyServiceFallDiscordWebhookConfig.setUrlTemplate("https://discord.com/webhook/{webhook.id}/{webhook.token}");
        assertNull(notifyServiceFallDiscordWebhookConfig.createUrl());

        notifyServiceFallDiscordWebhookConfig.setId("123");
        assertNull(notifyServiceFallDiscordWebhookConfig.createUrl());

        notifyServiceFallDiscordWebhookConfig.setToken("123456");
        assertNotNull(notifyServiceFallDiscordWebhookConfig.createUrl());
    }

    @Test
    void shouldCreateMessage() {
        var time = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0, 0, 0);

        var serviceEvent = new ServiceEvent(time, "Google", "Google search");
        var notifyServiceFallDiscordWebhookConfig = new NotifyServiceFallDiscordWebhookConfig();
        notifyServiceFallDiscordWebhookConfig.setMessageTemplate("The service {serviceName} of the application {applicationName} fell in {time}.");
        assertEquals("The service Google search of the application Google fell in 2024-01-01T00:00:00.",
                notifyServiceFallDiscordWebhookConfig.createMessage(serviceEvent));
    }

    @Test
    void shouldCreateFormParam() {
        var message = "The service Google search of the application Google fell in 2024-01-01T00:00:00.";
        var notifyServiceFallDiscordWebhookConfig = new NotifyServiceFallDiscordWebhookConfig();
        var formParam = new HashMap<String, Object>();
        formParam.put("content", message);
        assertEquals(formParam, notifyServiceFallDiscordWebhookConfig.createFormParam(message));
    }
}
