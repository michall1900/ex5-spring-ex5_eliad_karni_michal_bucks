package hac.controllers;

import hac.repo.board.Board;
import hac.repo.player.Player;
import hac.repo.room.Room;
import hac.services.BoardService;
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

import java.security.Principal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/game")
public class Game {
    @Autowired
    private RoomService roomService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private BoardService boardService;

    final private ExecutorService executor = Executors.newSingleThreadExecutor();

    @GetMapping("/wait-to-start-page")
    public String getWaitToStartPage(){
        return "game/waitingForStartGame";
    }

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

    @GetMapping("/on-game")
    public String onGamePage(Model model, Principal principal){
        //set opponent turn if needed.
        model.addAttribute("opponentBoards", boardService.getOpponentBoardsByUsername(principal.getName()));
        model.addAttribute("myBoard", boardService.getUserBoardByUsername(principal.getName()));
        return "game/game";
    }

    @GetMapping("/finish-page")
    public String finishGame(Model model, Principal principal){
        Player player = playerService.getPlayerByUsername(principal.getName(), true);
        if(player.getStatus() == Player.PlayerStatus.WIN) {
            model.addAttribute("status", "WIN");
        }
        else if(player.getStatus() == Player.PlayerStatus.LOSE){
            Room room = playerService.getRoomByUsername(principal.getName());
            for(Player checkedPlayer : room.getPlayers()){
                if (checkedPlayer.getStatus() == Player.PlayerStatus.WIN) {
                    model.addAttribute("winner", checkedPlayer.getUsername());
                    break;
                }
            }
            model.addAttribute("status", "LOSE");
        }
        else
            return "redirect: /lobby";
        model.addAttribute("status", "LOSE");
        model.addAttribute("winner", "Eliad");
        return "game/finishGame";
    }
}
