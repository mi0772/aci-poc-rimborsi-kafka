package it.almaviva.aci.pocrimborsiproducer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Value("${app.async-pool-size:10}")
    private int poolSize;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(poolSize);
        executor.setCorePoolSize(poolSize);
        executor.setThreadNamePrefix("rimborso-send-");
        executor.initialize();
        return executor;
    }
}
