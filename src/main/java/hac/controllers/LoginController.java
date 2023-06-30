package hac.controllers;

import hac.classes.user.NewUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class LoginController {

    @Autowired
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    @Qualifier("userDetailsService")
    private InMemoryUserDetailsManager usersManager;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("mode", "login");
        return "loginPages/login-register";
    }
    @RequestMapping("/register/{errorMessage}")
    public String getRegisterErrorMessage(@RequestParam("errorMessage") String errorMessage, Model model) {
        model.addAttribute("mode", "register");
        model.addAttribute("errorMessage", errorMessage);
        return "loginPages/login-register";
    }

    @RequestMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("mode", "register");
        return "loginPages/login-register";
    }

    @PostMapping("/register")
    public String registerUser(NewUser user, Model model){
        try {
            String validation = user.validate();
            if(!validation.equals("Valid"))
                throw new Exception(validation);
            usersManager.createUser(User.withUsername(user.getUsername())
                    .password(passwordEncoder.encode(user.getPassword()))
                    .roles("USER")
                    .build());
            return "redirect:/login";
        }catch (Exception e){
            model.addAttribute("mode", "register");
            model.addAttribute("errorMessage", e.getMessage());
            return "loginPages/login-register";
        }
    }

    /** simple Error page. */
    @RequestMapping("/403")
    public String forbidden() {
        return "loginPages/403";
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {

        logger.error("Exception during execution of SpringSecurity application", ex);
        String errorMessage = (ex != null ? ex.getMessage() : "Unknown error");

        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }


}