package hac.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hac.repo.board.Board;
import hac.services.BoardService;
import hac.services.PlayerService;
import hac.services.RoomService;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/game")
public class GameController {

    @Autowired
    BoardService boardService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private PlayerService playerService;

    @GetMapping("/wait-to-start-page")
    public String getWaitToStartPage(){
        return "game/waitingForStartGame";
    }

    @GetMapping("/on-game")
    public String onGamePage(Model model, Principal principal){
        //set opponent turn if needed.
        try {
            roomService.setOnGameModel(model, principal.getName());
        }
        //TODO handle errors
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "game/game";
    }
    @GetMapping("/init")
    public String gameInit(Model model, Principal principal){
        //TODO handle error
        try{
            roomService.setGameInitModel(model, principal.getName());
        }
        catch(Exception e){
            model.addAttribute("error",e.getMessage());
            System.out.println(e.getMessage());
        }


        return "game/initGame";
    }

    //TODO handle board error
    @PostMapping("/init")
    public String postBoard(@RequestParam("boardName") String boardString, Model model, Principal principal){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Board board = objectMapper.readValue(boardString, Board.class);
//            Set<ConstraintViolation<Board>> violations = validator.validate(board);
//            if (!violations.isEmpty()) {
//                throw new ConstraintViolationException(violations);
//            }

            roomService.saveNewBoard(board, principal.getName());

            return "game/waitingForStartGame";

        } catch (Exception e) {
            //TODO handle different with db error and
            model.addAttribute("error",e.getMessage());
            return "/game/initGame";
        }

    }
    @GetMapping("/finish-page")
    public String finishGame(Model model, Principal principal){
        //TODO catch errors.
        if(playerService.setWinnersInModelReturnIfNotFound(principal.getName(), model))
            return "game/finishGame";
        else
            return "redirect: /lobby";
//        model.addAttribute("status", "LOSE");
//        model.addAttribute("winner", "Eliad");

    }
}
