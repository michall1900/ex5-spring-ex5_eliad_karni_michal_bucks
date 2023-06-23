package hac.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.repo.room.RoomRepository;
import hac.services.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OnRoomFilter implements HandlerInterceptor {

    RoomService roomService;


    PlayerRepository playerRepository;
    public OnRoomFilter(RoomService roomService){
        this.setRoomService(roomService);
    }

    public OnRoomFilter(){}

    public RoomService getRoomService() {
        return roomService;
    }

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println("IN FILTERRR");
        List<Room> rooms = roomService.getAllRooms();
        try{
            for (Room r : rooms)
                System.out.println(r);
        }
        catch (Exception e){
            System.out.println(e);
        }

        List<Player> players = playerRepository.findAll();
        try{
            for (Player r : players)
                System.out.println(r);
        }
        catch (Exception e){
            System.out.println(e);
        }

        return true; // continue with the request to next filter or to controller
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, //
                           Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

    }
}
