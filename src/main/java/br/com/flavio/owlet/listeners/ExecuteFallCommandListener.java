package br.com.flavio.owlet.listeners;

import br.com.flavio.owlet.model.ServiceEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecuteFallCommandListener implements ServiceFallListener {
    private final String command;

    public ExecuteFallCommandListener(String command) {
        this.command = command;
    }

    @Override
    public void onFall(ServiceEvent event) {
        if(command != null && !command.isBlank()){
            try {
                var builder = new ProcessBuilder();
                builder.command(command.split(" "));

                var process = builder.start();

                new BufferedReader(new InputStreamReader(process.getErrorStream()))
                        .lines().forEach(System.err::println);

                new BufferedReader(new InputStreamReader(process.getInputStream()))
                        .lines().forEach(System.out::println);

                int exitCode = process.waitFor();
                Logger.getGlobal().log(Level.INFO,
                        String.format("The command '%s' it was run with exit code -> %d", command, exitCode));
            } catch (IOException | InterruptedException ex){
                Logger.getGlobal().log(Level.SEVERE, ex.getMessage());
            }
        }
    }
}
