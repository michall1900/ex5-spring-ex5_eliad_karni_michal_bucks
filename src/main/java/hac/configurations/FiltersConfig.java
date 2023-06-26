package hac.configurations;

import hac.filters.InRoomFilter;
import hac.filters.OnRoomFilter;
import hac.repo.player.PlayerRepository;
import hac.repo.room.RoomRepository;
import hac.services.PlayerService;
import hac.services.RoomService;
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
//    @Autowired
//    RoomRepository roomRepo;
//
//    @Autowired
//    PlayerRepository playerRepo;
//
//    @Resource(name="getRoomLock")
//    ReentrantReadWriteLock roomLock;
//
//    @Resource(name="getPlayerLock")
//    ReentrantReadWriteLock playerLock;

    @Autowired
    RoomService roomService;

    @Autowired
    PlayerService playerService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/static/**")
                .addResourceLocations("/static/");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OnRoomFilter(roomService, playerService))
                .addPathPatterns("/game/init");
        registry.addInterceptor(new InRoomFilter(roomService, playerService))
                .addPathPatterns("/lobby", "/how-to-play", "/logout");

    }
//@Override
//public void addInterceptors(InterceptorRegistry registry) {
//    registry.addInterceptor(new OnRoomFilter(roomService, playerRepository))
//            .addPathPatterns("/game/init");
//}
}
