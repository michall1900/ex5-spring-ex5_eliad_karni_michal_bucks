package hac.controllers;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/lobby")
public class LobbyController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);


    @GetMapping("")
    public String getLobby() {
        return "lobby/lobby";
    }

    @GetMapping("/create-room")
    public String getRoomCreation(){
        return "/lobby/roomCreation";
    }

    @PostMapping("/create-room")
    public String addRoom(@RequestParam("type") int type, Principal principal){
        System.out.println("Current logged user details: " + principal.getName());
        System.out.println(type);
        //lock to check if the player is already inside a room
        //lobby.addRoom(room);
        return "/index";
    }

    @PostMapping("/print-rooms")
    public String printRooms(){
        //lobby.printRooms();
        return "/index";
    }

}
