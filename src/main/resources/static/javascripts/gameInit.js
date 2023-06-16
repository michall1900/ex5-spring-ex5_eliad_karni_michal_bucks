(function(){
    const DEFAULT_VALUE = -1
    const ADD_BUTTON_VIEW = {name: "Add", class:"addButton btn btn-primary btn-sm"}
    const CANCEL_BUTTON_VIEW = {name:"Cancel", class:"addButton btn btn-secondary btn-sm"}
    const DEFAULT_START_POINT = {row:DEFAULT_VALUE, col:DEFAULT_VALUE}
    const IMG_SOURCES_MAP =  new Map([["empty", "../images/empty.png"],["noShip","../images/noShip.png"],
        ["submarineCell","../images/submarineCell.png"]])
    const changeButtonView = (btnElement, newClassName, newText)=>{
        btnElement.innerText = newText;
        btnElement.className = newClassName;
    }

    class GameBoard{
        #BOARD_SIZE = 10
        matrix = []
        submarinePlaces = new Map()
        #controller

        constructor(controller) {
            for (let row = 0; row < this.#BOARD_SIZE; row++) {
                this.matrix.push([])
                for (let col = 0; col < this.#BOARD_SIZE; col++) {
                    this.matrix[row].push(document.getElementById(`${row}.${col}`));
                    this.matrix[row][col].addEventListener("click", (event)=>{this.#handleBoardClick(row,col)})
                }
            }
            console.log(this.matrix)
            this.#controller = controller;
        }

        #handleBoardClick(row,col){
            //means that we are adding a new submarine
            if (this.#controller.getSize() !== DEFAULT_VALUE){
                let imageElement = document.getElementById(`image_${row}.${col}`)
                console.log(imageElement)
                let src = imageElement.getAttribute("src")
                if (".." + src === IMG_SOURCES_MAP.get("empty")){
                    //It's the first choice of the user
                    if (this.#controller.getIndex() === DEFAULT_START_POINT){
                        imageElement.setAttribute("src",IMG_SOURCES_MAP.get("submarineCell"))
                        this.#controller.setCurrentStartIndex({row: row, col:col})
                        //need to add here size = 1 case
                    }
                    //It's the last index of the current submarine
                    else{
                        const {row:firstRow, col:firstCol} = this.#controller.getIndex()
                        const submarineSize = this.#controller.getSize()
                        if(this.#isValidSubmarine(firstRow,firstCol, row, col, submarineSize)){
                            this.#displaySubmarine(firstRow, firstCol,row, col);
                            let haveElem = document.getElementById(`have_${submarineSize}`)
                            let neededElem = document.getElementById(`needed_${submarineSize}`)
                            haveElem.innerText = `${+(haveElem.innerText) + 1}`
                            let addBtn = document.getElementById("addButton_" + submarineSize);
                            changeButtonView(addBtn, ADD_BUTTON_VIEW.class, ADD_BUTTON_VIEW.name);
                            if (haveElem.innerText === neededElem.innerText){
                                addBtn.setAttribute("disabled", "")
                            }
                            //add submarine
                            //reset controller
                        }
                        //"invalid choice"
                        else{}
                    }
                }
            }
            //clicked on submarine (no ship should be invalid)
            else{
                //check if the "delete" button is clicked, if not - this is an error.
            }
        }

        #isValidSubmarine(firstRow, firstCol, lastRow, lastCol, size){
            if((firstRow === lastRow && (Math.abs(firstCol- lastCol )+ 1) === size) ||
                (firstCol === lastCol && (Math.abs(firstRow-lastRow ) + 1)=== size)){
                let [goingUp, goingDown] = [firstRow > lastRow, firstRow < lastRow];
                let [goingLeft, goingRight] = [firstCol > lastCol, firstCol < lastCol];
                console.log(firstRow, firstCol, lastRow, lastCol, goingUp, goingDown, goingLeft, goingRight)
                console.log(Math.abs(firstCol- lastCol )+ 1, Math.abs(firstRow-lastRow ) + 1, size)
                if (goingUp){
                    console.log("going up")
                    for (let row = firstRow - 1; row>= lastRow; row--) {
                        console.log(row, firstRow, lastRow)
                        let imageElement = document.getElementById(`image_${row}.${lastCol}`)
                        if ((".." + imageElement.getAttribute("src")) !== IMG_SOURCES_MAP.get("empty")) {
                            console.log("failed in " + row +" "+lastCol);
                            console.log(this.matrix[row][lastCol].getAttribute("src"))
                            console.log(IMG_SOURCES_MAP.get("empty"))
                            return false;
                        }
                    }
                }
                else if (goingDown){
                    console.log("going down")
                    for (let row = firstRow + 1; row <= lastRow; row++) {
                        console.log(row, firstRow, lastRow)
                        let imageElement = document.getElementById(`image_${row}.${lastCol}`)
                        if ((".." + imageElement.getAttribute("src")) !== IMG_SOURCES_MAP.get("empty")) {
                            console.log("failed in " + row +" "+lastCol);
                            console.log(this.matrix[row][lastCol].getAttribute("src"))
                            console.log(IMG_SOURCES_MAP.get("empty"))
                            return false;
                        }
                    }
                }
                else if (goingLeft){
                    for (let col = firstCol - 1; col >= lastCol; col--) {
                        console.log(col, firstCol, lastCol)
                        let imageElement = document.getElementById(`image_${firstRow}.${col}`)
                        if ((".." + imageElement.getAttribute("src")) !== IMG_SOURCES_MAP.get("empty")) {
                            console.log("failed in " + firstRow +" "+col);
                            console.log(this.matrix[firstRow][col])
                            console.log(this.matrix[firstRow][col].getAttribute("src"))
                            console.log(IMG_SOURCES_MAP.get("empty"))
                            return false;
                        }
                    }
                }
                else if (goingRight){
                    console.log("going right")
                    for (let col = firstCol + 1; col <= lastCol; col ++) {
                        console.log(col, firstCol, lastCol)
                        let imageElement = document.getElementById(`image_${firstRow}.${col}`)
                        if ((".." + imageElement.getAttribute("src")) !== IMG_SOURCES_MAP.get("empty")) {
                            console.log("failed in " + firstRow +" "+col);
                            console.log(this.matrix[firstRow][col].getAttribute("src"))
                            console.log(IMG_SOURCES_MAP.get("empty"))
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }

        #displaySubmarine(firstRow, firstCol, lastRow, lastCol){
            console.log("display")
            let [goingUp, goingDown] = [firstRow > lastRow, firstRow < lastRow];
            let [goingLeft, goingRight] = [firstCol > lastCol, firstCol < lastCol];
            if (goingUp || goingDown){
                for (let row = Math.max(((goingUp)? lastRow: firstRow) - 1, 0);
                     row <= Math.min(((goingUp)? firstRow: lastRow)+1, this.#BOARD_SIZE-1);
                     row++){
                    for(let col = Math.max(firstCol-1,0); col<=Math.min(firstCol+1, this.#BOARD_SIZE-1); col++){
                        let imageElement = document.getElementById(`image_${row}.${col}`)
                        console.log("firstCol = " + firstCol, " cuurent col = " + col);
                        console.log((goingUp)? "going up": "going down");
                        console.log("first row = "+ firstRow, "last row = " + lastRow, "current row = " + row);
                        console.log(((goingUp)? "is lastRow <= row && row <= firstRow = " + (lastRow <= row && row <= firstRow):
                            "is firstRow <= row && row <=lastRow = " + (firstRow <= row && row <=lastRow)))
                        if (firstCol === col && ((goingUp)? (lastRow <= row && row <= firstRow): (firstRow <= row && row <=lastRow))){
                            imageElement.setAttribute("src", IMG_SOURCES_MAP.get("submarineCell"))
                        }
                        else{
                            imageElement.setAttribute("src", IMG_SOURCES_MAP.get("noShip"))
                            this.matrix[row][col].setAttribute("disabled","")
                        }
                    }
                }
            }
            else if (goingLeft|| goingRight){
                for(let row = Math.max(firstRow-1,0); row<=Math.min(firstRow+1, this.#BOARD_SIZE-1); row++){
                    for (let col = Math.max(((goingLeft)? lastCol: firstCol) - 1, 0);
                         col <= Math.min(((goingLeft)? firstCol: lastCol)+1, this.#BOARD_SIZE-1);
                         col++){
                        let imageElement = document.getElementById(`image_${row}.${col}`)
                        if (firstRow === row && ((goingLeft)?( lastCol <= col && col<= firstCol): (firstCol <= col && col <=lastCol))){
                            imageElement.setAttribute("src", IMG_SOURCES_MAP.get("submarineCell"))
                        }
                        else{
                            imageElement.setAttribute("src", IMG_SOURCES_MAP.get("noShip"))
                            this.matrix[row][col].setAttribute("disabled","")
                        }
                    }
                }
            }
        }

    }
    class Controller{
        #board = new GameBoard(this);
        #currentSize = DEFAULT_VALUE
        #currentStartIndex = DEFAULT_START_POINT;

        setCurrentSize (size){
            if (this.#currentSize!== DEFAULT_VALUE){
                let lastBtn = document.getElementById("addButton_" + this.#currentSize.toString())
                if (lastBtn && lastBtn.innerText === CANCEL_BUTTON_VIEW.name){
                    changeButtonView(lastBtn, ADD_BUTTON_VIEW.class, ADD_BUTTON_VIEW.name)
                }
            }
            this.#currentSize = size
            this.setCurrentStartIndex(DEFAULT_START_POINT);
        }

        setCurrentStartIndex(newIndex){
            this.#currentStartIndex = newIndex
        }

        getSize(){
            return this.#currentSize;
        }

        getIndex(){
            return this.#currentStartIndex;
        }
    }

    const handleAddClick = (event, btn, controller)=>{
        let isAdd = btn.innerHTML === ADD_BUTTON_VIEW.name
        if (isAdd){
            changeButtonView(btn, CANCEL_BUTTON_VIEW.class, CANCEL_BUTTON_VIEW.name)
            controller.setCurrentSize(+(btn.id.split("_")[1]))
        }
        else{
            changeButtonView(btn, ADD_BUTTON_VIEW.class, ADD_BUTTON_VIEW.name)
            controller.setCurrentSize(DEFAULT_VALUE)
        }
    }


    document.addEventListener("DOMContentLoaded",()=>{
        let controller = new Controller();
        let addButtons = document.querySelectorAll(".addButton");
        addButtons.forEach((btn)=>{
            btn.addEventListener("click", (event)=>{handleAddClick(event,btn, controller)});
        })
    })
})();