package hac.controllers;

import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.services.PlayerService;
import hac.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;


//TODO - delete this, it's a temporary route
@Controller
@RequestMapping("/game/test")
public class TempForGameController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRepository playerRepository;
//    @Autowired
//    private RoomRepository roomRepo;
//
//    @Autowired
//    private PlayerRepository playersRepo;


    @GetMapping("")
    public String tempGame(Principal principal){
        try {

            System.out.println("in /game");
            Player player1 = playerService.createNewPlayer("1");
            Player player2 = playerService.createNewPlayer("2");
            System.out.println("Going to save player 1");
            Room room = roomService.createNewRoom(player1, 0);
            System.out.println("Going to save player 2");
            //long roomId = roomService.saveRoom(room).getId();
//            roomService.addPlayerToRoom(roomId,player1);
            roomService.addPlayerToRoom(room.getId(),player2);
            System.out.println("After saving both players, changing status");
            roomService.changeRoomStatus(room.getId(), Room.RoomEnum.WAITING_FOR_BOARDS);
            System.out.println("Finish with room creation");
        }
        catch (Exception e){
            System.out.println(e);
        }
        try{
            Player player = playerService.getPlayerByUsername("1", true);
        }
        catch (Exception e){
            System.out.println(e);
        }


        return "redirect:/game/init";
    }

    @GetMapping("/test")
    public String temp(){
//        try {
//            Player p = playerRepository.findByUsername("1");
//            p.setStatus(Player.PlayerStatus.ON_GAME);
//            playerRepository.save(p);
//        }
//        catch (Exception e){
//            System.out.println(e);
//        }
//        try{
//            Player player = playerService.getPlayerByUsername("1", true);
//            System.out.println(player.getInfo().toString());
//        }
//        catch (Exception e) {
//            System.out.println(e);
//        }
        try{
            playerService.removePlayer("1");
            //List<Room> rooms = roomService.getAllRooms();
        }
        catch (Exception e){
            System.out.println(e);
        }
        return "redirect:/game/init";
    }
}
