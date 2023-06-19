(function(){
    const DEFAULT_VALUE = -1
    const ADD_BUTTON_VIEW = {name: "Add", class:"addButton btn btn-primary btn-sm"}
    const DELETE_BUTTON_VIEW = {name:"Delete", class: "btn btn-danger btn-sm"}
    const CANCEL_BUTTON_VIEW = {name:"Cancel", class:"btn btn-secondary btn-sm"}
    const DEFAULT_START_POINT = {row:DEFAULT_VALUE, col:DEFAULT_VALUE}
    const IMG_SOURCES_MAP =  new Map([["empty", "../images/empty.png"],["noShip","../images/noShip.png"],
        ["submarineCell","../images/submarineCell.png"]])
    const BOARD_SIZE = 10
    const MIN_SIZE = 1
    const ADD_NOT_CLICKED_ERROR = "To select a submarine, you must first click an 'Add' button."
    const HAVE_NO_SUBMARINE_TO_DISPLAY = "All submarines are already displayed. Click 'Ready' or delete a submarine to relocate it."
    const YOU_SHOULD_CLICK_DELETE_FIRST_ERROR = "Click on a submarine. To delete it, click 'Delete' first."
    const YOU_CANT_CHOOSE_THIS_CELL_ERROR = "This location cannot be chosen to display the submarine."
    const EMPTY_ON_DELETE_ERROR = "You clicked on empty place. Please click on a submarine."
    const ADD_ON_DELETE_ERROR = "After clicking 'Delete,' you can't click 'Add'."
    const DELETE_ON_ADD_ERROR = "While adding a new submarine, you can't click 'Delete.'"
    const DELETE_ON_READY_ERROR = "While adding a new submarine, you can't click 'Delete.'"

    const INIT_INSTRUCTION = "Click 'Add' to create a new submarine, 'Delete' to remove an existing submarine, or 'Ready' once you've selected positions for all submarines."
    const AFTER_ADD_INSTRUCTION = "Choose the first cell on the game board as the first corner of the submarine, or click 'Cancel' to cancel the current selection."
    const AFTER_CHOOSING_FIRST_POS_INSTRUCTION = "Choose another cell on the game board as the other corner of the submarine. Ensure it matches the length of your submarine. Click \"Cancel\" or the marked cell to cancel the first corner placing."
    const AFTER_DELETE_CLICK_INSTRUCTION = "Click on the submarine you wish to delete."
    let DELETE_ELEMENT;
    let READY_ELEMENT;
    let INSTRUCTIONS_ELEMENT;
    let ERROR_ELEMENT;
    let isDeletePressed = false;
    let isAddPressed = false;


    const changeButtonView = (btnElement, newClassName, newText)=>{
        btnElement.innerText = newText;
        btnElement.className = newClassName;
    }

    const getSrcToCompare= (imageElement)=>{
        let src = imageElement.getAttribute("src")
        return (src.startsWith("..")? src: `..${src}`);
    }

    const handelSubmarineAddButton = (size, isIncrease)=>{
        let haveElem = document.getElementById(`have_${size}`)
        let neededElem = document.getElementById(`needed_${size}`)
        haveElem.innerText = `${+(haveElem.innerText) + ((isIncrease)? 1: -1)}`
        let addBtn = document.getElementById(`addButton_${size}`);
        changeButtonView(addBtn, ADD_BUTTON_VIEW.class, ADD_BUTTON_VIEW.name);
        isAddPressed = false;
        if (haveElem.innerText === neededElem.innerText)
            addBtn.setAttribute("disabled", "");
        else
            addBtn.removeAttribute("disabled");
    }

    class Submarine{
        INVALID_SUBMARINE = "The submarine you trying to insert is invalid.";
        #size
        #firstIndex
        #lastIndex
        #isVertical
        #relevantElements = []

        constructor(firstChosenIndex, lastChosenIndex,size) {
            this.#validateSubmarine(firstChosenIndex, lastChosenIndex, size);
            [this.#firstIndex, this.#lastIndex] = this.#getRealFirstAndLastIndex(firstChosenIndex, lastChosenIndex);
            this.#size = size;
            this.#isVertical = (firstChosenIndex.row > lastChosenIndex.row || firstChosenIndex.row < lastChosenIndex.row);
        }

        displaySubmarine(controller){
            for(let row = Math.max(this.#firstIndex.row-1,0);
                row<=Math.min(this.#lastIndex.row +1, BOARD_SIZE-1);
                row++){
                for (let col = Math.max(this.#firstIndex.col - 1, 0);
                     col <= Math.min(this.#lastIndex.col +1, BOARD_SIZE-1);
                     col++){
                    const relevantButton = document.getElementById(`${row}.${col}`)
                    let imageElement = document.getElementById(`image_${row}.${col}`)
                    let clonedButton
                    if ((this.#isVertical?
                        (col=== this.#firstIndex.col && this.#firstIndex.row<= row && row<= this.#lastIndex.row):
                        (row === this.#firstIndex.row && this.#firstIndex.col<= col && col<= this.#lastIndex.col))){
                        imageElement.setAttribute("src", IMG_SOURCES_MAP.get("submarineCell"))
                        clonedButton = relevantButton.cloneNode(true)
                        clonedButton.addEventListener("click", (_) =>{this.#handleDelete(controller)})
                        relevantButton.parentNode.replaceChild(clonedButton,relevantButton)
                    }
                    else{
                        imageElement.setAttribute("src", IMG_SOURCES_MAP.get("noShip"))
                        clonedButton = relevantButton.cloneNode(true)
                        clonedButton.setAttribute("disabled","")
                        relevantButton.parentNode.replaceChild(clonedButton,relevantButton)
                    }
                    this.#relevantElements.push({btn: clonedButton, img:document.getElementById(`image_${row}.${col}`),
                        row:row, col:col});


                }
            }
        }

        #validateSubmarine(firstChosenIndex, lastChosenIndex, size){
            if(!((firstChosenIndex.row === lastChosenIndex.row && (Math.abs(firstChosenIndex.col- lastChosenIndex.col )+ 1) === size) ||
                (firstChosenIndex.col === lastChosenIndex.col && (Math.abs(firstChosenIndex.row-lastChosenIndex.row ) + 1)=== size)))
                throw new Error (this.INVALID_SUBMARINE)
            if (size > MIN_SIZE){
                let isVertical = (firstChosenIndex.row > lastChosenIndex.row || firstChosenIndex.row < lastChosenIndex.row);
                let [realFirstIndex, realLastIndex] = this.#getRealFirstAndLastIndex(firstChosenIndex, lastChosenIndex);
                this.#checkIfSubmarineInEmptyCells(realFirstIndex, realLastIndex,firstChosenIndex,isVertical);
            }


        }

        #checkIfSubmarineInEmptyCells(firstIndex,lastIndex,firstChosenIndex, isVertical){
            for(let index = (isVertical? firstIndex.row: firstIndex.col);
                index < (isVertical? lastIndex.row: lastIndex.col); index++){
                const [currentRow, currentCol] = isVertical? [index, firstIndex.col]: [firstIndex.row, index];
                const imageElement = document.getElementById(`image_${currentRow}.${currentCol}`)
                const src = imageElement.getAttribute("src")
                const srcToCompute = src.startsWith("..")? src: `..${src}`;
                if ((currentRow!== firstChosenIndex.row || currentCol!== firstChosenIndex.col) &&
                    srcToCompute!== IMG_SOURCES_MAP.get("empty"))
                    throw new Error (this.INVALID_SUBMARINE);
            }
        }

        #getRealFirstAndLastIndex(firstChosenIndex, lastChosenIndex){
            return (firstChosenIndex.col < lastChosenIndex.col || firstChosenIndex.row < lastChosenIndex.row)?
                [firstChosenIndex, lastChosenIndex]: [lastChosenIndex, firstChosenIndex];
        }

        #handleDelete(controller){
            if (isAddPressed)
                console.log(YOU_CANT_CHOOSE_THIS_CELL_ERROR)
            else if (!isDeletePressed)
                console.log(YOU_SHOULD_CLICK_DELETE_FIRST_ERROR)//should be changed to displayErrorMessage
                ;
            else{
                this.#relevantElements.forEach(({btn,img, row, col})=>{
                    console.log(btn.parentNode)
                    img.setAttribute("src", IMG_SOURCES_MAP.get("empty"))
                    btn.removeAttribute("disabled")
                    const clonedBtn = btn.cloneNode(true)
                    clonedBtn.addEventListener("click", (_)=>controller.handleBoardClick(row, col))
                    btn.parentNode.replaceChild(clonedBtn, btn);
                })
                handelSubmarineAddButton(this.#size, false)
                controller.deleteSubmarineFromList(this.getFirstIndexString())
                changeButtonView(DELETE_ELEMENT, DELETE_BUTTON_VIEW.class, DELETE_BUTTON_VIEW.name)
                INSTRUCTIONS_ELEMENT.innerHTML = INIT_INSTRUCTION
                isDeletePressed = false
            }
        }

        getFirstIndexString(){
           return `${this.#firstIndex.row}.${this.#firstIndex.col}`
        }
    }

    class Controller{
        #currentSize = DEFAULT_VALUE
        #currentStartIndex = DEFAULT_START_POINT;
        #submarineMap = new Map();
        #numberOfNeededSubmarines = 0

        constructor() {
            for (let row = 0; row < BOARD_SIZE; row++) {
                for (let col = 0; col < BOARD_SIZE; col++) {
                    document.getElementById(`${row}.${col}`).addEventListener("click",
                        (_)=>{this.handleBoardClick(row,col)})
                }
            }
            document.querySelectorAll('[id^="needed_"]').forEach((elem)=>{
                console.log(elem)
                console.log(+(elem.innerText))
                this.#numberOfNeededSubmarines += (+(elem.innerText))
            })

        }
        handleBoardClick(row, col){
            try{
                this.#handleSubmarineChoice(row, col)
            }
            catch (e){
                //should display the error message somewhere
                console.log(e)
            }
        }

        #handleSubmarineChoice(row, col){
            if (isDeletePressed)
                throw new Error(EMPTY_ON_DELETE_ERROR)
            if (!isAddPressed)
                throw new Error(ADD_NOT_CLICKED_ERROR) // should be change to - if (all the submarines displayed - display the other message)
            let imageElement = document.getElementById(`image_${row}.${col}`)
            let srcToCompute = getSrcToCompare(imageElement)
            if (srcToCompute === IMG_SOURCES_MAP.get("empty")){
                this.#handleClickOnEmptyCell(imageElement, row, col)
            }
            else if(srcToCompute === IMG_SOURCES_MAP.get("submarineCell") &&
                row === this.#currentStartIndex.row && col === this.#currentStartIndex.col){
                imageElement.setAttribute("src", IMG_SOURCES_MAP.get("empty"))
                this.#setCurrentStartIndex(DEFAULT_START_POINT);
                INSTRUCTIONS_ELEMENT.innerHTML = AFTER_ADD_INSTRUCTION
            }
            else
                throw new Error(YOU_CANT_CHOOSE_THIS_CELL_ERROR)

        }
        #handleClickOnEmptyCell(imageElement, row, col){
            //It's the first choice of the user
            if (this.#currentStartIndex === DEFAULT_START_POINT){
                imageElement.setAttribute("src",IMG_SOURCES_MAP.get("submarineCell"))
                this.#currentStartIndex = {row: row, col:col}
                if(this.#currentSize === MIN_SIZE)
                    this.#createNewSubmarine(row, col)
                else
                    INSTRUCTIONS_ELEMENT.innerHTML = AFTER_CHOOSING_FIRST_POS_INSTRUCTION
            }
            //It's the last index of the current submarine
            else
                this.#createNewSubmarine(row, col)
        }

        #createNewSubmarine(row, col){
            const submarine = new Submarine(this.#currentStartIndex, {row:row, col:col},this.#currentSize)
            submarine.displaySubmarine(this)
            handelSubmarineAddButton(this.#currentSize, true)
            this.setCurrentSize(DEFAULT_VALUE)
            this.#submarineMap.set(submarine.getFirstIndexString(), submarine);
            console.log(this.#numberOfNeededSubmarines)
            console.log(this.#submarineMap.size)
            if (this.#submarineMap.size === this.#numberOfNeededSubmarines) {
                console.log("READYYY")
            }
            INSTRUCTIONS_ELEMENT.innerHTML = INIT_INSTRUCTION
        }

        deleteSubmarineFromList(firstIndexString){
            this.#submarineMap.delete(firstIndexString);
            if (this.#submarineMap.size === this.#numberOfNeededSubmarines-1) {
                console.log("NOT READDDYYYY")
            }
        }
        setCurrentSize (size){
            if (this.#currentSize!== DEFAULT_VALUE){
                let lastBtn = document.getElementById("addButton_" + this.#currentSize.toString())
                if (lastBtn.innerText === CANCEL_BUTTON_VIEW.name){
                    changeButtonView(lastBtn, ADD_BUTTON_VIEW.class, ADD_BUTTON_VIEW.name)
                    isAddPressed = false;
                }
            }
            this.#currentSize = size
            this.#setCurrentStartIndex(DEFAULT_START_POINT);
        }

        #setCurrentStartIndex(newIndex){
            this.#currentStartIndex = newIndex
        }

        resetOnCancel(){
            if (this.#currentStartIndex!== DEFAULT_START_POINT)
                document.getElementById(`image_${this.#currentStartIndex.row}.${this.#currentStartIndex.col}`).setAttribute("src", IMG_SOURCES_MAP.get("empty"))
            this.setCurrentSize(DEFAULT_VALUE)
        }
    }

    const handleAddClick = (event, btn, controller)=>{
        if (isDeletePressed)
            console.log(DELETE_ON_ADD_ERROR)
        else{
            if (!isAddPressed){
                changeButtonView(btn, CANCEL_BUTTON_VIEW.class, CANCEL_BUTTON_VIEW.name)
                controller.setCurrentSize(+(btn.id.split("_")[1]))
                INSTRUCTIONS_ELEMENT.innerHTML = AFTER_ADD_INSTRUCTION
            }
            else{
                changeButtonView(btn, ADD_BUTTON_VIEW.class, ADD_BUTTON_VIEW.name)
                controller.resetOnCancel()
                INSTRUCTIONS_ELEMENT.innerHTML = INIT_INSTRUCTION
            }
            isAddPressed=!isAddPressed;
        }

    }
    const handleDeleteClick = (event)=>{
        if (isAddPressed)
            console.log(ADD_ON_DELETE_ERROR)
        else{
            if (!isDeletePressed){
                changeButtonView(DELETE_ELEMENT, CANCEL_BUTTON_VIEW.class, CANCEL_BUTTON_VIEW.name)
                INSTRUCTIONS_ELEMENT.innerHTML = AFTER_DELETE_CLICK_INSTRUCTION
            }
            else{
                changeButtonView(DELETE_ELEMENT, DELETE_BUTTON_VIEW.class, DELETE_BUTTON_VIEW.name)
                INSTRUCTIONS_ELEMENT.innerHTML = INIT_INSTRUCTION
            }
            isDeletePressed= !isDeletePressed
        }

    }

    document.addEventListener("DOMContentLoaded",()=>{
        let controller = new Controller();
        let addButtons = document.querySelectorAll(".addButton");
        addButtons.forEach((btn)=>{
            btn.addEventListener("click", (event)=>{handleAddClick(event,btn, controller)});
        })
        INSTRUCTIONS_ELEMENT = document.getElementById("instructions")
        INSTRUCTIONS_ELEMENT.innerHTML = INIT_INSTRUCTION
        DELETE_ELEMENT = document.getElementById("delete")
        DELETE_ELEMENT.addEventListener("click",handleDeleteClick)
    })
})();