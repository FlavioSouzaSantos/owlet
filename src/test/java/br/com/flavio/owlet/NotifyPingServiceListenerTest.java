package br.com.flavio.owlet;

import br.com.flavio.owlet.listeners.NotifyPingServiceListener;
import br.com.flavio.owlet.model.ClientServiceConfig;
import br.com.flavio.owlet.model.HttpMethod;
import br.com.flavio.owlet.model.NotifyServicePingConfig;
import br.com.flavio.owlet.model.ServiceEvent;
import br.com.flavio.owlet.services.CheckEndpointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotifyPingServiceListenerTest {

    @Test
    void shouldNotifyPing() {
        var listener =  Mockito.mock(NotifyPingServiceListener.class,
                Mockito.withSettings()
                        .useConstructor(new NotifyServicePingConfig())
                        .defaultAnswer(Answers.RETURNS_DEFAULTS));

        var clientServiceConfig = new ClientServiceConfig();
        clientServiceConfig.setUrl(URI.create("https://www.google.com.br"));
        clientServiceConfig.setHttpMethod(HttpMethod.GET);
        clientServiceConfig.setServiceName("Serviço de teste");
        clientServiceConfig.setApplicationName("Aplicação teste");
        clientServiceConfig.setTimeoutConnectionInMilliseconds(10_000L);
        clientServiceConfig.setHttpResponseCodeForCheckIfServiceIsUp(200);

        var checkEndpointService = new CheckEndpointService(clientServiceConfig);
        checkEndpointService.setServicePingListener(listener);
        checkEndpointService.checkHealth();

        verify(listener, times(1)).onPing(any(ServiceEvent.class));
    }

    @Test
    void shouldNotNotifyPing() {
        var clientServiceConfig = new ClientServiceConfig();
        clientServiceConfig.setUrl(URI.create("http://localhost:8080"));
        clientServiceConfig.setHttpMethod(HttpMethod.GET);
        clientServiceConfig.setServiceName("Serviço de teste");
        clientServiceConfig.setApplicationName("Aplicação teste");
        clientServiceConfig.setTimeoutConnectionInMilliseconds(10_000L);

        var listener =  Mockito.mock(NotifyPingServiceListener.class,
                Mockito.withSettings()
                        .useConstructor(new NotifyServicePingConfig())
                        .defaultAnswer(Answers.RETURNS_DEFAULTS));

        var checkEndpointService = new CheckEndpointService(clientServiceConfig);
        checkEndpointService.setServicePingListener(listener);
        checkEndpointService.checkHealth();

        verify(listener, never()).onPing(any(ServiceEvent.class));
    }
}
