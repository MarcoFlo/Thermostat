package it.polito.thermostat.wifi;

import it.polito.thermostat.wifi.object.ESP8266;
import it.polito.thermostat.wifi.object.Programm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;


@SpringBootApplication
@EnableAsync
public class WifiApplication {

    @Bean("threadPoolTaskExecutor")
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(1000);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Async-");
        return executor;
    }


    public static void main(String[] args) {
        SpringApplication.run(WifiApplication.class, args);
//        new SpringApplicationBuilder(WifiApplication.class, args)
//                .properties(Collections.singletonMap("server.port", "8081"))
//                .run(args);

    }

}
