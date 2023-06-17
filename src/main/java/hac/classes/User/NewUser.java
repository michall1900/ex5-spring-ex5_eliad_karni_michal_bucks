package hac.classes.User;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class NewUser implements Serializable {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private String username;
    private String password;

    public NewUser(String username, String password) {
        this.username = username;
        this.password = passwordEncoder.encode(password);
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
}