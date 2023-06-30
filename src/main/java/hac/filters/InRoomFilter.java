package hac.filters;

import hac.services.PlayerService;
import hac.services.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class InRoomFilter  implements HandlerInterceptor {
    PlayerService playerService;

    RoomService roomService;


    public InRoomFilter(RoomService roomService, PlayerService playerService) {
        this.setRoomService(roomService);
        this.setPlayerService(playerService);
    }

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
        try {
            System.out.println("in in game filter");
                try {
                    roomService.removePlayer(request.getUserPrincipal().getName());
                }catch (Exception e){}
        }catch (Exception e){
            return false;
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
