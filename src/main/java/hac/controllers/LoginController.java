package hac.controllers;

import hac.classes.user.NewUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    @Qualifier("userDetailsService")
    private InMemoryUserDetailsManager usersManager;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("mode", "login");
        return "loginPages/login-register";
    }

    /** User zone index. */
    @RequestMapping("/user")
    public String userIndex() {
        return "user/index";
    }

    /** User zone index. */


    @RequestMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("mode", "register");
        return "loginPages/login-register";
    }

    @PostMapping("/register")
    public String registerUser(NewUser user){
        usersManager.createUser(User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build());
        return "redirect:/login";
    }
    /** Administration zone index.
     * Note that we can access current logged user just by adding the Principal
     * parameter
     */
    @RequestMapping("/admin")
    public String adminIndex(Principal principal) {
        System.out.println("Current logged user details: " + " (" + principal + ")" );
        return "admin/index";
    }

    /** Shared zone index. */
    @RequestMapping("/shared")
    public String sharedIndex() {
        // print current logged user details - withouth using the Principal parameter
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Current logged user details: "
                + ((UserDetails) principal).getUsername()
                + " (" + ((UserDetails) principal).getAuthorities() + ")");


        return "shared/index";
    }

    /** Simulation of an exception. */
    @RequestMapping("/simulateError")
    public void simulateError() {
        throw new RuntimeException("This is a simulated exception thrown by a controller");
    }

    /** Error page that displays all exceptions. */
    @RequestMapping("/errorpage")
    public String error(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
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