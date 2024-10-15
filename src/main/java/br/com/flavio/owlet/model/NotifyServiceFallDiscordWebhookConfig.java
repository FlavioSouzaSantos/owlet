package br.com.flavio.owlet.model;

import br.com.flavio.owlet.annotaions.PrefixProperty;
import lombok.*;

import java.net.URI;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@PrefixProperty("notify.service.fall.discord.webhook")
public class NotifyServiceFallDiscordWebhookConfig {
    private URI url;
    private String username;
    private URI avatarUrl;
    private String messageTemplate;

    @Setter(AccessLevel.NONE)
    private String dateTimePattern = DateTimeFormatter.ISO_LOCAL_DATE_TIME.toString();

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public String createMessage(ServiceEvent event) {
        if(event != null && messageTemplate != null && !messageTemplate.isBlank()) {
            return messageTemplate
                    .replaceAll("\\{applicationName}", event.applicationName())
                    .replaceAll("\\{serviceName}", event.serviceName())
                    .replaceAll("\\{time}", event.time().format(dateTimeFormatter));
        }
        return null;
    }

    public DiscordWebhookMessage createDiscordWebhookMessage(String message) {
        return new DiscordWebhookMessage(username, avatarUrl, message);
    }

    public void setDateTimePattern(String dateTimePattern) {
        if(dateTimePattern != null && !dateTimePattern.isBlank()){
            this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
            this.dateTimePattern = dateTimePattern;
        }
    }
}
