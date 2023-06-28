package hac.controllers;

import hac.classes.customErrors.GameOver;
import hac.classes.customErrors.InvalidChoiceError;
import hac.classes.forGame.UserTurn;
import hac.embeddables.UpdateObject;
import hac.repo.room.Room;
import hac.services.BoardService;
import hac.services.PlayerService;
import hac.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

//TODO add error handler
@Controller
@RequestMapping("/game")
public class GameController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private BoardService boardService;

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
            model.addAttribute("turn", roomService.getPlayerUsernameTurn(principal.getName()));
            model.addAttribute("name", principal.getName());
            model.addAttribute("opponentBoards", boardService.getOpponentBoardsByUsername(principal.getName()));
            model.addAttribute("myBoard", boardService.getUserTwoDimensionalArrayBoardByUsername(principal.getName()));

        }
        //TODO handle errors
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "game/game";
    }
    @GetMapping( "/wait-to-start")
    public DeferredResult<ResponseEntity<?>> getRoomStatus(Principal principal) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(5000L);
        output.onTimeout(() -> output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Service Unavailable")));
        try {
            roomService.getExecutorServiceForRoom(principal.getName()).execute(() -> {
                try {
                    Room.RoomEnum status;
                    do {
                        status = playerService.getRoomStatusByUserName(principal.getName());
                        if (status != Room.RoomEnum.ON_GAME) {
                            Thread.sleep(1000);
                        }
                    } while (status != Room.RoomEnum.ON_GAME);
                    output.setResult(ResponseEntity.ok("/game/on-game"));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Service Unavailable"));
                } catch (Exception e) {
                    output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
                }
            });
        }
        catch (Exception e) {
            output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
        }

        return output;
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateBoard(@RequestBody UserTurn userTurn, Principal principal){
        //System.out.println("hereeeeeee");
        try{
            roomService.setUpdates(principal.getName(),userTurn);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (InvalidChoiceError e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (GameOver e){
            return new ResponseEntity<>("/game/finish-page", HttpStatus.OK);
        }
        catch (Exception e){
            //TODO create this page.
            return new ResponseEntity<>("/lobby/room-error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @GetMapping("/update/{timestamp}")
    @ResponseBody
    public DeferredResult<ResponseEntity<?>> test(@PathVariable("timestamp") int timestamp, Principal principal) {
        System.out.println("In getttttt");

        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(5000L);
        output.onTimeout(() -> output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Service Unavailable")));
        try {
            roomService.getExecutorServiceForRoom(principal.getName()).execute(() -> {
                try {
                    AtomicReference<List<UpdateObject>> updates = new AtomicReference<>();
                    do {
                        updates.set(roomService.getUpdates(principal.getName(), timestamp));
                        if (updates.get().isEmpty()) {
                            Thread.sleep(1000);
                        }
                    } while (updates.get().isEmpty());
                    output.setResult(ResponseEntity.ok(updates.get()));
                }
                catch (InvalidChoiceError e){
                    output.setErrorResult(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()));
                }
                catch (GameOver e){
                    output.setResult(ResponseEntity.status(HttpStatus.OK).body("/game/finish-page"));
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Service Unavailable"));
                }
                catch (Exception e) {
                    output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
                }
            });
        } catch (Exception e) {
            output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
        }
        return output;
    }
}
