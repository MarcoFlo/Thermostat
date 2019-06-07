package it.polito.thermostat.controllermd.services.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
public class ExecuteShellComandService {
    private boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public StringBuilder execute(String command) {
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("sh", "-c", command);
        }
        builder.directory(new File(System.getProperty("user.home")));
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder stringBuilder = new StringBuilder();
        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), (line -> stringBuilder.append(line + "\n")));
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert exitCode == 0;
        return stringBuilder;
    }


    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
        }
    }

}