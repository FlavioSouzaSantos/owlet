package br.com.flavio.owlet.services;

import br.com.flavio.owlet.PropertiesUtil;
import br.com.flavio.owlet.exceptions.ConfigException;
import br.com.flavio.owlet.model.ClientServiceConfig;
import br.com.flavio.owlet.model.NotifyServiceFallDiscordWebhookConfig;
import br.com.flavio.owlet.model.NotifyServicePingConfig;

import java.util.Properties;

public class ConfigService {

    public ClientServiceConfig loadClientServiceConfigProperties(final Properties properties) {
        return PropertiesUtil.loadProperties(properties, ClientServiceConfig.class);
    }

    public NotifyServiceFallDiscordWebhookConfig loadConfigNotifyServiceFallProperties(final Properties properties) {
        return PropertiesUtil.loadProperties(properties, NotifyServiceFallDiscordWebhookConfig.class);
    }

    public NotifyServicePingConfig loadConfigNotifyServicePingProperties(final Properties properties) {
        return PropertiesUtil.loadProperties(properties, NotifyServicePingConfig.class);
    }

    public void checkRequiredFields(final ClientServiceConfig clientServiceConfig) {
        if(clientServiceConfig.getUrl() == null)
            throw new ConfigException("The URL not found.");
        if(clientServiceConfig.getHttpMethod() == null)
            throw new ConfigException("The HTTP method not found.");
        if(clientServiceConfig.getServiceName() == null || clientServiceConfig.getServiceName().isBlank())
            throw new ConfigException("The client name not found.");
        if(clientServiceConfig.getApplicationName() == null || clientServiceConfig.getApplicationName().isBlank())
            throw new ConfigException("The application name not found.");
    }

    public void checkRequiredFields(final NotifyServiceFallDiscordWebhookConfig notifyServiceFallDiscordWebhookConfig) {
        if(notifyServiceFallDiscordWebhookConfig.getUrl() == null)
            throw new ConfigException("The URL template not found.");
        if(notifyServiceFallDiscordWebhookConfig.getMessageTemplate() == null || notifyServiceFallDiscordWebhookConfig.getMessageTemplate().isBlank())
            throw new ConfigException("The message template not found.");
    }

    public void checkRequiredFields(final NotifyServicePingConfig notifyServicePingConfig) {
        if(notifyServicePingConfig.getUrl() == null)
            throw new ConfigException("The URL not found.");
        if(notifyServicePingConfig.getHttpMethod() == null)
            throw new ConfigException("The HTTP method not found.");
    }
}
