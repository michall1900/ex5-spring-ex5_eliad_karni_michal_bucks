package hac.classes.user;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class NewUser implements Serializable {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @NotEmpty(message = "Movie's title is mandatory")
    private String username;
    @NotEmpty(message = "Movie's title is mandatory")
    private String password;

    private String confirmpassword;

    public NewUser(String username, String password, String confirmpassword) {
        this.username = username;
        this.password = passwordEncoder.encode(password);
        this.confirmpassword = confirmpassword;
    }

    public NewUser() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = passwordEncoder.encode(password);
    }
    public String getConfirmpassword() {return confirmpassword;}

    public void setConfirmpassword(String confirmpassword) { this.confirmpassword = confirmpassword;}
}