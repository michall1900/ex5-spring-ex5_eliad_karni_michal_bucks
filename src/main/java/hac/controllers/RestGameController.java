package hac.controllers;

import hac.classes.customErrors.GameOver;
import hac.classes.customErrors.InvalidChoiceError;
import hac.classes.forGame.UserTurn;
import hac.services.RoomService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;


import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 *  The controller manages the rests related to the game.
 * Some important things about the returned error messages/ statuses:
 * status ok: If game over, the answer is also ok and including in the body the path to game finish page.
 * More relevant status codes:
 * 504 error tells the user to send again request if the response is from long polling.
 * 400 tells the user that there was an error, but he can try to send the request again and fix the error at
 * his side.
 * Any other type of error statuses telling the user to redirect to /lobby/error-message because
 * There was a critical error while long polling (like some players left the room).
 */
@RestController
@RequestMapping("/game")
public class RestGameController {

    /**To get from him data and do in it some functionality*/
    @Autowired
    private RoomService roomService;

    /**
     * A route that is doing a long polling. It handles with waiting for game to start requests.
     * If everything is ok, it returns response status ok. The body should be empty.
     * @param principal To extract username
     * @return DeferredResult with the relevant response entity as described before.
     * @throws RuntimeException when there are problems/ game over.
     */
    @GetMapping( "/wait-to-start")
    public DeferredResult<ResponseEntity<?>> getRoomStatus(Principal principal) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(5000L);
        return roomService.handleStatusRoomPolling(principal,output);
    }
    /**
     * A route that handles with waiting new updates.
     * If everything is ok, it returns response status ok. The body should be empty.
     * The object that needs to send here needs to look like that:
     * {row:___, col:___ , opponentName:___}
     * @param principal To extract username
     * @return ResponseEntity with the relevant response as described before.
     * @throws RuntimeException when there are problems/ game over.
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateBoard(@RequestBody UserTurn userTurn, Principal principal){
        roomService.setUpdates(principal.getName(),userTurn);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * A route that is doing a long polling. It handles with waiting for updates to start requests.
     * Needs to receive in the path a value with the user's last index received from the updates' array.
     * In success, a json returns, looking like that:
     * [{attackDetails:{attackerName:___, opponentName:___, row:___, col:___},
     * boardChanges:[{row:___, col:___, status:"Hit"/"Miss"}, {..}, ...]}, ...]
     * Rows and columns need to be 0 &lt;= integer &lt; Board.Size.
     * attackerName is the username of the attacker, opponentName is the attacked player's username.
     * @param principal To extract username
     * @return DeferredResult with the relevant response entity as described before.
     * @throws RuntimeException when there are problems/ game over.
     */
    @GetMapping("/update/{timestamp}")
    public DeferredResult<ResponseEntity<?>> test(@PathVariable("timestamp") int timestamp, Principal principal) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(5000L);
        return roomService.handleUpdatePolling(principal, output, timestamp);
    }

    /**
     * Exception handler that handles with db errors
     * @param ex - The exception.
     * @return json with the error messages.
     */
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
     * @return json with the error messages.
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
    /**
     * Exception handler to handle with invalid user's choice (like problems with update)
     * @param e the exception.
     * @return ResponseEntity with status 400 and a body with the error message.
     */
    @ExceptionHandler(InvalidChoiceError.class)
    public ResponseEntity<String> handleAllExceptions(InvalidChoiceError e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    /**
     * Exception handler that handles with game over state. It tells the user to redirect to finish-page.
     * @param e The error message (This is not a real error)
     * @return response entity with status ok and body with the path to finish game.
     */
    @ExceptionHandler(GameOver.class)
    public ResponseEntity<String> handleAllExceptions(GameOver e) {
        return ResponseEntity.status(HttpStatus.OK).body("/game/finish-page");
    }

    /**
     * Exception handler that handles with all the exceptions in pages. It return error 500 tell the user to return
     * /lobby/error-message path.
     * @param e The exception
     * @return Status 500 with the error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

}
