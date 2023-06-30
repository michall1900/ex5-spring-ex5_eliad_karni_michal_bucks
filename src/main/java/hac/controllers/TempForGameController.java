package hac.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hac.classes.forGame.UserTurn;
import hac.repo.board.Board;
import hac.repo.board.BoardRepository;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.repo.room.RoomRepository;
import hac.repo.subamrine.Submarine;
import hac.repo.subamrine.SubmarineRepository;
import hac.repo.tile.Tile;
import hac.repo.tile.TileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Just a test to do some tests and also print all the data bases.
 */
@Controller
@RequestMapping("/game/test")
public class TempForGameController {
    /**The player repository*/
    @Autowired
    private PlayerRepository playerRepository;

    /**The room repository*/
    @Autowired
    private RoomRepository roomRepo;

    /**The board repository*/
    @Autowired
    private BoardRepository boardRepository;

    /**The tile repository*/
    @Autowired
    private TileRepository tileRepository;

    /**The submarine repository*/
    @Autowired
    private SubmarineRepository submarineRepository;

    /**
     * Prints all the db.
     * @return "redirect:/lobby"
     */
    @GetMapping("/print")
    public String print(){
        System.out.println("Rooms\n===================================================\n\n");
        for (Room room: roomRepo.findAll()){
            System.out.println(room);
        }
        System.out.println("\n\n===================================================\n\n");
        System.out.println("Players\n===================================================\n\n");
        for (Player player: playerRepository.findAll()){
            System.out.println(player);
        }
        System.out.println("\n\n===================================================\n\n");
        System.out.println("Boards\n===================================================\n\n");
        for (Board board: boardRepository.findAll()){
            System.out.println(board);
        }
        System.out.println("\n\n===================================================\n\n");
        System.out.println("Tiles\n===================================================\n\n");
        for (Tile tile: tileRepository.findAll()){
            System.out.println(tile);
        }
        System.out.println("\n\n===================================================\n\n");
        System.out.println("Submarines\n===================================================\n\n");
        for (Submarine submarine: submarineRepository.findAll()){
            System.out.println(submarine);
        }
        System.out.println("\n\n===================================================\n\n");
        return "redirect:/lobby";
    }
}
