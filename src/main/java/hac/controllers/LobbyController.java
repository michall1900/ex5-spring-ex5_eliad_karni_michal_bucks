package hac.controllers;

import hac.classes.Lobby.Lobby;
import hac.classes.Lobby.Room;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lobby")
public class LobbyController {

    @Resource(name="getSingletonLobby")
    private Lobby lobby;

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);


    @GetMapping("")
    public String login(Model model) {
        model.addAttribute("mode", "login");
        return "lobby/lobby";
    }

    @GetMapping("/create-room")
    public String getRoomCreation(){
        return "/lobby/roomCreation";
    }

    @PostMapping("/create-room")
    public String addRoom(Room room){
        lobby.addRoom(room);
        return "/index";
    }

    @PostMapping("/print-rooms")
    public String printRooms(){
        lobby.printRooms();
        return "/index";
    }

}
