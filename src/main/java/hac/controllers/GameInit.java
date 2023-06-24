package hac.controllers;

import hac.repo.board.Board;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/game/init")
public class GameInit {
    @GetMapping("")
    public String gameInit(Model model){
        model.addAttribute("endValue", Board.SIZE-1);
        model.addAttribute("imgPath", Board.imgType.get("empty"));
        //we will get the option from the db.
        model.addAttribute("option", Board.options.get(0));
        model.addAttribute("url","/game/init");
        return "game/initGame";
    }

    @PostMapping("")
    public String postBoard(@Valid Board gameBoard, Model model, Principal principal){
        System.out.println("Recieved!");
//        try{
//
//        }
//        catch (Exception e){
//
//            return "redirect:/game/init";
//        }

        return "redirect:/game/init";
    }



}
