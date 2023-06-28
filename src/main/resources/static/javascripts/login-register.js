(function (){
    const DISTINCT_PASSWORD_ERR = "The two passwords that you have been entered are distinct."

    /**
     * This function is validate the form before submit. It checks if the two passwords are the same. If they aren't,
     * error message is display. otherwise, the function submit again the form.
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