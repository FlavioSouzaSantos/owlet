package br.com.flavio.owlet;

import br.com.flavio.owlet.model.CheckHealthLog;
import br.com.flavio.owlet.model.ClientServiceConfig;
import br.com.flavio.owlet.model.HttpMethod;
import br.com.flavio.owlet.model.ServiceEvent;
import br.com.flavio.owlet.services.CheckEndpointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CheckEndpointServiceTest {

    @Test
    void shouldCheckEndpointHealth() {
        var serviceConfig = new ClientServiceConfig();
        serviceConfig.setUrl(URI.create("https://www.google.com.br/"));
        serviceConfig.setHttpMethod(HttpMethod.GET);
        serviceConfig.setTimeoutConnectionInMilliseconds(10_000L);
        serviceConfig.setHttpResponseCodeForCheckIfServiceIsUp(200);

        var checkEndpointService = new CheckEndpointService(serviceConfig);
        var response = checkEndpointService.checkHealth();
        assertEquals(true, response);
    }

    @Test
    void shouldGenerateLog() throws InterruptedException {
        var serviceConfig = new ClientServiceConfig();
        serviceConfig.setServiceName("Teste 1");
        serviceConfig.setUrl(URI.create("https://www.google.com.br"));
        serviceConfig.setHttpMethod(HttpMethod.GET);
        serviceConfig.setTimeoutConnectionInMilliseconds(10_000L);
        serviceConfig.setHttpResponseCodeForCheckIfServiceIsUp(200);

        var checkEndpointService = new CheckEndpointService(serviceConfig);
        for(int i=0; i< 3; i++){
            checkEndpointService.checkHealth();
            Thread.sleep(3_000);
        }

        var logs = checkEndpointService.findAll();
        assertEquals(3, logs.size());
    }

    @Test
    void shouldGenerateLogFail() throws InterruptedException {
        var serviceConfig = new ClientServiceConfig();
        serviceConfig.setUrl(URI.create("https://www.googlexxxx.com.br/"));
        serviceConfig.setServiceName("Teste 1");
        serviceConfig.setHttpMethod(HttpMethod.GET);
        serviceConfig.setTimeoutConnectionInMilliseconds(10_000L);

        var checkEndpointService = new CheckEndpointService(serviceConfig);
        for(int i=0; i< 3; i++){
            checkEndpointService.checkHealth();
            Thread.sleep(3_000);
        }

        var logs = checkEndpointService.findAll(CheckHealthLog::success);
        assertEquals(0L, logs.size());
    }

    @Test
    void shouldTestCheckHealthInSpecificPeriod() throws InterruptedException {
        var serviceConfig = new ClientServiceConfig();
        serviceConfig.setUrl(URI.create("https://www.google.com.br/"));
        serviceConfig.setServiceName("Teste 1");
        serviceConfig.setHttpMethod(HttpMethod.GET);
        serviceConfig.setTimeoutConnectionInMilliseconds(10_000L);
        serviceConfig.setHttpResponseCodeForCheckIfServiceIsUp(200);
        serviceConfig.setCheckPeriodInMilliseconds(5_000L);

        var checkEndpointService = new CheckEndpointService(serviceConfig);
        checkEndpointService.checkHealth();

        assertEquals(true,
                checkEndpointService.canRunNewCheckingIn().toMillis() >= 4_900L && checkEndpointService.canRunNewCheckingIn().toMillis() <= 5_000L);

        checkEndpointService.checkHealth();
        Thread.sleep(3_000L);
        assertEquals(true,
                checkEndpointService.canRunNewCheckingIn().toMillis() >= 1_900L && checkEndpointService.canRunNewCheckingIn().toMillis() <= 2_000L);
    }

    @Test
    void shouldTestNewCheckAfterFailCheckHealth() throws InterruptedException {
        var serviceConfig = new ClientServiceConfig();
        serviceConfig.setUrl(URI.create("https://www.googlexx.com.br/"));
        serviceConfig.setServiceName("Teste");
        serviceConfig.setHttpMethod(HttpMethod.GET);
        serviceConfig.setTimeoutConnectionInMilliseconds(10_000L);
        serviceConfig.setPeriodForNewCheckAfterFailure(3_000L);

        var checkEndpointService = new CheckEndpointService(serviceConfig);
        checkEndpointService.checkHealth();
        checkEndpointService.checkHealth();
        Thread.sleep(2_000L);

        assertEquals(true,
                checkEndpointService.canRunNewCheckingIn().toMillis() >= 900L && checkEndpointService.canRunNewCheckingIn().toMillis() <= 1_000L);

        Thread.sleep(1_000L);
        assertEquals(Duration.ZERO, checkEndpointService.canRunNewCheckingIn());
    }

    @Test
    void shouldTestNotifyEndpointIsFall() {
        var serviceConfig = new ClientServiceConfig();
        serviceConfig.setServiceName("Teste");
        serviceConfig.setUrl(URI.create("https://www.google.com.br/"));
        serviceConfig.setHttpMethod(HttpMethod.GET);
        serviceConfig.setHttpResponseCodeForCheckIfServiceIsUp(204);
        serviceConfig.setTimeoutConnectionInMilliseconds(10_000L);
        serviceConfig.setMaxFailureForCheckIfServiceIsDown(2);

        var eventList = new ArrayList<ServiceEvent>();
        var checkEndpointService = new CheckEndpointService(serviceConfig);
        checkEndpointService.addServiceFallListener(eventList::add);

        checkEndpointService.checkHealth();
        checkEndpointService.checkHealth();

        assertEquals(0, eventList.size());
        checkEndpointService.checkHealth();
        assertEquals(1, eventList.size());
    }

    @Test
    void shouldTestNotifyEndpointIsPing() {
        var serviceConfig = new ClientServiceConfig();
        serviceConfig.setServiceName("Teste Ping");
        serviceConfig.setUrl(URI.create("https://www.google.com.br"));
        serviceConfig.setHttpMethod(HttpMethod.GET);
        serviceConfig.setHttpResponseCodeForCheckIfServiceIsUp(200);
        serviceConfig.setTimeoutConnectionInMilliseconds(10_000L);

        var eventList = new ArrayList<ServiceEvent>();
        var checkEndpointService = new CheckEndpointService(serviceConfig);
        checkEndpointService.setServicePingListener(eventList::add);

        checkEndpointService.checkHealth();
        checkEndpointService.checkHealth();
        assertEquals(2, eventList.size());
        checkEndpointService.checkHealth();
        assertEquals(3, eventList.size());
    }
}
