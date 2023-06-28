package hac.classes.user;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class NewUser implements Serializable {


    @NotEmpty(message = "Username is mandatory")
    private String username;
    @NotEmpty(message = "Password is mandatory")
    private String password;

    @NotEmpty(message = "Password is mandatory")
    private String confirmPassword;

    public NewUser(String username, String password, String confirmPassword) {
        this.username = username;
        setPassword(password);
        setConfirmPassword(confirmPassword);
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
        this.password = password;
    }
    public String getConfirmPassword() {return confirmPassword;}

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Boolean isPasswordsEqual(){
        return this.password.equals(this.confirmPassword);
    }
}