(function (){
    // The id of the room's player table element.
    const ROOM_PLAYER_TABLE_BODY_ID = "room-player-body-table-body";
    // The element of the room's player table.
    let roomPlayersTableBodyElement;
    // The amount of seconds between each update fetch.
    let UPDATE_RATE = 1;
    // The variable counts the strike of the bad response from the server.
    let badResponseCounter = 0;
    // The max strike of bad response from the server. when reached, the page moves to lobby.
    const MAX_BAD_RESPONSE = 10;

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
                await new Promise(resolve => setTimeout(resolve, 1000 * UPDATE_RATE));
                await subscribe();
            } else if (response.status !== 200) {
                // An error - let's show it
                // Reconnect in one second
                await new Promise(resolve => setTimeout(resolve, 1000 * UPDATE_RATE));
                await subscribe();
            } else {
                // Get and show the message
                let message = await response.json();
                checkAnswer(message);
                setRoomTable(message);
                checkGameStarted(message);
                await new Promise(resolve => setTimeout(resolve, 1000 * UPDATE_RATE));
                await subscribe()
            }
        }catch (e){
            badResponseCounter++;
            if(badResponseCounter === MAX_BAD_RESPONSE){
                window.location.href = "/lobby";
            }
            await new Promise(resolve => setTimeout(resolve, 1000 * UPDATE_RATE));
            await subscribe()
        }
    }
})();