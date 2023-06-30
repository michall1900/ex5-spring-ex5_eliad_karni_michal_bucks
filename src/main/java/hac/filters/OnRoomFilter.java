package hac.filters;

import hac.services.PlayerService;
import hac.services.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


/**
 * Filter to /game/init - handle with many kinds of situations that can happen when getting to this route.
 */
public class OnRoomFilter implements HandlerInterceptor {

    /**
     * An error message when the status is invalid
     */
    public final static String INVALID_STATUS = "Invalid request to get into the initialize game page.";

    /**
     * An error message tells the board already sent.
     */
    public final static String BOARD_ALREADY_SENT = "You already sent the board, please wait";
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
    public OnRoomFilter(RoomService roomService, PlayerService playerService) {
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
     * Getting the request and validate the status.
     * If the status is valid, it continues, but if it isn't, the user will be navigated to another page.
     * If the player is not ready, and the game is waiting for boards.
     * If a player is ready and the game is waiting for boards, that means that he tries to get to the initial room again
     * when he needs to be in the waiting room, so it returns false and redirect to the relevant status.
     * If there is a critical error, the user navigated to /lobby/error-message.
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return
     * @throws Exception
     */
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
     * @throws Exception not thrown
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

    }
}
