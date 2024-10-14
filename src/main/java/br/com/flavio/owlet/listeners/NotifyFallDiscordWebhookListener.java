package br.com.flavio.owlet.listeners;

import br.com.flavio.owlet.exceptions.NotifyException;
import br.com.flavio.owlet.model.HttpMethod;
import br.com.flavio.owlet.model.NotifyServiceFallDiscordWebhookConfig;
import br.com.flavio.owlet.model.ServiceEvent;
import com.google.gson.Gson;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class NotifyFallDiscordWebhookListener implements ServiceFallListener {

    private final NotifyServiceFallDiscordWebhookConfig config;
    private final Gson gson = new Gson();

    public NotifyFallDiscordWebhookListener(NotifyServiceFallDiscordWebhookConfig config) {
        this.config = config;
    }

    @Override
    public synchronized void onFall(ServiceEvent event) {
        try (var client = HttpClient.newHttpClient()){
            var discordWebhookMessage = config.createDiscordWebhookMessage(config.createMessage(event));
            var bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(discordWebhookMessage));

            var requestBuilder = HttpRequest.newBuilder()
                    .uri(config.getUrl())
                    .header("content-type", "application/json")
                    .method(HttpMethod.POST.name(), bodyPublisher)
                    .timeout(Duration.of(10, SECONDS));

            var response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.discarding());
            if(response.statusCode() != 200 && response.statusCode() != 204){
                throw new Exception(String.format("Request responded with status code -> %d", response.statusCode()));
            }
        }catch (Exception ex){
            throw new NotifyException(ex.getMessage(), ex);
        }
    }
}
