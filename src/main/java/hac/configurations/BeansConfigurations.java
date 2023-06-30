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

/**
 * The Configuration manages the singleton beans of the project.
 */
@Configuration
public class BeansConfigurations {
    /**
     * A singleton bean of each room's long polling thread.
     * @return A singleton bean of each room's long polling thread.
     */
    @Bean
    @Scope("singleton")
    public Map<Long, ExecutorService> getRoomExecutors(){
        return new ConcurrentHashMap<>();
    }

    /**
     * A singleton bean of the lock of the whole DB.
     * @return A singleton bean of the lock of the whole DB.
     */
    @Bean
    @Scope("singleton")
    public ReentrantReadWriteLock getLockForAllDb(){
        return new ReentrantReadWriteLock();
    }

    /**
     * A singleton bean of the locks of each room's lock.
     * @return A singleton bean of the locks of each room's lock.
     */
    @Bean
    @Scope("singleton")
    public HashMap<Long, ReentrantReadWriteLock> getRoomLocksMap(){
        return new HashMap<>();
    }

    /**
     * A singleton bean of the locks of each room's lock manager.
     * @return A singleton bean of the locks of each room's lock manager.
     */
    @Bean
    @Scope("singleton")
    public RoomLockHandler getRoomLock(){
        return new RoomLockHandler();
    }

}
