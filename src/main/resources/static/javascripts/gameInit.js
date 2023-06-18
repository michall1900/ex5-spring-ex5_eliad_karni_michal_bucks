(function(){
    const DEFAULT_VALUE = -1
    const ADD_BUTTON_VIEW = {name: "Add", class:"addButton btn btn-primary btn-sm"}
    const CANCEL_BUTTON_VIEW = {name:"Cancel", class:"addButton btn btn-secondary btn-sm"}
    const DEFAULT_START_POINT = {row:DEFAULT_VALUE, col:DEFAULT_VALUE}
    const IMG_SOURCES_MAP =  new Map([["empty", "../images/empty.png"],["noShip","../images/noShip.png"],
        ["submarineCell","../images/submarineCell.png"]])
    const BOARD_SIZE = 10
    const MIN_SIZE = 1
    const ADD_NOT_CLICKED = "You need first click on one of the 'Add' buttons to select submarine."
    const HAVE_NO_BOARDS_TO_DISPLAY = "You already display all the submarines. Please click on ready button or delete one of the submarine to put it in another place."
    let isDeletePressed = false;

    const changeButtonView = (btnElement, newClassName, newText)=>{
        btnElement.innerText = newText;
        btnElement.className = newClassName;
    }

    const getSrcToCompare= (imageElement)=>{
        let src = imageElement.getAttribute("src")
        return (src.startsWith("..")? src: `..${src}`);
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

        displaySubmarine(lastListenerFunction){
            for(let row = Math.max(this.#firstIndex.row-1,0);
                row<=Math.min(this.#lastIndex.row +1, BOARD_SIZE-1);
                row++){
                for (let col = Math.max(this.#firstIndex.col - 1, 0);
                     col <= Math.min(this.#lastIndex.col +1, BOARD_SIZE-1);
                     col++){
                    const relevantButton = document.getElementById(`${row}.${col}`)
                    let imageElement = document.getElementById(`image_${row}.${col}`)
                    if ((this.#isVertical?
                        (col=== this.#firstIndex.col && this.#firstIndex.row<= row && row<= this.#lastIndex.row):
                        (row === this.#firstIndex.row && this.#firstIndex.col<= col && col<= this.#lastIndex.col))){
                        imageElement.setAttribute("src", IMG_SOURCES_MAP.get("submarineCell"))
                        relevantButton.removeEventListener("click",lastListenerFunction)
                        relevantButton.addEventListener("click", (event) =>{this.#handleDelete(lastListenerFunction)})
                    }
                    else{
                        imageElement.setAttribute("src", IMG_SOURCES_MAP.get("noShip"))
                        relevantButton.setAttribute("disabled","")
                        relevantButton.removeEventListener("click",lastListenerFunction)
                    }
                    this.#relevantElements.push({btn: relevantButton, img:imageElement, row:row, col:col});


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

        #handleDelete(lastListenerFunction){
            if (!isDeletePressed)
                //displayErrorMessage
                ;
            else{
                this.#relevantElements.forEach(({btn,img, row, col})=>{
                    img.setAttribute("src", IMG_SOURCES_MAP.get("empty"))
                    btn.removeAttribute("disabled")
                    btn.removeEventListener("click", this.#handleDelete)
                    btn.addEventListener("click", (_)=>lastListenerFunction(row, col))
                })
            }
        }
    }

    class Controller{
        #currentSize = DEFAULT_VALUE
        #currentStartIndex = DEFAULT_START_POINT;
        #boardMatrix = []
        constructor() {
            for (let row = 0; row < BOARD_SIZE; row++) {
                this.#boardMatrix.push([])
                for (let col = 0; col < BOARD_SIZE; col++) {
                    this.#boardMatrix[row].push(document.getElementById(`${row}.${col}`));
                    this.#boardMatrix[row][col].addEventListener("click", (_)=>{this.#handleBoardClick(row,col)})
                }
            }
        }
        #handleBoardClick(row, col){
            try{
                this.#handleSubmarineChoice(row, col)
            }
            catch (e){
                //should display the error message somewhere
                console.log(e)
            }
            // if (this.#currentSize !== DEFAULT_VALUE){
            //     let imageElement = document.getElementById(`image_${row}.${col}`)
            //     let srcToCompute = getSrcToCompare(imageElement)
            //     if (srcToCompute === IMG_SOURCES_MAP.get("empty")){
            //         //It's the first choice of the user
            //         if (this.#currentStartIndex === DEFAULT_START_POINT){
            //             imageElement.setAttribute("src",IMG_SOURCES_MAP.get("submarineCell"))
            //             this.#currentStartIndex = {row: row, col:col}
            //             if(this.#currentSize === MIN_SIZE){
            //                 //
            //             }
            //
            //
            //         }
            //         //It's the last index of the current submarine
            //         else{
            //             try{
            //                 const submarine = new Submarine(this.#currentStartIndex, {row:row, col:col},this.#currentSize)
            //                 //this.#handleChosenSubmarine(this.#controller.getIndex().row, this.#controller.getIndex().col,row, col);
            //                 let haveElem = document.getElementById(`have_${this.#currentSize}`)
            //                 let neededElem = document.getElementById(`needed_${this.#currentSize}`)
            //                 haveElem.innerText = `${+(haveElem.innerText) + 1}`
            //                 let addBtn = document.getElementById(`addButton_${this.#currentSize}`);
            //                 changeButtonView(addBtn, ADD_BUTTON_VIEW.class, ADD_BUTTON_VIEW.name);
            //                 if (haveElem.innerText === neededElem.innerText){
            //                     addBtn.setAttribute("disabled", "")
            //                 }
            //                 //add submarine
            //                 //reset controller
            //             }
            //             catch (e){
            //                 console.log(e)
            //             }
            //         }
            //     }
            // }
            // //clicked on submarine (noShip cells should be invalid)
            // else{
            //     //check if the "delete" button is clicked, if not - this is an error.
            // }
        }

        #handleSubmarineChoice(row, col){
            if (this.#currentSize === DEFAULT_VALUE)
                throw new Error(ADD_NOT_CLICKED) // should be change to - if (all the submarines displayed - display the other message)
            let imageElement = document.getElementById(`image_${row}.${col}`)
            let srcToCompute = getSrcToCompare(imageElement)
            if (srcToCompute === IMG_SOURCES_MAP.get("empty")){
                //It's the first choice of the user
                if (this.#currentStartIndex === DEFAULT_START_POINT){
                    imageElement.setAttribute("src",IMG_SOURCES_MAP.get("submarineCell"))
                    this.#currentStartIndex = {row: row, col:col}
                    if(this.#currentSize === MIN_SIZE){
                        this.#createNewSubmarine(row, col)
                    }
                }
                //It's the last index of the current submarine
                else{
                    this.#createNewSubmarine(row, col)
                }
            }
        }

        #createNewSubmarine(row, col){
            const submarine = new Submarine(this.#currentStartIndex, {row:row, col:col},this.#currentSize)
            submarine.displaySubmarine()
            //this.#handleChosenSubmarine(this.#controller.getIndex().row, this.#controller.getIndex().col,row, col);
            let haveElem = document.getElementById(`have_${this.#currentSize}`)
            let neededElem = document.getElementById(`needed_${this.#currentSize}`)
            haveElem.innerText = `${+(haveElem.innerText) + 1}`
            let addBtn = document.getElementById(`addButton_${this.#currentSize}`);
            changeButtonView(addBtn, ADD_BUTTON_VIEW.class, ADD_BUTTON_VIEW.name);
            if (haveElem.innerText === neededElem.innerText){
                addBtn.setAttribute("disabled", "")
            }
            //add submarine
            //reset controller
        }

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