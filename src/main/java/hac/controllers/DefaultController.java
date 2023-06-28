package hac.controllers;

import hac.repo.board.Board;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/** this is a test controller, delete/replace it when you start working on your project */
@Controller
public class DefaultController {
    /** Home page. */
    @RequestMapping("/")
    public String index() {return "index";}
    @RequestMapping("/how-to-play")
    public String howToPlay(Model model) {
        //System.out.println(Board.options.get(Board.Options.BASIC.ordinal()));
        model.addAttribute("option1", Board.options.get(Board.Options.BASIC.ordinal()));
        model.addAttribute("option2", Board.options.get(Board.Options.ALTERNATIVE.ordinal()));
        return "how-to-play";
    }
}
