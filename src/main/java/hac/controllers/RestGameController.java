package hac.controllers;

import hac.classes.customErrors.GameOver;
import hac.classes.customErrors.InvalidChoiceError;
import hac.classes.forGame.UserTurn;
import hac.services.BoardService;
import hac.services.PlayerService;
import hac.services.RoomService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


//TODO add error handler
@RestController
@RequestMapping("/game")
public class RestGameController {

    @Autowired
    private RoomService roomService;


    @Autowired
    private PlayerService playerService;

    @GetMapping( "/wait-to-start")
    public DeferredResult<ResponseEntity<?>> getRoomStatus(Principal principal) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(5000L);
        return roomService.handleStatusRoomPolling(principal,output);
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateBoard(@RequestBody UserTurn userTurn, Principal principal){
        //try{
        roomService.setUpdates(principal.getName(),userTurn);
        return new ResponseEntity<>(HttpStatus.OK);
        //}
//        catch (InvalidChoiceError e){
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//        catch (GameOver e){
//            return new ResponseEntity<>("/game/finish-page", HttpStatus.OK);
//        }
//        catch (Exception e){
//            //TODO create this page.
//            return new ResponseEntity<>("/room-error", HttpStatus.INTERNAL_SERVER_ERROR);
//        }

    }
    @GetMapping("/update/{timestamp}")
    public DeferredResult<ResponseEntity<?>> test(@PathVariable("timestamp") int timestamp, Principal principal) {
        System.out.println("In getttttt");
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(5000L);
        return roomService.handleUpdatePolling(principal, output, timestamp);

    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errorsMap = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorsMap.put(fieldName, errorMessage);
        });

        return errorsMap;
    }
    /**
     * Handle with ConstraintViolationException exceptions.
     * @param ex The exception
     * @return Map&lt;String, String&gt; with all the errors.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleValidationExceptions(ConstraintViolationException ex) {
        Map<String, String> errorsMap = new HashMap<>();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        constraintViolations.forEach((violation) -> {
            String propertyName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errorsMap.put(propertyName, errorMessage);
        });

        return errorsMap;
    }
    @ExceptionHandler(InvalidChoiceError.class)
    public ResponseEntity<String> handleAllExceptions(InvalidChoiceError e) {
        System.out.println("IN INVALID CHOICE!!");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(GameOver.class)
    public ResponseEntity<String> handleAllExceptions(GameOver e) {
        System.out.println("IN GAME OVER!!!");
        return ResponseEntity.status(HttpStatus.OK).body("/game/finish-page");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e) {
        System.out.println("THROW INTERNAL SERVER ERROR!!!");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

}
