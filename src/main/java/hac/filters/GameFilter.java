package hac.filters;

import hac.services.PlayerService;
import hac.services.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


/**
 * The filter was meant to handle situation that a player sent an "in game related" rest and the room
 * is not full but because of an error discovered too close to the submission deadline, we decided to disable the
 * filter's functionality.
 */
public class GameFilter implements HandlerInterceptor {
    /**
     * The member service acts like an api to the rooms DB.
     */
    private RoomService roomService;
    /**
     * The member service acts like an api to the players DB.
     */
    private PlayerService playerService;


    /**
     * Ctor of the filter.
     * @param roomService The member service acts like an api to the rooms DB.
     * @param playerService The member service acts like an api to the players DB.
     */
    public GameFilter(RoomService roomService, PlayerService playerService) {
        this.setRoomService(roomService);
        this.setPlayerService(playerService);
    }

    /**
     * The room service getter.
     * @return The room service.
     */
    public RoomService getRoomService() {
        return roomService;
    }

    /**
     * The room service setter.
     * @param roomService the room service
     */
    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }


    /**
     * The player service getter.
     * @return The player service.
     */
    public PlayerService getPlayerService() {
        return playerService;
    }

    /**
     * The player service setter.
     * @param playerService The player setter.
     */
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * The handler was meant to handle situation that a player sent an "in game related" rest and the room
     * is not full but because of an error discovered too close to the submission deadline, we decided to disable the
     * filter's functionality.
     * @param request The user's game.
     * @param response not used
     * @param handler not used
     * @return If the request needs to be blocked or not.
     * @throws Exception not thrown.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        try{
            if(request.isUserInRole("USER") || request.isUserInRole("ADMIN")) {
                roomService.validatePlayerInRoomStatus(request.getUserPrincipal().getName());
            }
//            else {
//                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//                response.sendRedirect("/lobby/error-message");
//                return false;
//                //return true;
//            }
        }catch (Exception e){
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.sendRedirect("/lobby/error-message");
//            return false;
        }
        return true;
    }

    /**
     * The function has no use.
     * @param request unused.
     * @param response unused.
     * @param handler unused.
     * @throws Exception not thrown
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, //
                           Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * The function has no use.
     * @param request unused.
     * @param response unused.
     * @param handler unused.
     * @param ex unused.
     * @throws Exception not thrown
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
    }
}