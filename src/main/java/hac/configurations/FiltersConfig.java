package hac.configurations;

import hac.filters.OnRoomFilter;
import hac.repo.player.PlayerRepository;
import hac.repo.room.RoomRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Configuration
public class FiltersConfig implements WebMvcConfigurer {
    @Autowired
    RoomRepository roomRepo;

    @Autowired
    PlayerRepository playerRepo;

    @Resource(name="getRoomLock")
    ReentrantReadWriteLock roomLock;

    @Resource(name="getPlayerLock")
    ReentrantReadWriteLock playerLock;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OnRoomFilter(roomRepo, playerRepo, roomLock, playerLock))
                .addPathPatterns("/game/init");
    }
}
