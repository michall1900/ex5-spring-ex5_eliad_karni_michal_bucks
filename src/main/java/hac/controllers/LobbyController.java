package hac.controllers;

import java.security.Principal;
import java.util.*;

import hac.repo.board.Board;
import hac.repo.player.Player;

import hac.repo.room.Room;

import hac.services.PlayerService;
import hac.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/lobby")
public class LobbyController {
    /**
     * The member service acts like an api to the rooms DB.
     */
    @Autowired
    private RoomService roomService;
    /**
     * The member service acts like an api to the players DB.
     */
    @Autowired
    private PlayerService playerService;

    /**
     * The rest returns the lobby home page.
     * @return The lobby page html.
     */
    @GetMapping("")
    public String getLobby() {
        return "lobby/lobby";
    }

    /**
     * The rest returns a lobby page with The room been closed error message.
     * @param model To add the parameters to the thymeleaf page generation.
     * @return The lobby Html with the error message.
     */
    @GetMapping("/error-message")
    public String getErrorLobby(Model model) {
        model.addAttribute("errorMessage", "The room been closed");
        return "lobby/lobby";
    }

    /**
     * The rest return a JSON that contains all the rooms in the database which their status is: WAITING_FOR_NEW_PLAYER.
     * @return All the rooms in the database which their status is: WAITING_FOR_NEW_PLAYER.
     */
    @GetMapping("/getRooms")
    public @ResponseBody List<Map<String,String>> getRooms() {
        List<Map<String, String>> ans = new ArrayList<>();
        for(Room room : roomService.getAllRooms()){
            if (room.getStatus() == Room.RoomEnum.WAITING_FOR_NEW_PLAYER)
                ans.add(room.getInfo());
        }
        return ans;
    }

    /**
     * The function returns a list of all the players in the room, if the room is full, an informative message added to
     * the JSON.
     * @param model To add parameters to the thymeleaf.
     * @param principal To get info about the user.
     * @return A list of the players in the room, and if the room is full.
     */
    @GetMapping("/getRoom")
    public @ResponseBody Map<String,String> getRoom(Model model, Principal principal) {
        try {
            Room room = playerService.getRoomByUsername(principal.getName(), true);
            Map<String, String> ans = room.getInfo();
            if(room.full()){
                ans.put("start_game", "True");
            }
            return ans;
        }catch (Exception e){
            return new HashMap<String, String>();
        }
    }

    /**
     * The rest return a create room page.
     * @param model To add parameters to the thymeleaf.
     * @return create room page's html.
     */
    @GetMapping("/create-room")
    public String getRoomCreation(Model model) {
        //System.out.println(Board.options.get(Board.Options.BASIC.ordinal()));
        model.addAttribute("option1", Board.options.get(Board.Options.BASIC.ordinal()));
        model.addAttribute("option2", Board.options.get(Board.Options.ALTERNATIVE.ordinal()));
        return "/lobby/roomCreation";
    }

    /**
     * The function creates a new room receiving a post with the new room type.
     * @param type The new room's type.
     * @param principal For info about the user.
     * @param model To add parameters to the thymeleaf.
     * @return redirection to the wait room or the roomCreation html in case of errors.
     */
    @PostMapping("/create-room")
    public String addRoom(@RequestParam("type") int type, Principal principal, Model model){
        try {
            Player player = playerService.createNewPlayer(principal.getName());
            roomService.createNewRoom(player, type);
            return "redirect:/lobby/wait";
        } catch (Exception e){
            model.addAttribute("errorMessage", "Failed to create the room");
            return "/lobby/roomCreation";
        }
    }

    /**
     * The rest returns the waiting room html.
     * @param model To add parameters to the thymeleaf.
     * @return The waiting room html.
     */
    @GetMapping("/wait")
    public String wait(Model model) {
        return "/lobby/waitingRoom";
    }

    /**
     * The function adds the sender to the received id's room.
     * @param id The room the user wanted to be added to.
     * @param model To add parameters to the thymeleaf.
     * @param principal For info about the user.
     * @return HTML to the waiting room. redirect to the lobby if the addition failed.
     */
    @GetMapping("/enter-room/{id}")
    public String enterRoom(@PathVariable("id")  long id, Model model, Principal principal) {
        try{
            Player player = playerService.createNewPlayer(principal.getName());
            roomService.addPlayerToRoom(id, player);
            return "/lobby/waitingRoom";
        }catch (Exception e){
            model.addAttribute("errorMessage", "failed to enter the room");
            return "redirect: /lobby";
        }
    }
}
