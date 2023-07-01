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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Set;

/**
 * The Controller handles the rests related to the game management.
 */
@Controller
@RequestMapping("/game")
public class GameController {
    /**
     * The member service acts like an api to the boards DB.
     */
    @Autowired
    BoardService boardService;
    /**
     * The member service acts like an api to the rooms DB.
     */
    @Autowired
    private RoomService roomService;
    /**
     * The member service acts like an api to the players DB.
     */
    @Autowired
    private PlayerService playerService;

    /**
     * The page is a waiting page till all the players in the room submits their boards set.
     * @return Html for the waiting room html.
     */
    @GetMapping("/wait-to-start-page")
    public String getWaitToStartPage(){
        return "game/waitingForStartGame";
    }

    /**
     * This path return to the user the game in it's position.
     * @param model - used to add parameters
     * @param principal - To extract username
     * @return The game page's html.
     */
    @GetMapping("/on-game")
    public String onGamePage(Model model, Principal principal){
        roomService.setOnGameModel(model, principal.getName());
        return "game/game";
    }

    /**
     * This route sends the initGame html for user - the html that includes the register board status.
     * @param model - used to add parameters
     * @param principal - To extract username
     * @return initGame html.
     */
    @GetMapping("/init")
    public String gameInit(Model model, Principal principal){
        roomService.setGameInitModel(model, principal.getName());
        return "game/initGame";
    }

    /**
     * This route is receiving the board, check it, and if it is a valid board, it also return the user to waiting for
     * start page.
     * The board should be looking like that:
     * a json object as a string - {submarines: [{firstRow:___, firstCol:___, lastRow:___, lastCol:___, size:___}, {...},...]}
     * @param boardString looking like that: {submarines: [{firstRow:___, firstCol:___, lastRow:___, lastCol:___, size:___}, {...},...]}
     * @param model - used to add parameters
     * @param principal - To extract username
     * @return The init page when there is a problem with the received string or redirects to game/wait-to-start-page if
     * the board sent successfully.
     * @throws RuntimeException When the board is invalid. The runtime exception could be in many types and are handler
     *                          inside ExceptionHandlers.
     */
    @PostMapping("/init")
    public String postBoard(@RequestParam("boardName") String boardString, Model model, Principal principal){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Board board = objectMapper.readValue(boardString, Board.class);
            roomService.saveNewBoard(board, principal.getName());
            return "redirect:/game/wait-to-start-page";

        } catch (JsonProcessingException  e) {
            model.addAttribute("error",e.getMessage());
            return "/game/initGame";
        }
    }

    /**
     * Redirect to /game/on-game when just /update sent to the server to handle with this situation. /update is a post
     * route.
     * @return "redirect:/game/on-game"
     */
    @GetMapping("/update")
    public String getForDeniedAccess(){
        return "redirect:/game/on-game";
    }

    /**
     * Return the finish-page and display the results if the player found in the room. Otherwise, in failer, the user
     * redirects to /lobby/error-message.
     * @param model - used to add parameters
     * @param principal - To extract username
     * @return The finish page when user found in db or redirects to
     */
    @GetMapping("/finish-page")
    public String finishGame(Model model, Principal principal){
        if(playerService.setWinnersInModelReturnIfNotFound(principal.getName(), model))
            return "game/finishGame";
        else
            return "redirect:/lobby/error-message";

    }

    /**
     * Exception handler that handles with db errors
     * @param ex - The exception.
     * @param model - used to add parameters
     * @return gameInit page with the relevant errors.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            errorMessage.append(((FieldError) error).getField())
                    .append(" :")
                    .append(error.getDefaultMessage())
                    .append(", ");

        });
        model.addAttribute("error",errorMessage);
        return "/game/initGame";
    }
    /**
     * Handle with ConstraintViolationException exceptions.
     * @param ex The exception
     * @param model To add attributes to html.
     * @return gameInit page with the relevant errors.
     */
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

    /**
     * Exception handler to handle with invalid user's choice (like problems with board)
     * @param e the exception.
     * @param model - used to add parameters
     * @return gameInit page with the relevant errors.
     */
    @ExceptionHandler(InvalidChoiceError.class)
    public String handleAllExceptions(InvalidChoiceError e, Model model) {
        model.addAttribute("error",e.getMessage());
        return "/game/initGame";
    }

    /**
     * Exception handler that handles with game over state. It redirects the user to finish-page.
     * @param e The error message (This is not a real error)
     * @return redirects the user to finish-page.
     */
    @ExceptionHandler(GameOver.class)
    public String handleAllExceptions(GameOver e) {
        return "redirect:/game/finish-page";
    }

    /**
     * Exception handler that handles with all the exceptions in pages. It redirects the user to lobby with an error message.
     * @param e The exception
     * @return "redirect:/lobby/error-message"
     */
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception e) {
        return "redirect:/lobby/error-message";
    }

}