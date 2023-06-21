package hac.controllers;

import java.security.Principal;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hac.classes.GameBoard;
import hac.repo.room.Room;
import hac.repo.room.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lobby")
public class LobbyController {

    @Autowired
    private RoomRepository roomRepo;

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
        // before creating player validate that player is not inside any room. There is query for that.
        System.out.println("Current logged user details: " + principal.getName());
        System.out.println(type);
        Room room = new Room();
        //validate type
        room.setOption((GameBoard.Options.values()[type]));
        roomRepo.save(room);
        List<Room> rooms = roomRepo.findAll();
        for (Room r : rooms) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String roomJson = objectMapper.writeValueAsString(r);

            System.out.println(roomJson);
            }
            catch (JsonProcessingException e){
                System.out.println(e);
            }
        }
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
