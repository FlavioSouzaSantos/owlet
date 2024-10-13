package br.com.flavio.owlet.services;

import br.com.flavio.owlet.listeners.ServiceFallListener;
import br.com.flavio.owlet.listeners.ServicePingListener;
import br.com.flavio.owlet.model.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.time.temporal.ChronoUnit.MILLIS;

public class CheckEndpointService {

    private final List<CheckHealthLog> checkHealthLogs = new ArrayList<>();
    private final ClientServiceConfig clientServiceConfig;
    private final HttpRequest request;
    private ServiceFallListener serviceFallListener;
    private ServicePingListener servicePingListener;
    private int countSequenceFail=0;

    public CheckEndpointService(ClientServiceConfig clientServiceConfig){
        this.clientServiceConfig = clientServiceConfig;
        this.request = HttpRequest.newBuilder()
                .uri(clientServiceConfig.getUrl())
                .method(clientServiceConfig.getHttpMethod().name(), HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.of(clientServiceConfig.getTimeoutConnectionInMilliseconds(), MILLIS))
                .build();
    }

    public boolean checkHealth() {
        var startTime = LocalDateTime.now();
        var result = false;
        try (var client = HttpClient.newHttpClient()) {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.statusCode() == clientServiceConfig.getHttpResponseCodeForCheckIfServiceIsUp();

            var healthLog = new CheckHealthLog(startTime, LocalDateTime.now(), clientServiceConfig.getServiceName(),
                    clientServiceConfig.getApplicationName(), clientServiceConfig.getUrl(), clientServiceConfig.getHttpMethod(), response.statusCode(),
                    result, null);
            checkHealthLogs.add(healthLog);

            Logger.getGlobal().log(healthLog.success() ? Level.INFO : Level.WARNING,
                    healthLog.toString());

            countSequenceFail = result ? 0 : countSequenceFail + 1;
        } catch (Exception ex) {
            var healthLog = new CheckHealthLog(startTime, LocalDateTime.now(), clientServiceConfig.getServiceName(),
                    clientServiceConfig.getApplicationName(), clientServiceConfig.getUrl(), clientServiceConfig.getHttpMethod(), -1,
                    false, ex.getMessage());

            checkHealthLogs.add(healthLog);
            Logger.getGlobal().log(Level.SEVERE, healthLog.toString());
            countSequenceFail ++;
        }

        if(serviceFallListener != null && countSequenceFail > clientServiceConfig.getMaxFailureForCheckIfServiceIsDown()){
            serviceFallListener.onFall(new ServiceEvent(clientServiceConfig, LocalDateTime.now()));
            countSequenceFail = 0;
        }
        if(servicePingListener != null && result){
            servicePingListener.onPing(new ServiceEvent(clientServiceConfig, LocalDateTime.now()));
        }
        return result;
    }

    public List<CheckHealthLog> findAll() {
        return checkHealthLogs;
    }

    public List<CheckHealthLog> findAll(Predicate<CheckHealthLog> predicate) {
        return checkHealthLogs.stream().filter(predicate).toList();
    }

    public Duration canRunNewCheckingIn(){
        if(checkHealthLogs.isEmpty())
            return Duration.ZERO;

        var interval = checkHealthLogs.getLast().success()
                ? clientServiceConfig.getCheckPeriodInMilliseconds()
                : (checkHealthLogs.size() >= 2 && !checkHealthLogs.get(checkHealthLogs.size()-2).success() ?
                    clientServiceConfig.getPeriodForNewCheckAfterFailure() : clientServiceConfig.getCheckPeriodInMilliseconds());

        var diff = Duration.between(checkHealthLogs.getLast().endExecution(), LocalDateTime.now()).toMillis();
        return diff < interval ? Duration.of(interval - diff, MILLIS) : Duration.ZERO;
    }

    public void setServiceFallListener(ServiceFallListener listener) {
        serviceFallListener = listener;
    }

    public void setServicePingListener(ServicePingListener listener) {
        servicePingListener = listener;
    }
}
