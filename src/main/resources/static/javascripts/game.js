(function(){
    const TIME_OUT = 100;
    const BUTTON_CLASS_NAME = ".boardBtn"
    const ERROR_BTN_ID = "errorBtn"
    const ERROR_BODY_ID = "error"
    const DEFAULT_ERROR = "There is a problem to with server's response"
    const STILL_PROCESSING_ERROR = "Your last click is still not updated. Please wait until it will update."
    const NOT_YOUR_TURN = "It's not your turn"
    const TURN_ID = "turnOf"
    const LAST_STEP_ID = "lastStep";
    const URL_TO_UPDATE = "/game/update"
    const MY_NAME_ID = "name";
    const ERROR_PATH = "/lobby/error-message"
    const FINISH_PAGE = "/game/finish-page";
    const IMAGES_PATHS = new Map([["Miss","../images/noShip.png"],["Hit","../images/explodeShip.jpg"]])
    const BOARD_SIZE = 10
    let csrfToken
    let csrfHeader
    let timestamp = 0;
    let isNeedToPoll = true;
    let TURN_ELEMENT;
    let LAST_STEP_ELEMENT;
    let ERROR_ELEMENT;
    let ERROR_BTN;
    let MY_NAME;
    let isMyTurn;
    let isStillProcessing = false;
    let lastStep = {row:-1, col:-1, opponentName:""};


    const displayError = (errorMsg)=>{
        ERROR_ELEMENT.innerHTML = errorMsg
        ERROR_BTN.click();
    }

    const checkResponse = async (response) =>{
        if (!response.ok) {
            console.log(response.status);
            const err = await getErrorMessage(response);
            throw new Error(`Some error occurred ${response.status}. ${err}`);
        }
    }
    const getErrorMessage = async (response) =>{
        console.log(response);
        if (response.status !== 400){
            isNeedToPoll=false;
            window.location.href = ERROR_PATH
        }
        else{
            try {
                return await response.text();
            }
            catch{
                return DEFAULT_ERROR;
            }
        }
    }

    const sendClickToServer  = async (buttonIdString, btn) =>{
        let [opponentName, row, col] = buttonIdString.split(".");
        if (!isMyTurn || isStillProcessing) {
            displayError(isStillProcessing? STILL_PROCESSING_ERROR :NOT_YOUR_TURN)
            btn.removeAttribute("disabled","");
            return;
        }
        lastStep = {row:row, col:col, opponentName: opponentName};
        isStillProcessing = true;
        try {
            let response = await fetch(URL_TO_UPDATE, {
                method: "POST",
                headers:{
                    'Content-Type': 'application/json; charset=utf-8',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify(lastStep)

            })
            await checkResponse(response);
            let data = await response.text();
            if (!!data && data.startsWith("/")) {
                window.location.href = data
            }

        }
        catch(e){
            btn.removeAttribute("disabled","");
            displayError(e);
            isStillProcessing = false;
            console.log(e);
        }



    }

    const handleBtnClick= (event, btn)=>{
        btn.setAttribute("disabled","");
        sendClickToServer(btn.id, btn);
    }
     async function getUpdates (){

        if(isNeedToPoll) {
            try {
                let response = await fetch(`${URL_TO_UPDATE}/${timestamp}`,{
                    method:"GET",
                    headers:{
                        [csrfHeader]: csrfToken
                    }
                });
                console.log(response.status);
                if (response.status === 504) {
                    //reconnect - waiting a lot of time
                    await new Promise(resolve => setTimeout(resolve, TIME_OUT))
                    await getUpdates();
                }
                else if(response.status === 400){
                    try {
                        let res = await response.text();
                        displayError(res);
                    }
                    catch{
                        displayError(DEFAULT_ERROR);
                    }
                    await new Promise(resolve => setTimeout(resolve, TIME_OUT))
                    await getUpdates();
                }
                else if (response.status !== 200) {
                    isNeedToPoll=false;
                    window.location.href = ERROR_PATH
                } else {
                    let data = await response.text();
                    try{
                        let json  = JSON.parse(data);
                        try{
                            handleReceivedData(json);
                        }
                        catch{
                            displayError(DEFAULT_ERROR);
                            isStillProcessing = false;
                        }
                        await new Promise(resolve => setTimeout(resolve, TIME_OUT))
                        await getUpdates();
                    }
                    catch{
                        isNeedToPoll=false;
                        window.location.href = FINISH_PAGE
                    }
                }
            } catch (e) {
                displayError(DEFAULT_ERROR);
                isStillProcessing = false;
            }
        }
    }
    const handleReceivedData = (jsonData)=>{
        validateResponse(jsonData)
        timestamp += jsonData.length;
        let usernameTurn = jsonData[jsonData.length-1].attackDetails.nextTurn;
        isMyTurn = usernameTurn === MY_NAME;
        TURN_ELEMENT.innerHTML = (!isMyTurn)? `'${usernameTurn}'`: "Your";
        let attackDetailsObject = jsonData[jsonData.length-1].attackDetails;
        LAST_STEP_ELEMENT.innerHTML = `User '${attackDetailsObject.attackerName}' hit on 
            '${attackDetailsObject.opponentName}''s board in index row=${attackDetailsObject.row} col = ${attackDetailsObject.col}`;

        jsonData.forEach((change)=>{
            let boardChange = change.boardChanges;
            attackDetailsObject = change.attackDetails
            if (attackDetailsObject.row === lastStep.row && attackDetailsObject.col === lastStep.col
                && attackDetailsObject.attackerName === MY_NAME && attackDetailsObject.opponentName === lastStep.opponentName)
                isStillProcessing = false;
            let prefix = attackDetailsObject.opponentName;
            boardChange.forEach((tileChange)=>{
                let buttonElement = document.getElementById(`${prefix}.${tileChange.row}.${tileChange.col}`)
                if (!!buttonElement){
                    buttonElement.setAttribute("disabled","");
                }
                let imgElement = document.getElementById(`image_${prefix}.${tileChange.row}.${tileChange.col}`)
                if (!!imgElement){
                    imgElement.setAttribute("src", IMAGES_PATHS.get(tileChange.status));
                }

            })
        })

    }

    const validateResponse = (jsonData)=>{
        if (!Array.isArray(jsonData) || !jsonData.every((data)=>
            isValidAttackDetails(data.attackDetails) && isValidBoardChanges(data.boardChanges)
        ))
            throw new Error(DEFAULT_ERROR);
    }
    const isValidAttackDetails = (attackDetails)=>{
        return (!!attackDetails && !!attackDetails.attackerName && !!attackDetails.opponentName &&
            isStringIsAValidIndex(attackDetails.row) && isStringIsAValidIndex(attackDetails.col)
        )
    }

    const isValidBoardChanges = (boardChanges)=>{
        return (!!boardChanges  && Array.isArray(boardChanges) && boardChanges.every(tileChange=>
            isStringIsAValidIndex(tileChange.row) && isStringIsAValidIndex(tileChange.col)&&
            !!tileChange.status && [...IMAGES_PATHS.keys()].some((statusName)=> tileChange.status === statusName )
        ))
    }

    const isStringIsAValidIndex = (integerString)=>{
        return (!!integerString && !Number.isNaN(integerString) && Number.isInteger(+integerString) &&
                (+integerString)>=0 && (+integerString)< BOARD_SIZE)
    }

    document.addEventListener("DOMContentLoaded",()=>{
        getUpdates();
        document.querySelectorAll(BUTTON_CLASS_NAME).forEach((button)=>{
            button.addEventListener("click", (event)=>{handleBtnClick(event,button)})
        })
        ERROR_ELEMENT = document.getElementById(ERROR_BODY_ID);
        ERROR_BTN = document.getElementById(ERROR_BTN_ID)
        if (ERROR_ELEMENT.value && ERROR_ELEMENT.value!=="")
            ERROR_BTN.click();
        csrfToken = document.querySelector('meta[name="_csrf"]').content;
        csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        LAST_STEP_ELEMENT = document.getElementById(LAST_STEP_ID)
        TURN_ELEMENT = document.getElementById(TURN_ID)
        isMyTurn = (TURN_ELEMENT.innerText === "Your")

        const name = document.getElementById(MY_NAME_ID);
        console.log(name.innerText);
        MY_NAME = (!!name)? name.innerText : "";
    })
})();