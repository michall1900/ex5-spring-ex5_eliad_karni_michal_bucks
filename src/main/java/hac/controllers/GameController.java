package hac.controllers;

import hac.classes.forGame.UserTurn;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO add error handler
@Controller
@RequestMapping("/game")
public class GameController {
    final private ExecutorService executor = Executors.newSingleThreadExecutor();

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
    @PostMapping("/update")
    public ResponseEntity<?> updateBoard(UserTurn userTurn){
        System.out.println("hereeeeeee");
        try{
            //roomService.setUpdates(principal.getName(),userTurn);
        }
        //TODO handle exception
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
