package hac.configurations;

import hac.filters.FinishGameFilter;
import hac.filters.GameFilter;
import hac.filters.InRoomFilter;
import hac.filters.OnRoomFilter;
import hac.services.PlayerService;
import hac.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FiltersConfig implements WebMvcConfigurer {

    /**Room service*/
    @Autowired
    RoomService roomService;

    /**Player service*/
    @Autowired
    PlayerService playerService;

    /**
     * To handle with static folder
     * @param registry ResourceHandlerRegistry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/static/**")
                .addResourceLocations("/static/");
    }

    /**
     * To add interceptors
     * @param registry InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OnRoomFilter(roomService, playerService))
                .addPathPatterns("/game/init");
        registry.addInterceptor(new InRoomFilter(roomService, playerService))
                .addPathPatterns("/", "/lobby","/lobby/error-message", "/how-to-play", "/logout");
        registry.addInterceptor(new GameFilter(roomService, playerService))
                .addPathPatterns("/game/**");
        registry.addInterceptor(new FinishGameFilter(roomService, playerService))
                .addPathPatterns("/game/finish-game");
    }
}
