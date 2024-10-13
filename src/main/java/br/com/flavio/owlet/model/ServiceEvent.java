package br.com.flavio.owlet.model;

import br.com.flavio.owlet.serializers.LocalDateTimeSerializer;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public record ServiceEvent(LocalDateTime time, String applicationName, String serviceName) {
    public ServiceEvent(ClientServiceConfig clientServiceConfig, LocalDateTime time){
        this(time, clientServiceConfig.getApplicationName(), clientServiceConfig.getServiceName());
    }

    public String toJson() {
        var gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .create();
        return gson.toJson(this);
    }
}
