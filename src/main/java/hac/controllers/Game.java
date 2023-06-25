package hac.controllers;

import hac.repo.board.Board;
import hac.repo.player.Player;
import hac.repo.room.Room;
import hac.services.PlayerService;
import hac.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.net.http.HttpResponse;
import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/game")
public class Game {
    @Autowired
    private RoomService roomService;

    @Autowired
    private PlayerService playerService;

    final private ExecutorService executor = Executors.newSingleThreadExecutor();

    @ResponseBody
    @GetMapping("/wait-to-start")
    public DeferredResult<ResponseEntity<?>> getRoomStatus(Principal principal) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        executor.execute(() -> {
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
                output.setErrorResult(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service Unavailable"));
            } catch (Exception e) {
                output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
            }
        });

        return output;
    }

//    public ResponseEntity<?> waitForAllPlayers(Model model, Principal principal){
//        try {
//            DeferredResult<String> output = new DeferredResult<>(5*60*1000L);
//            output.onTimeout(()->
//                    output.setErrorResult(""));
//            Room.RoomEnum status = playerService.getRoomStatusByUserName(principal.getName());
//            while(status != Room.RoomEnum.ON_GAME){
//                try{
//                    Thread.sleep(1000L);
//                }
//                catch{
//
//                }
//            }
//            return "/game/waitingForStartGame";
//            //List<String> waitingToPlayerNames = roomService.getAllPlayersThatNotSentTheBoard(principal.getName());
//            //if (waitingToPlayerNames.isEmpty()){
//                //return "redirect:/game/waitingForStartGame";
//            //}
////            else {
////                model.addAttribute("names", waitingToPlayerNames);
////                return "game/waitingForStartGame";
////            }
//        }
//        catch(Exception e){
//            //TODO put an error - There is an error with the db, and delete what is needed.
//            return "redirect:/lobby";
//        }
//    }
    @GetMapping("/on-game")
    public String onGamePage(Model model, Principal principal){
        return "game/game";
    }
}
