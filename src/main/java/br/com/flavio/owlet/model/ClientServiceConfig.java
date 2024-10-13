package br.com.flavio.owlet.model;

import br.com.flavio.owlet.annotaions.PrefixProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@NoArgsConstructor
@PrefixProperty("client.service")
public class ClientServiceConfig {
    private URI url;
    private HttpMethod httpMethod = HttpMethod.GET;
    private String serviceName;
    private String applicationName;
    private int httpResponseCodeForCheckIfServiceIsUp = 200;
    private long checkPeriodInMilliseconds = 10_000;
    private long timeoutConnectionInMilliseconds = 10_000;
    private int maxFailureForCheckIfServiceIsDown = 2;
    private long periodForNewCheckAfterFailure = 10_000;
}
