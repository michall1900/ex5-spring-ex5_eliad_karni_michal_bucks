package hac.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hac.repo.board.Board;
import hac.repo.tile.Tile;
import hac.services.BoardService;
import hac.services.PlayerService;
import hac.services.RoomService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Set;

@Controller
@RequestMapping("/game/init")
public class GameInitController {

    @Autowired
    BoardService boardService;

    @Autowired
    private Validator validator;

    @Autowired
    private RoomService roomService;

    @Autowired
    private PlayerService playerService;


    @GetMapping("")
    public String gameInit(Model model, Principal principal){
        try{

        }
        catch(Exception e){

            System.out.println(e);
        }

        try{
            model.addAttribute("names", roomService.getAllOpponentNamesByUsername(principal.getName()));
            model.addAttribute("endValue", Board.SIZE-1);
            model.addAttribute("imgPath", Board.imgType.get(String.valueOf(Tile.TileStatus.Empty)));
            model.addAttribute("option", Board.options.get(roomService.getBoardOptionByUsername(principal.getName()).ordinal()));
            model.addAttribute("url","/game/init");
        }
        catch(Exception e){
            model.addAttribute("error",e.getMessage());
            System.out.println(e.getMessage());
        }


        return "game/initGame";
    }

    //TODO handle board error
    @PostMapping("")
    public String postBoard(@RequestParam("boardName") String boardString, Model model, Principal principal){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Board board = objectMapper.readValue(boardString, Board.class);
//            Set<ConstraintViolation<Board>> violations = validator.validate(board);
//            if (!violations.isEmpty()) {
//                throw new ConstraintViolationException(violations);
//            }
            boardService.saveNewBoard(board, principal.getName());
            roomService.updateRoomStatusByUsername(principal.getName());
            return "game/waitingForStartGame";

        } catch (Exception e) {
            //TODO handle different with db error and
            model.addAttribute("error",e.getMessage());
            return "/game/initGame";
        }

    }



}
