package br.com.flavio.owlet.threads;

import br.com.flavio.owlet.listeners.ServiceFallListener;
import br.com.flavio.owlet.listeners.ServicePingListener;
import br.com.flavio.owlet.model.ClientServiceConfig;
import br.com.flavio.owlet.services.CheckEndpointService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckEndpointThread implements Runnable {

    private final CheckEndpointService service;

    public CheckEndpointThread(ClientServiceConfig clientServiceConfig, ServicePingListener servicePingListener, ServiceFallListener serviceFallListener) {
        if(clientServiceConfig != null){
            service = new CheckEndpointService(clientServiceConfig);
            if(servicePingListener != null)
                service.setServicePingListener(servicePingListener);
            if(serviceFallListener != null)
                service.setServiceFallListener(serviceFallListener);
        } else {
            service = null;
        }
    }

    @Override
    public void run() {
        while (service != null){
            try {
                var sleep = service.canRunNewCheckingIn();
                if(sleep.isZero()){
                    service.checkHealth();
                } else {
                    Thread.sleep(sleep.toMillis());
                }
            } catch(Exception ex) {
                Logger.getGlobal().log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
}
