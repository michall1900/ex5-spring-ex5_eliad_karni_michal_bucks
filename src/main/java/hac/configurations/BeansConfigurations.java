package hac.configurations;

import hac.beans.RoomLockHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
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

    @Bean
    @Scope("singleton")
    public ReentrantReadWriteLock getLockForAllDb(){
        return new ReentrantReadWriteLock();
    }

    @Bean
    @Scope("singleton")
    public HashMap<Long, ReentrantReadWriteLock> getRoomLocksMap(){
        return new HashMap<>();
    }

    @Bean
    @Scope("singleton")
    public RoomLockHandler getRoomLock(){
        return new RoomLockHandler();
    }

}
