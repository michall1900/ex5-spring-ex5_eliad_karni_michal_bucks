package hac.controllers;

import hac.classes.GameBoard;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequestMapping("/game/init")
public class GameInit {
    @GetMapping("")
    public String gameInit(Model model){
        model.addAttribute("endValue", GameBoard.SIZE-1);
        model.addAttribute("imgPath", GameBoard.imgType.get("empty"));
        //we will get the option from the db.
        model.addAttribute("option", GameBoard.options.get(0));
        model.addAttribute("url","/game/init");
        return "game/initGame";
    }

    @PostMapping("")
    public String postBoard(@Valid GameBoard gameBoard, Model model, Principal principal){
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
