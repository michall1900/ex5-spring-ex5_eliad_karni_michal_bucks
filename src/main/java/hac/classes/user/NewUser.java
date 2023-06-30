package hac.classes.user;

import jakarta.validation.constraints.NotEmpty;
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

    public String validate(){
        if(this.username.length() < 6 || this.username.length() > 30)
            return "Username length must be between 6-30 characters";
        if(this.username.contains("\\s"))
            return "Username cannot contain white spaces";
        if(this.password.contains("\\s"))
            return "Password cannot contain white spaces.";
        if(!this.password.equals(this.confirmPassword))
            return "The passwords are not equals.";
        if(this.password.length() > 30 || this.password.length() < 6)
            return "Password length must be between 6-30 characters";
        return "Valid";
    }
}