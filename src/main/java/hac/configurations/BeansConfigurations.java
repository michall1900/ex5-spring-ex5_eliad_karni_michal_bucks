package hac.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Configuration
public class BeansConfigurations {

    @Bean
    @Scope("singleton")
    public Map<Long, ExecutorService> getRoomExecutors(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    @Scope("singleton")
    public ReentrantReadWriteLock roomExecutorsLock(){
        return new ReentrantReadWriteLock();
    }



}
