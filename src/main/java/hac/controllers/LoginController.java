package hac.controllers;

import hac.classes.user.NewUser;
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

/**
 * The login controller
 */
@Controller
public class LoginController {
    /**
     * The encoder of the received password.
     */
    @Autowired
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * The users management singleton.
     */
    @Autowired
    @Qualifier("userDetailsService")
    private InMemoryUserDetailsManager usersManager;

    /**
     * The rest returns the login page.
     * @param model To add parameters to the thymeleaf.
     * @return returns the login page.
     */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("mode", "login");
        return "loginPages/login-register";
    }

    /**
     * The rest returns a registration page.
     * @param errorMessage The error message needed to get displayed
     * @param model To add parameters to the thymeleaf.
     * @return the register page html with error message.
     */
    @RequestMapping("/register/{errorMessage}")
    public String getRegisterErrorMessage(@RequestParam("errorMessage") String errorMessage, Model model) {
        model.addAttribute("mode", "register");
        model.addAttribute("errorMessage", errorMessage);
        return "loginPages/login-register";
    }

    /**
     * The rest returns a registration page.
     * @param model To add parameters to the thymeleaf.
     * @return the register page html.
     */
    @RequestMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("mode", "register");
        return "loginPages/login-register";
    }

    /**
     * The rest perform a registration action.
     * In case of success the user is redirected to the login page.
     * In case of failure, the user get back to the registration page with an informative error message.
     * @param user The new user registration parameters.
     * @param model To add parameters to the thymeleaf.
     * @return In case the registration succeed, the function returns to the
     */
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

    /**
     * The rest returns an error page of 403 code.
     * @return 403 error page html.
     */
    @RequestMapping("/403")
    public String forbidden() {
        return "loginPages/403";
    }

    /**
     * In case of exception in the login actions, the user will be sent to this page.
     * @param ex The thrown exception
     * @param model To add parameters to the thymeleaf.
     * @return An informative error page.
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {
        String errorMessage = (ex != null ? ex.getMessage() : "Unknown error");

        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }
}