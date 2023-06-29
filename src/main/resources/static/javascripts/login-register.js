(function (){
    const DISTINCT_PASSWORD_ERR = "The two passwords that you have been entered are distinct."

    /**
     * This function is validating the form before submit.
     * It checks if the two passwords are the same.
     * If they aren't, an error message is displayed. otherwise, the function submits again the form.
     * @param event - event
     */
    function validatePasswordForm(event) {
        event.preventDefault()
        let password1 = document.getElementById("password").value
        let password2 = document.getElementById("confirm-password").value
        if (password1 === password2)
            event.target.submit()
        else{
            document.getElementById("error-message").innerHTML = `${DISTINCT_PASSWORD_ERR}`
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