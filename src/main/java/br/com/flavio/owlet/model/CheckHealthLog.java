package br.com.flavio.owlet.model;

import java.net.URI;
import java.time.LocalDateTime;

public record CheckHealthLog(LocalDateTime startExecution, LocalDateTime endExecution,
                             String serviceName, String applicationName, URI url, HttpMethod method, int httpStatusCodeResponse,
                             boolean success, String errorMessage) {
}
