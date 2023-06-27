(function(){
    const BUTTON_CLASS_NAME = ".boardBtn"
    const ERROR_BTN_ID = "errorBtn"
    const ERROR_BODY_ID = "error"
    const DEFAULT_ERROR = "There is a problem to connect to the server"

    let timestamp = 0;
    let isNeedToPoll = false;

    let ERROR_ELEMENT;
    let ERROR_BTN;

    const displayError = (errorMsg)=>{
        ERROR_ELEMENT.innerHTML = errorMsg
        ERROR_BTN.click();
    }

    const checkResponse = async (response) =>{
        if (!response.ok) {
            const err = await getErrorMessage(response);
            throw new Error(`Some error occurred ${response.status}. ${err}`);
        }
    }
    const getErrorMessage = async (response) =>{
        console.log(response);
        if (response.status !== 400){
            //TODO need to change to error page
            //window.location.href = "/lobby"
        }
        else{
            try {
                let text = await response.text();
                displayError(text);
            }
            catch{
                //TODO need to relocation to error page
                displayError(DEFAULT_ERROR);
            }
        }
    }

    const sendClickToServer  = async (buttonIdString, btn) =>{
        let [opponentName, row, col] = buttonIdString.split(".");
        row = +row
        col= +col
        console.log(opponentName, row, col)
        try {
            let response = await fetch("/game/update", {
                method: "Post",
                headers:{
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({"row": row, "col": col, "opponentName": opponentName})
            })
            await checkResponse(response);
        }
        catch(e){
            isNeedToPoll = false;
            btn.removeAttribute("disabled","");
            displayError(DEFAULT_ERROR);
            console.log(e);
        }


    }

    const handleBtnClick= (event, btn)=>{
        btn.setAttribute("disabled","");
        sendClickToServer(btn.id, btn);
    }

    document.addEventListener("DOMContentLoaded",()=>{
        //startLongPolling
        document.querySelectorAll(BUTTON_CLASS_NAME).forEach((button)=>{
            button.addEventListener("click", (event)=>{handleBtnClick(event,button)})
        })
        ERROR_ELEMENT = document.getElementById(ERROR_BODY_ID);
        ERROR_BTN = document.getElementById(ERROR_BTN_ID)
        if (ERROR_ELEMENT.value && ERROR_ELEMENT.value!=="")
            ERROR_BTN.click();
    })
})();