package hac.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
    @GetMapping("/room-error")
    public String getError(){
        return "/game/roomError";
    }
}
