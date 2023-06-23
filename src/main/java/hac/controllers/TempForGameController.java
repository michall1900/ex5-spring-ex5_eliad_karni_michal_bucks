package hac.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import hac.classes.GameBoard;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.repo.room.RoomRepository;
import hac.repo.services.RoomService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Controller
@RequestMapping("/game")
public class TempForGameController {

    @Autowired
    private RoomService rm;

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private PlayerRepository playersRepo;

    @Resource(name="getRoomLock")
    ReentrantReadWriteLock roomLock;

    @Resource(name="getPlayerLock")
    ReentrantReadWriteLock playerLock;
    @GetMapping("")
    public String tempGame(Principal principal){
        System.out.println("in /game");
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setStatus(Player.PlayerStatus.NOT_READY);
        player2.setStatus(Player.PlayerStatus.NOT_READY);
        player1.setUsername("1");
        player2.setUsername("2");
        Room room = new Room();
        room.setStatus(Room.RoomEnum.WAITING_FOR_BOARDS);
        room.add(player1);
        room.add(player2);
//        room.setPlayers(new ArrayList<>() {{
//                add(player1);
//                add(player2);
//            }
//        });
//        player1.setRoom(room);
//        player2.setRoom(room);
        try {
            rm.add(room);
//            playersRepo.save(player1);
//            playersRepo.save(player2);
            List<Player> p = playersRepo.findAll();
            for (Player pl : p) {
                ObjectMapper objectMapper = new ObjectMapper();
                String pJson = objectMapper.writeValueAsString(pl);
                System.out.println(pJson);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return "redirect:/game/init";
    }
}
