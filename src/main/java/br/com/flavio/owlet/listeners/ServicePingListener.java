package br.com.flavio.owlet.listeners;

import br.com.flavio.owlet.model.ServiceEvent;

@FunctionalInterface
public interface ServicePingListener {
    void onPing(ServiceEvent event);
}
