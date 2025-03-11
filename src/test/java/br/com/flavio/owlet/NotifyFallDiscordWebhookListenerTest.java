package br.com.flavio.owlet;

import br.com.flavio.owlet.listeners.NotifyFallDiscordWebhookListener;
import br.com.flavio.owlet.model.*;
import br.com.flavio.owlet.services.CheckEndpointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        checkEndpointService.addServiceFallListener(notifyFallDiscordWebhookListener);
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
        checkEndpointService.addServiceFallListener(notifyFallDiscordWebhookListener);
        checkEndpointService.checkHealth();
        checkEndpointService.checkHealth();
        checkEndpointService.checkHealth();

        verify(notifyFallDiscordWebhookListener, never()).onFall(any(ServiceEvent.class));
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
    void shouldCreateWebhookMessage() {
        var message = "The service Google search of the application Google fell in 2024-01-01T00:00:00.";
        var notifyServiceFallDiscordWebhookConfig = new NotifyServiceFallDiscordWebhookConfig();
        var formParam = new DiscordWebhookMessage(null, null, message);
        assertEquals(formParam, notifyServiceFallDiscordWebhookConfig.createDiscordWebhookMessage(message));
    }
}
