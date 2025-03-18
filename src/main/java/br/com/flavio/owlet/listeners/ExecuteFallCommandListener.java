package br.com.flavio.owlet.listeners;

import br.com.flavio.owlet.model.ServiceEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

                var linesError = new BufferedReader(new InputStreamReader(process.getErrorStream()))
                        .lines().collect(Collectors.joining("\n"));
                if(!linesError.isBlank()){
                    Logger.getGlobal().log(Level.SEVERE, linesError);
                }

                var linesSuccess = new BufferedReader(new InputStreamReader(process.getInputStream()))
                        .lines().collect(Collectors.joining("\n"));
                if(!linesSuccess.isBlank()){
                    Logger.getGlobal().log(Level.INFO, linesSuccess);
                }

                int exitCode = process.waitFor();
                Logger.getGlobal().log(Level.INFO,
                        String.format("The command '%s' it was run with exit code -> %d", command, exitCode));
            } catch (IOException | InterruptedException ex){
                Logger.getGlobal().log(Level.SEVERE, ex.getMessage());
            }
        }
    }
}
