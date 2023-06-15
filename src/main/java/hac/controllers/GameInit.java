package hac.controllers;

import hac.classes.GameBoard;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/game/init")
public class GameInit {
    @GetMapping("")
    public String gameInit(Model model){
        model.addAttribute("endValue", GameBoard.SIZE-1);
        model.addAttribute("imgPath", GameBoard.imgType.get("empty"));
        //we will get the option from the db.
        model.addAttribute("option", GameBoard.options.get(1));
        return "game/initGame";
    }
}
