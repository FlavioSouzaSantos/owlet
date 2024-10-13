package br.com.flavio.owlet.model;

import br.com.flavio.owlet.annotaions.PrefixProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@PrefixProperty("notify.service.fall.discord.webhook")
public class NotifyServiceFallDiscordWebhookConfig {
    private String urlTemplate;
    private String id;
    private String token;
    private String messageTemplate;

    public URI createUrl() {
        if(urlTemplate == null || urlTemplate.isBlank() || id == null || id.isBlank() || token == null || token.isBlank()){
            return null;
        }
        return URI.create(urlTemplate.replaceAll("\\{webhook.id}", id)
                .replaceAll("\\{webhook.token}", token));
    }

    public String createMessage(ServiceEvent event) {
        if(event != null && messageTemplate != null && !messageTemplate.isBlank()) {
            return messageTemplate
                    .replaceAll("\\{applicationName}", event.applicationName())
                    .replaceAll("\\{serviceName}", event.serviceName())
                    .replaceAll("\\{time}", event.time().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        return null;
    }

    public Map<String, Object> createFormParam(String message) {
        var formParam = new HashMap<String, Object>();
        formParam.put("content", message);
        return formParam;
    }
}
