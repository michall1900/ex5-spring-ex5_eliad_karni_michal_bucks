package hac.controllers;

import hac.repo.board.Board;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The controller handles the root requests.
 */
@Controller
public class DefaultController {
    /**
     * Home page.
     * @return The home page's html
     */
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    /**
     * The how to play page.
     * @param model Model that used for the
     * @return The how to play page's html.
     */
    @RequestMapping("/how-to-play")
    public String howToPlay(Model model) {
        model.addAttribute("option1", Board.options.get(Board.Options.BASIC.ordinal()));
        model.addAttribute("option2", Board.options.get(Board.Options.ALTERNATIVE.ordinal()));
        return "how-to-play";
    }
}
