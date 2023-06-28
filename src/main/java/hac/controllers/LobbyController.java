package hac.controllers;

import java.security.Principal;
import java.util.*;

import hac.repo.board.Board;
import hac.repo.player.Player;

import hac.repo.room.Room;

import hac.services.PlayerService;
import hac.services.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/lobby")
public class LobbyController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private PlayerService playerService;

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("")
    public String getLobby() {
        return "lobby/lobby";
    }

    @GetMapping("/error-message")
    public String getErrorLobby(Model model) {
        model.addAttribute("errorMessage", "The room been closed");
        return "lobby/lobby";
    }

    @GetMapping("/getRooms")
    public @ResponseBody List<Map<String,String>> getRooms() {
        List<Map<String, String>> ans = new ArrayList<Map<String, String>>();
        for(Room room : roomService.getAllRooms()){
            ans.add(room.getInfo());
        }
        return ans;
    }

    @GetMapping("/getRoom")
    public @ResponseBody Map<String,String> getRoom(Model model, Principal principal) {
        try {
            Player player = playerService.getPlayerByUsername(principal.getName(), true);
            Room room = player.getRoom();
            Map<String, String> ans = room.getInfo();
            if(room.full()){
                ans.put("start_game", "True");
            }
            return ans;
        }catch (Exception e){
            return new HashMap<String, String>();
        }
    }

    @GetMapping("/create-room")
    public String getRoomCreation(Model model) {
        //System.out.println(Board.options.get(Board.Options.BASIC.ordinal()));
        model.addAttribute("option1", Board.options.get(Board.Options.BASIC.ordinal()));
        model.addAttribute("option2", Board.options.get(Board.Options.ALTERNATIVE.ordinal()));
        return "/lobby/roomCreation";
    }

    @PostMapping("/create-room")
    public String addRoom(@RequestParam("type") int type, Principal principal, Model model){
        //TODO validate type
        try {
            Player player = playerService.createNewPlayer(principal.getName());
            roomService.createNewRoom(player, type);
            return "redirect:/lobby/wait";
        } catch (Exception e){
            model.addAttribute("errorMessage", "Failed to create the room");
            return "/lobby/roomCreation";
        }
    }

    @GetMapping("/wait")
    public String wait(Model model) {
        return "/lobby/waitingRoom";
    }

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


    @PostMapping("/print-rooms")
    public String printRooms(){
        //lobby.printRooms();
        return "/index";
    }
}
