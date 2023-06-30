package hac.controllers;

import hac.repo.board.Board;
import hac.repo.player.Player;
import hac.classes.customErrors.GameOver;
import hac.classes.customErrors.InvalidChoiceError;
import hac.classes.forGame.UserTurn;
import hac.embeddables.UpdateObject;
import hac.repo.room.Room;
import hac.services.BoardService;
import hac.services.PlayerService;
import hac.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


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
            roomService.setOnGameModel(model, principal.getName());
        }
        //TODO handle errors
        catch (Exception e){
            //TODO something with the exception.
        }
        return "game/game";
    }
    @GetMapping( "/wait-to-start")
    public DeferredResult<ResponseEntity<?>> getRoomStatus(Principal principal) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(5000L);
        return roomService.handleStatusRoomPolling(principal,output);
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
            return new ResponseEntity<>("/room-error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @GetMapping("/update/{timestamp}")
    @ResponseBody
    public DeferredResult<ResponseEntity<?>> test(@PathVariable("timestamp") int timestamp, Principal principal) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(5000L);
        return roomService.handleUpdatePolling(principal, output, timestamp);

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
