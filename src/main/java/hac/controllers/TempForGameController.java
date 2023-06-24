package hac.controllers;

import hac.repo.player.Player;
import hac.repo.room.Room;
import hac.services.PlayerService;
import hac.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;


//TODO - delete this, it's a temporary route
@Controller
@RequestMapping("/game")
public class TempForGameController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private PlayerService playerService;
//    @Autowired
//    private RoomRepository roomRepo;
//
//    @Autowired
//    private PlayerRepository playersRepo;


    @GetMapping("")
    public String tempGame(Principal principal){
        try {
            /*
            System.out.println("in /game");
            Player player1 = playerService.createNewPlayer("1");
            Player player2 = playerService.createNewPlayer("2");
            Room room = roomService.createNewRoom();
            long roomId = roomService.saveRoom(room).getId();
            roomService.addPlayerToRoom(roomId,player1);
            roomService.addPlayerToRoom(roomId,player2);
            roomService.changeRoomStatus(roomId, Room.RoomEnum.WAITING_FOR_BOARDS);
             */
        }
        catch (Exception e){
            System.out.println(e);
        }
        return "redirect:/game/init";
    }
}
