package br.com.flavio.owlet.listeners;

import br.com.flavio.owlet.exceptions.NotifyException;
import br.com.flavio.owlet.model.HttpMethod;
import br.com.flavio.owlet.model.NotifyServiceFallDiscordWebhookConfig;
import br.com.flavio.owlet.model.ServiceEvent;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

import static java.time.temporal.ChronoUnit.SECONDS;

public class NotifyFallDiscordWebhookListener implements ServiceFallListener {

    private final NotifyServiceFallDiscordWebhookConfig config;

    public NotifyFallDiscordWebhookListener(NotifyServiceFallDiscordWebhookConfig config) {
        this.config = config;
    }

    @Override
    public synchronized void onFall(ServiceEvent event) {
        try (var client = HttpClient.newHttpClient()){
            var formParam = config.createFormParam(config.createMessage(event));
            var stringBody = convertMapFormParamToStringFormParam(formParam);

            var bodyPublisher = HttpRequest.BodyPublishers.ofString(stringBody);

            var requestBuilder = HttpRequest.newBuilder()
                    .uri(config.createUrl())
                    .header("content-type", "multipart/form-data")
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

    private String convertMapFormParamToStringFormParam(Map<String, Object> param) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, Object> singleEntry : param.entrySet()) {
            if (formBodyBuilder.length() > 0) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }
}
