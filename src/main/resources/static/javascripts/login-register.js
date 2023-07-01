(function (){
    /**
     * This function is validating the form before submit.
     * If the form isn't valid, an error message is displayed. otherwise, the function submits the form.
     * @param event - event
     */
    function validatePasswordForm(event) {
        event.preventDefault()
        let username = document.getElementById("username").value;
        let password = document.getElementById("password").value;
        let confirmPassword = document.getElementById("confirm-password").value;

        let errorMessage = "";

        if (username.length < 6 || username.length > 30) {
            errorMessage = "Username length must be between 6-30 characters";
        }
        else if (/\s/.test(username)) {
            errorMessage = "Username cannot contain white spaces";
        }
        else if (/\s/.test(password)) {
            errorMessage = "Password cannot contain white spaces.";
        }
        else if (password !== confirmPassword) {
            errorMessage = "The passwords are not equal.";
        }
        else if (password.length > 30 || password.length < 6) {
            errorMessage = "Password length must be between 6-30 characters";
        }
        else{
            event.target.submit();
        }
        if(errorMessage !== ""){
            document.getElementById("error-message").innerHTML = errorMessage
        }
    }

    // listeners setting
    document.addEventListener("DOMContentLoaded", (e)=>{
        let registerForm = document.getElementById("register-form");
        if(registerForm){
            registerForm.addEventListener("submit", validatePasswordForm);
        }
    })
})();