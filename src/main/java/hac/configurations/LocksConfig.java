package hac.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Configuration
public class LocksConfig {

//    @Bean
//    @Scope("singleton")
//    public ReentrantReadWriteLock getRoomLock(){
//        return new ReentrantReadWriteLock();
//    }
//
//    @Bean
//    @Scope("singleton")
//    public ReentrantReadWriteLock getPlayerLock(){
//        return new ReentrantReadWriteLock();
//    }


//    @Bean
//    @Scope("singleton")
//    public ReentrantReadWriteLock getBoardLock(){
//        return new ReentrantReadWriteLock();
//    }
}
