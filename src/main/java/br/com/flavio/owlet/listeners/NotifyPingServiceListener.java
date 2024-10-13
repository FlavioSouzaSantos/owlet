package br.com.flavio.owlet.listeners;

import br.com.flavio.owlet.exceptions.NotifyException;
import br.com.flavio.owlet.model.NotifyServicePingConfig;
import br.com.flavio.owlet.model.ServiceEvent;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class NotifyPingServiceListener implements ServicePingListener {

    private final NotifyServicePingConfig config;

    public NotifyPingServiceListener(NotifyServicePingConfig config){
        this.config = config;
    }

    @Override
    public synchronized void onPing(ServiceEvent event) {
        try (var client = HttpClient.newHttpClient()) {
            var bodyPublisher = config.isSendPayload()
                    ? HttpRequest.BodyPublishers.ofString(event.toJson())
                    : HttpRequest.BodyPublishers.noBody();

            var requestBuilder = HttpRequest.newBuilder()
                    .uri(config.getUrl())
                    .method(config.getHttpMethod().name(), bodyPublisher)
                    .timeout(Duration.of(5, SECONDS));

            if(config.isSendPayload()){
                requestBuilder.header("content-type", "application/json");
            }

            var response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.discarding());
            if(response.statusCode() < 200 || response.statusCode() > 299){
                throw new Exception(String.format("Request responded with status code -> %d", response.statusCode()));
            }
        } catch (Exception ex){
            throw new NotifyException(ex.getMessage(), ex);
        }
    }
}
