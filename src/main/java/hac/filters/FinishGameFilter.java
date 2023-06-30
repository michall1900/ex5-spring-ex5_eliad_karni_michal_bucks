package hac.filters;

import hac.services.PlayerService;
import hac.services.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Filter makes sure that after the user has finished the game and the finish page displayed, he will be
 * removed from the room.
 */
public class FinishGameFilter implements HandlerInterceptor {
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
    public FinishGameFilter(RoomService roomService, PlayerService playerService) {
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
     * @return The room setter.
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
     * @return The player setter.
     */
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }


    /**
     * The function has no use.
     * @param request unused.
     * @param response unused.
     * @param handler unused.
     * @throws Exception not thrown
     * @return true always.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        return true;
    }

    /**
     * The pre handle validates that the player us removed from its room.
     * @param request The request which fills the username.
     * @param response unused.
     * @param handler unused.
     * @throws Exception not thrown
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        try {
            roomService.removePlayer(request.getUserPrincipal().getName());
        }catch (Exception e){
        }
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
