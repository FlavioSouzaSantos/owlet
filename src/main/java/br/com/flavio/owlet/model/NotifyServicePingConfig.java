package br.com.flavio.owlet.model;

import br.com.flavio.owlet.annotaions.PrefixProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@NoArgsConstructor
@PrefixProperty("notify.service.ping")
public class NotifyServicePingConfig {
    private URI url;
    private HttpMethod httpMethod = HttpMethod.POST;
    private boolean sendPayload=true;
}
