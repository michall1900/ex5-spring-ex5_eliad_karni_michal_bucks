package hac.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hac.repo.board.Board;
import hac.services.BoardService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@Controller
@RequestMapping("/game/init")
public class GameInit {

    @Autowired
    BoardService boardService;

    @Autowired
    private Validator validator;
    @GetMapping("")
    public String gameInit(Model model){
        model.addAttribute("endValue", Board.SIZE-1);
        model.addAttribute("imgPath", Board.imgType.get("empty"));
        //we will get the option from the db.
        model.addAttribute("option", Board.options.get(0));
        model.addAttribute("url","/game/init");
        return "game/initGame";
    }

    //TODO handle board error
    @PostMapping("")
    public String postBoard(@RequestParam("boardName") String boardString, Model model, Principal principal){
        System.out.println("Recieved!");
        System.out.println(boardString);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Board board = objectMapper.readValue(boardString, Board.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(board, "board");
            Set<ConstraintViolation<Board>> violations = validator.validate(board);
            if (!violations.isEmpty()) {
                // Handle the validation errors.
                // This is just an example; replace with your own error handling.
                throw new ConstraintViolationException(violations);
            }
            boardService.saveNewBoard(board, principal.getName());
            return "redirect:/game";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/game/init";
        }
//        try {
//            boardService.saveNewBoard(board, principal.getName());
//            return "redirect:/game";
//        }
//        catch (Exception e){
//            System.out.println(e);
//            //TODO = Add the error message;
//            return "redirect:/game/init";
//        }

    }



}
