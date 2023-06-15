(function(){
    const DEFAULT_VALUE = -1
    class GameBoard{
        #BOARD_SIZE = 10
        matrix = []
        submarinePlaces = new Map()

        constructor() {
            for (let row = 0; row < this.#BOARD_SIZE; row++) {
                this.matrix.push([])
                for (let col = 0; col < this.#BOARD_SIZE; col++)
                    this.matrix[row].push(document.getElementById(`${row}.${col}`));
            }
            console.log(this.matrix)
        }
    }
    class Controller{
        #board = new GameBoard();
        #currentSize = DEFAULT_VALUE
        #currentStartIndex = {row:DEFAULT_VALUE, col:DEFAULT_VALUE};
        constructor() {
        }

        setCurrentSize (size){
            this.#currentSize = size
            this.setCurrentStartIndex({row:DEFAULT_VALUE, col:DEFAULT_VALUE});
        }

        setCurrentStartIndex(newIndex){
            this.#currentStartIndex = newIndex
        }
    }

    const handleAddClick = (event, btn, controller)=>{
        let isAdd = btn.innerHTML.toLowerCase() === "add"
        if (isAdd){
            btn.innerHTML = "Cancel";
            btn.className = "addButton btn btn-secondary btn-sm";
            controller.setCurrentSize(+(btn.id.split("_")[1]))
        }
        else{
            btn.innerHTML = "Add";
            btn.className = "addButton btn btn-primary btn-sm";
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