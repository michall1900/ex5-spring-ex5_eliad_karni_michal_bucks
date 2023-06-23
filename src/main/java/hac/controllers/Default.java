package hac.controllers;

import hac.classes.GameBoard;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** this is a test controller, delete/replace it when you start working on your project */
@Controller
public class Default {
    /** Home page. */
    @RequestMapping("/")
    public String index() {return "index";}
    @RequestMapping("/how-to-play")
    public String howToPlay(Model model) {
        model.addAttribute("option1", GameBoard.options.get(GameBoard.Options.TYPE1.ordinal()));
        model.addAttribute("option2", GameBoard.options.get(GameBoard.Options.TYPE2.ordinal()));
        return "how-to-play";
    }
}
