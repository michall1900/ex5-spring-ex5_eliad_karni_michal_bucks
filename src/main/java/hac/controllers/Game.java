package hac.controllers;

import hac.repo.board.Board;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/game")
public class Game {
    @GetMapping("")
    public String gameInit(Model model){
        //TODO - display opponents in model
        return "game/waitingForStartGame";
    }
}
