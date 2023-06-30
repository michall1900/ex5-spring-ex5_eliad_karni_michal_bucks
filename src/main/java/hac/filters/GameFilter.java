package hac.filters;

import hac.services.PlayerService;
import hac.services.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class GameFilter implements HandlerInterceptor {
    PlayerService playerService;

    RoomService roomService;


    public GameFilter(RoomService roomService, PlayerService playerService) {
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
        try{
            System.out.println("aaaaaa");
            if(request.isUserInRole("USER") || request.isUserInRole("ADMIN")) {
                roomService.validatePlayerInRoomStatus(request.getUserPrincipal().getName());
            }
            else {
                response.sendRedirect("/lobby/error-message");
                return false;
            }
        }catch (Exception e){
            response.sendRedirect("/lobby/error-message");
            return false;
        }
        return true;
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