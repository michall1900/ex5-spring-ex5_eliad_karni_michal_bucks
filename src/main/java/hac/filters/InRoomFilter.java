package hac.filters;

import hac.services.PlayerService;
import hac.services.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * The filter makes sure that if a player (which means a user inside a room) enters pages that aren't in game related,
 * he will be removed from its room.
 */
public class InRoomFilter  implements HandlerInterceptor {
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
    public InRoomFilter(RoomService roomService, PlayerService playerService) {
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
     * @param roomService The room setter.
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
     * The filter makes sure that if a player (which means a user inside a room) enters pages that aren't in game related,
     * he will be removed from its room.
     * @param request The request is used to get the user's information.
     * @param response not used
     * @param handler not used
     * @return If no exception has thrown through the removal, the rest is able to continue its request.
     * @throws Exception If the user removal has failed.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        try {
            try {
                roomService.removePlayer(request.getUserPrincipal().getName());
            }catch (Exception e){}
        }catch (Exception e){
            return false;
        }
        return true; // continue with the request to next filter or to controller
    }

    /**
     * The function has no use.
     * @param request unused.
     * @param response unused.
     * @param handler unused.
     * @throws Exception not thrown
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * The function has no use.
     * @param request unused.
     * @param response unused.
     * @param handler unused.
     * @throws Exception not thrown
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
    }
}
