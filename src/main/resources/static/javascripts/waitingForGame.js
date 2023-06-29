(function(){
    const TIME_OUT = 1000;
    const ERROR_BTN_ID = "errorBtn"
    const ERROR_BODY_ID = "error"
    const DEFAULT_ERROR = "There is a problem to connect to the server"
    const URL = "/game/wait-to-start"

    let ERROR_ELEMENT;
    let ERROR_BTN;

    const displayError = (errorMsg)=>{
        ERROR_ELEMENT.innerHTML = errorMsg
        ERROR_BTN.click();
    }

    async function waitForAllUsers(){
        try{
            let response = await fetch(URL);
            if (response.status === 504){
                //reconnect - waiting a lot of time
                await new Promise(resolve => setTimeout(resolve, TIME_OUT))
                await waitForAllUsers();
            }
            else if(response.status!== 200){
                try{
                    //TODO go to error page instead.
                    displayError(await response.text());
                }
                catch (e){
                    displayError(DEFAULT_ERROR);
                }

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
        }
    }
    document.addEventListener("DOMContentLoaded",()=>{
        ERROR_ELEMENT = document.getElementById(ERROR_BODY_ID);
        ERROR_BTN = document.getElementById(ERROR_BTN_ID)
        if (ERROR_ELEMENT.value && ERROR_ELEMENT.value!=="")
            ERROR_BTN.click();
        waitForAllUsers();
    })

})();