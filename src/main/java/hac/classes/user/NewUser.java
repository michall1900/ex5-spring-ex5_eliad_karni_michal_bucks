package hac.classes.user;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * The Component is how the register page's form received.
 */
@Component
public class NewUser implements Serializable {
    /**
     * The username member.
     */
    @NotEmpty(message = "Username is mandatory")
    private String username;

    /**
     * The password member.
     */
    @NotEmpty(message = "Password is mandatory")
    private String password;

    /**
     * The confirmPassword member.
     */
    @NotEmpty(message = "Password is mandatory")
    private String confirmPassword;

    /**
     * ctor with all values.
     * @param username The username
     * @param password The password
     * @param confirmPassword the confirmation password.
     */
    public NewUser(String username, String password, String confirmPassword) {
        this.username = username;
        setPassword(password);
        setConfirmPassword(confirmPassword);
    }

    /**
     * The default Ctor
     */
    public NewUser() {
    }

    /**
     * The username member getter.
     * @return The username member value.
     */
    public String getUsername() {
        return username;
    }

    /**
     * The username member setter.
     * @param username The new username value.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * The password member getter.
     * @return The password member value.
     */
    public String getPassword() {
        return password;
    }

    /**
     * The password member setter.
     * @param password The new password value.
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * The confirmPassword member getter.
     * @return The ConfirmPassword member value.
     */
    public String getConfirmPassword() {return confirmPassword;}

    /**
     * The confirmPassword member setter.
     * @param confirmPassword The new confirmPassword value.
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * The method validates that the received user members' values are valid, so it's fine to add the user to the database.
     * @return If the user is valid, so it's fine to add the user and if it isn't, the reason why not.
     */
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