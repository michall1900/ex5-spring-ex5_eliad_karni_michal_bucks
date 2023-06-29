package hac.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.repo.room.RoomRepository;
import hac.services.PlayerService;
import hac.services.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OnRoomFilter implements HandlerInterceptor {

    final static String INVALID_STATUS = "Invalid request to get into the initialize game page.";

    final static String BOARD_ALREADY_SENT = "You already sent the board, please wait";
    RoomService roomService;

    PlayerService playerService;

    PlayerRepository playerRepository;
    public OnRoomFilter(RoomService roomService, PlayerService playerService){
        this.setRoomService(roomService);
        this.setPlayerService(playerService);
    }

    public OnRoomFilter(){}

    public RoomService getRoomService() {
        return roomService;
    }

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //System.out.println("INTERCEPTOR!!!!!");
//        if(request.isUserInRole("USER") || request.isUserInRole("ADMIN"))
//            System.out.println(request.getUserPrincipal().getName());
//        System.out.println("IN FILTERRR");
        try{
            List<Room> rooms = roomService.getAllRooms();
            for (Room r : rooms)
                System.out.println(r);
        }
        catch (Exception e){
            System.out.println("error while iterate over rooms");
            System.out.println(e);
        }
        try{
            List<Player> players = playerService.getAllPlayers();
            for (Player r : players)
                System.out.println(r);
        }
        catch (Exception e){
            System.out.println("error while iterate over players");
            System.out.println(e);
        }
        try{

            String username = request.getUserPrincipal().getName();
            //TODO move the functionality to one of the services
            Room.RoomEnum roomStatus = playerService.getRoomStatusByUserName(username);
            Player.PlayerStatus playerStatus = playerService.getPlayerStatusByUsername(username);
            //If the player is not ready and the game is waiting for boards, it's ok to get into this path.
            if (roomStatus == Room.RoomEnum.WAITING_FOR_BOARDS && playerStatus == Player.PlayerStatus.NOT_READY)
                return true;
            //TODO change the location of the other fields and the errors about them.It's something more global.
            //If player ready and the game is waiting for boards, that's mean that he tries to get to initial room again
            // when he needs to be in waiting room
            else {
                if (roomStatus == Room.RoomEnum.WAITING_FOR_BOARDS && playerStatus == Player.PlayerStatus.READY) {
                    request.setAttribute("error", BOARD_ALREADY_SENT);
                    response.sendRedirect("/game/wait-to-start-page");
                }
                //If the game is on and the player already in game, the user should get to the game path.
                else if (roomStatus == Room.RoomEnum.ON_GAME && playerStatus == Player.PlayerStatus.ON_GAME) {
                    request.setAttribute("error", BOARD_ALREADY_SENT);
                    // TODO change to the path for the game
                    response.sendRedirect("/game/on-game");
                } else {
                    System.out.println("Invalid room status");
                    //TODO remove player from db + from room list. If we got there it's already exist
                    request.setAttribute("error", INVALID_STATUS);
                    response.sendRedirect("/lobby");

                }
                return false;
            }
        }
        catch (Exception e){
            System.out.println("Invalid room/ player");
            System.out.println(e);
            //TODO if e == NO_ROOM this is an error in the db and we need to delete player from players db.
            request.setAttribute("error", e.getMessage());
            response.sendRedirect("/lobby");
            return false;
        }

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
