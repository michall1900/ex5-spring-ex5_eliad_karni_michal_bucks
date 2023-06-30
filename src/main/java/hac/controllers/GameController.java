package hac.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hac.classes.customErrors.GameOver;
import hac.classes.customErrors.InvalidChoiceError;
import hac.repo.board.Board;
import hac.services.BoardService;
import hac.services.PlayerService;
import hac.services.RoomService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

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
//        try {
            roomService.setOnGameModel(model, principal.getName());
//        }
//        //TODO handle errors
//        catch (Exception e){
//            System.out.println(e.getMessage());
//        }
        return "game/game";
    }
    @GetMapping("/init")
    public String gameInit(Model model, Principal principal){
//        //TODO handle error
//        try{
        roomService.setGameInitModel(model, principal.getName());
//        }
//        catch(Exception e){
//            model.addAttribute("error",e.getMessage());
//            System.out.println(e.getMessage());
//        }


        return "game/initGame";
    }

    //TODO handle board error
    @PostMapping("/init")
    public String postBoard(@RequestParam("boardName") String boardString, Model model, Principal principal){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Board board = objectMapper.readValue(boardString, Board.class);
            roomService.saveNewBoard(board, principal.getName());
            return "game/waitingForStartGame";

        } catch (JsonProcessingException  e) {
            model.addAttribute("error",e.getMessage());
            return "/game/initGame";
        }

    }
    @GetMapping("/update")
    public String getForDeniedAccess(){
        return "redirect:/game/on-game";
    }
    @GetMapping("/finish-page")
    public String finishGame(Model model, Principal principal){
        //TODO catch errors.
        if(playerService.setWinnersInModelReturnIfNotFound(principal.getName(), model))
            return "game/finishGame";
        else
            return "redirect:/lobby";
//        model.addAttribute("status", "LOSE");
//        model.addAttribute("winner", "Eliad");

    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        Map<String, String> errorsMap = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errorsMap.put(fieldName, errorMessage);
//        });
//
//        return errorsMap;
//    }
//    /**
//     * Handle with ConstraintViolationException exceptions.
//     * @param ex The exception
//     * @return Map&lt;String, String&gt; with all the errors.
//     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public String handleValidationExceptions(ConstraintViolationException ex, Model model) {
        StringBuilder errorMessage = new StringBuilder();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        constraintViolations.forEach((violation) -> {
            errorMessage.append(violation.getPropertyPath().toString())
                    .append(" :")
                    .append(violation.getMessage())
                    .append(", ");

        });

        model.addAttribute("error",errorMessage);
        return "/game/initGame";
    }
    @ExceptionHandler(InvalidChoiceError.class)
    public String handleAllExceptions(InvalidChoiceError e, Model model) {
        model.addAttribute("error",e.getMessage());
        return "/game/initGame";
    }
    @ExceptionHandler(GameOver.class)
    public String handleAllExceptions(GameOver e) {
        return "redirect:/game/finish-page";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception e) {
        return "redirect:/lobby/error-message";
    }

}
