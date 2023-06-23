package hac.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.repo.room.RoomRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OnRoomFilter implements HandlerInterceptor {

    RoomRepository roomRepo;

    PlayerRepository playerRepo;

    ReentrantReadWriteLock roomLock;

    ReentrantReadWriteLock playerLock;

    public OnRoomFilter(RoomRepository roomRepo, PlayerRepository playerRepo,
                        ReentrantReadWriteLock roomLock, ReentrantReadWriteLock playerLock){
        setRoomLock(roomLock);
        setPlayerLock(playerLock);
        setPlayerRepo(playerRepo);
        setRoomRepo(roomRepo);
    }

    public OnRoomFilter(){}

    public RoomRepository getRoomRepo() {
        return roomRepo;
    }

    public void setRoomRepo(RoomRepository roomRepo) {
        this.roomRepo = roomRepo;
    }

    public PlayerRepository getPlayerRepo() {
        return playerRepo;
    }

    public void setPlayerRepo(PlayerRepository playerRepo) {
        this.playerRepo = playerRepo;
    }

    public ReentrantReadWriteLock getRoomLock() {
        return roomLock;
    }

    public void setRoomLock(ReentrantReadWriteLock roomLock) {
        this.roomLock = roomLock;
    }

    public ReentrantReadWriteLock getPlayerLock() {
        return playerLock;
    }

    public void setPlayerLock(ReentrantReadWriteLock playerLock) {
        this.playerLock = playerLock;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println("IN FILTERRR");
        List<Room> rooms = roomRepo.findAll();
        for (Room r : rooms) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String roomJson = objectMapper.writeValueAsString(r);

                System.out.println(roomJson);
            }
            catch (JsonProcessingException e){
                System.out.println(e);
            }
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
