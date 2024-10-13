package br.com.flavio.owlet;

import br.com.flavio.owlet.exceptions.ConfigException;
import br.com.flavio.owlet.listeners.NotifyFallDiscordWebhookListener;
import br.com.flavio.owlet.listeners.NotifyPingServiceListener;
import br.com.flavio.owlet.services.ConfigService;
import br.com.flavio.owlet.threads.CheckEndpointThread;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {

    public static void main(String[] args){
        try {
            var configService = new ConfigService();

            var properties = new Properties();
            properties.load(new FileInputStream(getConfigFilePath().toFile()));

            var notifyServicePingConfig = configService.loadConfigNotifyServicePingProperties(properties);
            var notifyServiceFallConfig = configService.loadConfigNotifyServiceFallProperties(properties);

            NotifyPingServiceListener notifyPingListener = null;
            NotifyFallDiscordWebhookListener notifyFallDiscordWebhookListener = null;

            var propertiesList = PropertiesUtil.spreadByPrefixIndex("client.service", properties);
            if(!propertiesList.isEmpty()){
                if(notifyServicePingConfig != null){
                    configService.checkRequiredFields(notifyServicePingConfig);
                    notifyPingListener = new NotifyPingServiceListener(notifyServicePingConfig);
                }
                if(notifyServiceFallConfig != null){
                    configService.checkRequiredFields(notifyServiceFallConfig);
                    notifyFallDiscordWebhookListener = new NotifyFallDiscordWebhookListener(notifyServiceFallConfig);
                }

                try(var executor = Executors.newVirtualThreadPerTaskExecutor()){
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> executor.shutdownNow()));

                    for(Properties configProperties : propertiesList){
                        try{
                            var config = configService.loadClientServiceConfigProperties(configProperties);
                            configService.checkRequiredFields(config);

                            executor.submit(new CheckEndpointThread(config, notifyPingListener, notifyFallDiscordWebhookListener));
                        }catch (ConfigException ex) {
                            Logger.getGlobal().log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                }
            }
            System.exit(0);
        } catch (Throwable ex){
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    private static Path getConfigFilePath() {
        var path = Paths.get(System.getProperty("user.dir"), "config.properties");
        if(Files.notExists(path))
            throw new ConfigException(String.format("Config file called config.properties not found in %s", System.getProperty("user.dir")));
        return path;
    }
}