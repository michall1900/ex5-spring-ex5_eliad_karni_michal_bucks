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

    public final static String INVALID_STATUS = "Invalid request to get into the initialize game page.";

    public final static String BOARD_ALREADY_SENT = "You already sent the board, please wait";
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
        try{
            String username = request.getUserPrincipal().getName();
            //TODO move the functionality to one of the services
            String res = roomService.getValidationErrorForInitGame(username, true, true);
            //If the player is not ready and the game is waiting for boards, it's ok to get into this path.
            if (res==null || res.isEmpty())
                return true;
            //TODO change the location of the other fields and the errors about them. It's something more global.
            //If player ready and the game is waiting for boards, that's mean that he tries to get to initial room again
            // when he needs to be in waiting room
            else {
                if (res.equals(BOARD_ALREADY_SENT)) {
                    request.setAttribute("error", res);
                    response.sendRedirect("/game/wait-to-start-page");
                }
                else {
                    response.sendRedirect("/lobby/error-message");
                }
                return false;
            }
        }
        catch (Exception e){
            response.sendRedirect("/lobby/error-message");
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
