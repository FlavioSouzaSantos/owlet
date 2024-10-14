package br.com.flavio.owlet.model;

import br.com.flavio.owlet.annotaions.PrefixProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@PrefixProperty("notify.service.fall.discord.webhook")
public class NotifyServiceFallDiscordWebhookConfig {
    private URI url;
    private String messageTemplate;

    public String createMessage(ServiceEvent event) {
        if(event != null && messageTemplate != null && !messageTemplate.isBlank()) {
            return messageTemplate
                    .replaceAll("\\{applicationName}", event.applicationName())
                    .replaceAll("\\{serviceName}", event.serviceName())
                    .replaceAll("\\{time}", event.time().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        return null;
    }

    public DiscordWebhookMessage createDiscordWebhookMessage(String message) {
        return new DiscordWebhookMessage(null, null, message);
    }
}
