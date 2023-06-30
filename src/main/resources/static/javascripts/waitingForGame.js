(function(){
    const TIME_OUT = 1000;
    const ERROR_BTN_ID = "errorBtn"
    const ERROR_BODY_ID = "error"
    const DEFAULT_ERROR = "There is a problem to connect to the server"
    const URL = "/game/wait-to-start"
    const ERROR_PATH = "/lobby/error-message"
    const SPINNER_ELEMENT_ID = "spinner";
    let ERROR_ELEMENT;
    let ERROR_BTN;
    let SPINNER_ELEMENT;

    /**
     * A function that handle with error, it opens a modal and display the error message.
     * @param errorMsg The error that the function should display.
     */
    const displayError = (errorMsg)=>{
        ERROR_ELEMENT.innerHTML = errorMsg
        ERROR_BTN.click();
    }

    /**
     * This function is doing long polling.
     * It waits until the room will be ready.
     * When the room is ready, the function redirects the user to the game page or to the finish page if the game already finished.
     * In a case of error from server (telling something about the db), the function redirects user to error page.
     * @returns {Promise<void>}
     */
    async function waitForAllUsers(){
        try{
            let response = await fetch(URL);
            if (response.status === 504){
                //reconnect - waiting a lot of time
                await new Promise(resolve => setTimeout(resolve, TIME_OUT))
                await waitForAllUsers();
            }
            else if(response.status!== 200){
                window.location.href = ERROR_PATH

            }
            else{
                let data = await response.text();
                if (data){
                    window.location.href = data;
                }
                else{
                    await new Promise(resolve => setTimeout(resolve, TIME_OUT))
                    await waitForAllUsers();
                }

            }
        }
        catch (e){
            displayError(DEFAULT_ERROR);
            SPINNER_ELEMENT.classList.add("d-none");
        }
    }

    /**
     * Listeners to the program.
     */
    document.addEventListener("DOMContentLoaded",()=>{
        ERROR_ELEMENT = document.getElementById(ERROR_BODY_ID);
        ERROR_BTN = document.getElementById(ERROR_BTN_ID)
        if (ERROR_ELEMENT.value && ERROR_ELEMENT.value!=="")
            ERROR_BTN.click();
        waitForAllUsers();
        SPINNER_ELEMENT = document.getElementById(SPINNER_ELEMENT_ID);
    })

})();