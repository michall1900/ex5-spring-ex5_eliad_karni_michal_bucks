(function (){
    // The id of the room's player table element.
    const ROOM_PLAYER_TABLE_BODY_ID = "room-player-body-table-body";
    // The element of the room's player table.
    let roomPlayersTableBodyElement;
    // The amount of seconds between each update fetch.
    let UPDATE_RATE = 1;
    let badResponseCounter = 0;

    document.addEventListener("DOMContentLoaded", ()=>{
        // Init the room's player table element.
        roomPlayersTableBodyElement = document.getElementById(ROOM_PLAYER_TABLE_BODY_ID);
        // Starting the polling recursion.
        subscribe();
    });

    /**
     * The function validates the server's response.
     * In case the server's response is invalid, the page is redirected to the lobby.
     * @param roomJson The server's response.
     */
    function checkAnswer(roomJson){
        if( ! roomJson.hasOwnProperty("players")){
            window.location.href = "/lobby";
        }
    }

    /**
     * The function checks if the server's response indicates that the game has started.
     * @param roomJson The server's response.
     */
    function checkGameStarted(roomJson){
        if(roomJson.hasOwnProperty("start_game")) {
            console.log("room is full");
            window.location.href = "/game/init";
        }
    }

    /**
     * The function fills the room's player table with the players that are in the room
     * (This is important in case the game will support more then 2 players game mode).
     * @param roomsJson The server response.
     */
    function setRoomTable(roomsJson){
        roomPlayersTableBodyElement.innerHTML = "";
        JSON.parse(roomsJson.players).forEach((player) => {
            roomPlayersTableBodyElement.innerHTML += `
                <tr class="table-secondary">
                    <td>${player}</td>
                </tr>`;
        })
    }

    /**
     * The function recursively manages the polling between the page and the server.
     * @returns {Promise<void>} Has no value.
     */
    async function subscribe() {
        try {
            let response = await fetch("/lobby/getRoom");
            if (response.status === 502) {
                // Status 502 is a connection timeout error,
                // may happen when the connection was pending for too long,
                // and the remote server or a proxy closed it
                // let's reconnect
                await new Promise(resolve => setTimeout(resolve, 1000 * UPDATE_RAT));
                await subscribe();
            } else if (response.status !== 200) {
                // An error - let's show it
                console.log(response.statusText);
                // Reconnect in one second
                await new Promise(resolve => setTimeout(resolve, 1000 * UPDATE_RAT));
                await subscribe();
            } else {
                // Get and show the message
                let message = await response.json();
                console.log(message);
                checkAnswer(message);
                setRoomTable(message);
                checkGameStarted(message);
                await new Promise(resolve => setTimeout(resolve, 1000 * UPDATE_RAT));
                await subscribe()
            }
        }catch (e){
            console.log(e);
            badResponseCounter++;
            console.log(badResponseCounter);
            if(badResponseCounter === 3){
                window.location.href = "/lobby";
            }
            await new Promise(resolve => setTimeout(resolve, 1000 * UPDATE_RAT));
            await subscribe()
        }
    }
})();